package com.pet001kambala.namopsfleetmanager.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.pet001kambala.namopsfleetmanager.model.*
import com.pet001kambala.namopsfleetmanager.utils.Const
import com.pet001kambala.namopsfleetmanager.utils.DateUtil.Companion._24
import com.pet001kambala.namopsfleetmanager.utils.DateUtil.Companion.today
import com.pet001kambala.namopsfleetmanager.utils.Docs
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.extractDigit
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.toMap
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TyreRepo {

    private val DB = FirebaseFirestore.getInstance()

    suspend fun registerTyre(tyre: Tyre): Results {
        return try {
            val docRef = DB.collection(Docs.TYRES.name).document()
            tyre.apply {
                id = docRef.id
                accumulatedCost = purchasePrice
                currentThreadType = purchaseThreadType
                currentCondition = purchaseCondition
                currentThreadDepth = purchaseThreadDepth
            }
            docRef.set(tyre).await()
            Results.Success<Tyre>(code = Results.Success.CODE.WRITE_SUCCESS)
        } catch (e: Exception) {
            Results.Error(e)
        }
    }

    suspend fun registerTyreVendor(vendor: TyreVendor): Results {
        return try {
            val docRef = DB.collection(Docs.TYRE_VENDORS.name).document()
            vendor.id = docRef.id
            docRef.set(vendor).await()
            Results.Success<Tyre>(code = Results.Success.CODE.WRITE_SUCCESS)
        } catch (e: Exception) {
            Results.Error(e)
        }
    }

    @ExperimentalCoroutinesApi
    suspend fun tyreChangeListener(): Flow<Results> = callbackFlow {

        val collection = DB.collection(Docs.TYRES.name)
        try {//1. first load the tyre data
            val shot = collection.get().await()
            val data = shot.documents.mapNotNull { it.toObject(Tyre::class.java) }
            offer(
                Results.Success<Tyre>(
                    data = ArrayList(data),
                    code = Results.Success.CODE.LOAD_SUCCESS
                )
            )
        } catch (e: Exception) {
            offer(Results.Error(e))
        }
        //2.  then listen for document changes on the tyre collection
        val subscription = collection.addSnapshotListener { shot, error ->
            error?.let {
                offer(Results.Error(error))
            }
            shot?.apply {
                val data =
                    if (!this.isEmpty)
                        ArrayList(shot.documents.mapNotNull { it.toObject(Tyre::class.java) })
                    else null

                val results = Results.Success(
                    data = data,
                    code = Results.Success.CODE.LOAD_SUCCESS
                )
                offer(results)
            }
        }
        awaitClose { subscription.remove() }
    }

    suspend fun findTyre(sn: String): Results {
        return try {
            val shot = DB.collection(Docs.TYRES.name).whereEqualTo("serialNumber", sn).get().await()
            val data = shot.documents.mapNotNull { it.toObject(Tyre::class.java) }
            Results.Success<Tyre>(data = ArrayList(data), code = Results.Success.CODE.LOAD_SUCCESS)
        } catch (e: java.lang.Exception) {
            Results.Error(e)
        }
    }

    @InternalCoroutinesApi
    suspend fun mountTyre(tyre: Tyre, mountItem: TyreMountItem, trailer: Trailer? = null): Results {

        val tyreRef = DB.collection(Docs.TYRES.name).document(tyre.id)
        val usageRef = DB.collection(Docs.TYRE_MOUNT.name).document()
        mountItem.id = usageRef.id
        if (mountItem.isHorse()) {
            return try {
                DB.batch().apply {
                    update(
                        tyreRef, mapOf(
                            "mounted" to true,
                            "mountPosition" to mountItem.mountPosition,
                            "horseNo" to mountItem.vehicleNo,
                            "location" to "Mounted On Vehicle",
                            "reference_vehicle_km" to VehicleRepo().findVehicleOdometer(mountItem.vehicleNo!!)
                        )
                    )
                    set(usageRef, mountItem)
                }.commit().await()
                Results.Success<Tyre>(code = Results.Success.CODE.WRITE_SUCCESS)
            } catch (e: java.lang.Exception) {
                Results.Error(e)
            }
        } else  return mountTyreOntoTrailer(tyre, mountItem, trailer!!)
    }

    @InternalCoroutinesApi
    private suspend fun mountTyreOntoTrailer(
        tyre: Tyre,
        mountItem: TyreMountItem,
        trailer: Trailer
    ): Results {
        val trailerRef = DB.collection(Docs.TRAILER.name).document(trailer.id)
        val tyreRef = DB.collection(Docs.TYRES.name).document(tyre.id)
        val mountRef = DB.collection(Docs.TYRE_MOUNT.name).document()
        mountItem.id = mountRef.id

        return try {
            DB.batch().apply {
                update(
                    tyreRef,
                    mapOf(
                        "mounted" to true,
                        "mountPosition" to mountItem.mountPosition,
                        "trailerNo" to mountItem.vehicleNo,
                        "location" to "Mounted On Trailer",
                        "horseNo" to trailer.horseNo,//# of horse where trailer is mounted
                        "reference_vehicle_km" to if (trailer.mounted) VehicleRepo().findVehicleOdometer(
                            trailer.horseNo!!
                        ) else null
                    )
                )
                update(
                    trailerRef,
                    mapOf(
                        "mountedTyreIdList" to trailer.mountedTyreIdList.also { it.add(tyre.id) }
                    )
                )
                set(mountRef, mountItem)
            }.commit().await()
            Results.Success<Trailer>(code = Results.Success.CODE.WRITE_SUCCESS)
        } catch (e: java.lang.Exception) {
            Results.Error(e)
        }
    }

    suspend fun unMountTyre(tyre: Tyre, mountItem: TyreMountItem): Results {
        val tyreRef = DB.collection(Docs.TYRES.name).document(tyre.id)
        val mountRef = DB.collection(Docs.TYRE_MOUNT.name).document(mountItem.id)

        return try {
            DB.batch().apply {
                update(
                    tyreRef, mapOf(
                        "mounted" to false,
                        "mountPosition" to null,
                        "mountedOn" to null,
                        "location" to Const.defaultLocation,
                        "reference_vehicle_km" to null
                    )
                )
                update(
                    mountRef,
                    mapOf(
                        "unMountDate" to mountItem.unMountDate,
                        "unMountReason" to mountItem.unMountReason
                    )
                )
            }.commit().await()
            Results.Success<Tyre>(code = Results.Success.CODE.WRITE_SUCCESS)
        } catch (e: java.lang.Exception) {
            Results.Error(e)
        }
    }

    /***
     * Check whether the mount position on this horse or trailer is available for mount
     * @param unitNo unit number
     * @param mountPosition the position of interest
     * @return true if so else false
     */
    suspend fun isMountPositionAvailable(
        unitNo: String,
        mountPosition: String,
        isHorse: Boolean
    ): Boolean {
        val horseSearch = DB.collection(Docs.TYRES.name).whereEqualTo("horseNo", unitNo)
            .whereEqualTo("mountPosition", mountPosition)

        val trailerSearch = DB.collection(Docs.TYRES.name).whereEqualTo("trailerNo", unitNo)
            .whereEqualTo("mountPosition", mountPosition)
        return try {
            val shot =
                if (isHorse) horseSearch.get().await() else trailerSearch.get().await()
            shot.isEmpty
        } catch (e: java.lang.Exception) {
            false
        }
    }

    suspend fun recordTyreSurvey(recordedTyre: Tyre, survey: TyreSurveyItem): Results {
        return try {
            val docRef = DB.collection(Docs.TYRE_SURVEY.name).document()
            survey.id = docRef.id
            val tyreRef = DB.collection(Docs.TYRES.name).document(recordedTyre.id)
            DB.batch().apply {
                recordedTyre.currentThreadDepth = survey.depth
                recordedTyre.currentCondition = survey.currentCondition
                update(
                    tyreRef,
                    mapOf(
                        "currentThreadDepth" to survey.depth,
                        "currentCondition" to survey.currentCondition,
                        "currentThreadType" to survey.currentThreadType
                    )
                )
                set(docRef, survey)
            }.commit().await()
            Results.Success<TyreSurveyItem>(code = Results.Success.CODE.WRITE_SUCCESS)
        } catch (e: java.lang.Exception) {
            Results.Error(e)
        }
    }

    suspend fun updateTyreDetails(tyre: Tyre): Results {
        return try {
            DB.collection(Docs.TYRES.name)
                .document(tyre.id)
                .update(
                    mapOf(
                        Pair("brand", tyre.brand),
                        Pair("size", tyre.size),
                        Pair("purchaseCondition", tyre.purchaseCondition),
                        Pair("purchaseDate", tyre.purchaseDate),
                        Pair("purchasePrice", tyre.purchasePrice),
                        Pair("purchaseThreadType", tyre.currentThreadType),
                        Pair("purchaseThreadDepth", tyre.purchaseThreadDepth),
                        Pair("recommendedPressure", tyre.recommendedPressure)
                    )
                ).await()
            Results.Success<Tyre>(code = Results.Success.CODE.UPDATE_SUCCESS)
        } catch (e: java.lang.Exception) {
            Results.Error(e)
        }
    }

    /***
     * Listen for the changes to the [TyreMountItem] record and emit the changes
     */
    @ExperimentalCoroutinesApi
    fun mountChangeListener(sn: String): Flow<Results> = callbackFlow {
        val collection = DB.collection(Docs.TYRE_MOUNT.name).whereEqualTo("tyreSN", sn)
        //1. first load TyreMountItem records
        offer(loadTyreMountRecords(sn))
        //2.  then listen for document changes on the [TyreMountItem] collection
        val subscription = collection.addSnapshotListener { shot, error ->
            error?.let {
                offer(Results.Error(error))
            }
            shot?.apply {
                val data =
                    if (!this.isEmpty)
                        ArrayList(shot.documents.mapNotNull { it.toObject(TyreMountItem::class.java) })
                    else null

                val results = Results.Success(
                    data = data,
                    code = Results.Success.CODE.LOAD_SUCCESS
                )
                offer(results)
            }
        }
        awaitClose { subscription.remove() }
    }

    /***
     * Listen for the changes to the [TyreSurveyItem] record and emit the changes
     */
    @ExperimentalCoroutinesApi
    fun surveyChangeListener(sn: String): Flow<Results> = callbackFlow {
        val collection = DB.collection(Docs.TYRE_SURVEY.name).whereEqualTo("tyreSN", sn)
        //1. first load the TyreSurveyItem data
        offer(loadTyreSurveyRecords(sn))
        //2.  then listen for document changes on the [TyreSurveyItem] collection
        val subscription = collection.addSnapshotListener { shot, error ->
            error?.let {
                offer(Results.Error(error))
            }
            shot?.apply {
                val data =
                    if (!this.isEmpty)
                        ArrayList(shot.documents.mapNotNull { it.toObject(TyreSurveyItem::class.java) })
                    else null

                val results = Results.Success(
                    data = data,
                    code = Results.Success.CODE.LOAD_SUCCESS
                )
                offer(results)
            }
        }
        awaitClose { subscription.remove() }
    }

    suspend fun loadTyreSurveyRecords(sn: String): Results {
        val collection = DB.collection(Docs.TYRE_SURVEY.name).whereEqualTo("tyreSN", sn)
        return try {
            val shot = collection.get().await()
            val data = shot.documents.mapNotNull { it.toObject(TyreSurveyItem::class.java) }
            Results.Success<TyreSurveyItem>(
                data = ArrayList(data),
                code = Results.Success.CODE.LOAD_SUCCESS
            )
        } catch (e: Exception) {
            Results.Error(e)
        }
    }

    suspend fun loadTyreMountRecords(sn: String): Results {
        val collection = DB.collection(Docs.TYRE_MOUNT.name).whereEqualTo("tyreSN", sn)

        return try {//1. first load the TyreMountItem data
            val shot = collection.get().await()
            val data = shot.documents.mapNotNull { it.toObject(TyreMountItem::class.java) }
            Results.Success<TyreMountItem>(
                data = ArrayList(data),
                code = Results.Success.CODE.LOAD_SUCCESS
            )

        } catch (e: Exception) {
            Results.Error(e)
        }
    }

    suspend fun sendTyreForRepair(tyre: Tyre, tyreRepairItem: TyreRepairItem): Results {
        val tyreRef = DB.collection(Docs.TYRES.name).document(tyre.id)
        val repairRef = DB.collection(Docs.TYRE_REPAIR.name).document()
        tyreRepairItem.apply {
            id = repairRef.id
            tyreSN = tyre.serialNumber
            dateSent = today()._24()
        }

        return try {
            DB.batch().apply {
                update(
                    tyreRef, mapOf(
                        "location" to tyreRepairItem.repairVendor,
                        "currentThreadDepth" to tyreRepairItem.sentThreadDepth,
                        "currentThreadType" to tyreRepairItem.sentThreadType
                    )
                )
                set(repairRef, tyreRepairItem)
            }.commit().await()
            Results.Success<TyreRepairItem>(code = Results.Success.CODE.WRITE_SUCCESS)
        } catch (e: Exception) {
            Results.Error(e)
        }
    }

    suspend fun receiveTyreFromRepair(tyre: Tyre, tyreRepair: TyreRepairItem): Results {
        val tyreRef = DB.collection(Docs.TYRES.name).document(tyre.id)
        val repairRef = DB.collection(Docs.TYRE_REPAIR.name).document(tyreRepair.id)
        var curAccCost = extractDigit(tyre.accumulatedCost).toDouble()
        curAccCost += extractDigit(tyre.purchasePrice).toDouble()
        tyre.accumulatedCost = curAccCost.toString()
        return try {
            DB.batch().apply {
                update(
                    tyreRef,
                    mapOf(
                        "location" to Const.defaultLocation,
                        "currentThreadDepth" to tyreRepair.repairThreadDepth,
                        "currentThreadType" to tyreRepair.repairThreadType,
                        "accumulatedCost" to tyre.accumulatedCost
                    )
                )
                update(
                    repairRef,
                    mapOf(
                        "dateReturned" to today()._24(),
                        "repairCost" to tyre.purchasePrice,
                        "repairThreadDepth" to tyreRepair.repairThreadDepth,
                        "repairThreadType" to tyreRepair.repairThreadType
                    )
                )
            }.commit().await()
            Results.Success<TyreRepairItem>(code = Results.Success.CODE.WRITE_SUCCESS)
        } catch (e: java.lang.Exception) {
            Results.Error(e)
        }
    }

    /***
     * Find tyre sent for repair but not yet received from tyre repair vendor
     * @param sn tyre serial number
     * @return [Results] containing the [TyreRepairItem] if exist else data will be null
     */

    suspend fun findTyreRepairItem(sn: String): Results {
        val shot = DB.collection(Docs.TYRE_REPAIR.name)
            .whereEqualTo("tyreSN", sn)
            .whereEqualTo("dateReturned", null)
            .get().await()//tyre not returned from vendor

        return try {
            val data = shot.documents.mapNotNull { it.toObject(TyreRepairItem::class.java) }
            Results.Success<TyreRepairItem>(ArrayList(data), Results.Success.CODE.LOAD_SUCCESS)
        } catch (e: java.lang.Exception) {
            Results.Error(e)
        }
    }

    suspend fun findTyreMountItem(sn: String): Results {
        return try {
            val shot = DB.collection(Docs.TYRE_MOUNT.name)
                .whereEqualTo("tyreSN", sn)
                .whereEqualTo("unMountDate", null)
                .get().await()//tyre not returned from vendor

            val data = shot.documents.mapNotNull { it.toObject(TyreMountItem::class.java) }
            Results.Success<TyreMountItem>(ArrayList(data), Results.Success.CODE.LOAD_SUCCESS)
        } catch (e: java.lang.Exception) {
            Results.Error(e)
        }
    }


    suspend fun loadTyreRepairRecords(sn: String): Results {
        val repairRef = DB.collection(Docs.TYRE_REPAIR.name).whereEqualTo("tyreSN", sn)

        return try {
            val shot = repairRef.get().await()
            val docs = shot.documents.mapNotNull { it.toObject(TyreRepairItem::class.java) }
            Results.Success<TyreRepairItem>(
                ArrayList(docs),
                code = Results.Success.CODE.LOAD_SUCCESS
            )
        } catch (e: java.lang.Exception) {
            Results.Error(e)
        }
    }

    /***
     * Listen for the changes to the [TyreRepairItem] records and emit the changes
     */
    @ExperimentalCoroutinesApi
    fun repairChangeListener(sn: String): Flow<Results> = callbackFlow {
        val collection = DB.collection(Docs.TYRE_REPAIR.name).whereEqualTo("tyreSN", sn)
        //1. first load the [TyreRepairItem] data
        offer(loadTyreRepairRecords(sn))
        //2.  then listen for document changes on the [TyreRepairItem] collection
        val subscription = collection.addSnapshotListener { shot, error ->
            error?.let {
                offer(Results.Error(error))
            }
            shot?.apply {
                if (!this.isEmpty) {
                    val data = shot.documents.mapNotNull { it.toObject(TyreRepairItem::class.java) }
                    val results = Results.Success(
                        data = ArrayList(data),
                        code = Results.Success.CODE.LOAD_SUCCESS
                    )
                    offer(results)
                }
            }
        }
        awaitClose { subscription.remove() }
    }

    @ExperimentalCoroutinesApi
    suspend fun tyreVendorChangeListener(): Flow<Results> = callbackFlow {
        val collection = DB.collection(Docs.TYRE_VENDORS.name)
        offer(loadTyreVendorsList())

        val subscription = collection.addSnapshotListener { shot, error ->
            error?.let {
                offer(Results.Error(error))
            }
            shot?.apply {
                if (!this.isEmpty) {
                    val data = shot.documents.mapNotNull { it.toObject(TyreVendor::class.java) }
                    val results = Results.Success(
                        data = ArrayList(data),
                        code = Results.Success.CODE.LOAD_SUCCESS
                    )
                    offer(results)
                }
            }
        }
        awaitClose { subscription.remove() }
    }

    private suspend fun loadTyreVendorsList(): Results {
        val vendorRef = DB.collection(Docs.TYRE_VENDORS.name)

        return try {
            val shot = vendorRef.get().await()
            val docs = shot.documents.mapNotNull { it.toObject(TyreVendor::class.java) }
            Results.Success<TyreVendor>(ArrayList(docs), code = Results.Success.CODE.LOAD_SUCCESS)
        } catch (e: java.lang.Exception) {
            Results.Error(e)
        }
    }

    suspend fun updateTyreVendor(vendor: TyreVendor): Results {
        return try {
            DB.collection(Docs.TYRE_VENDORS.name)
                .document(vendor.id)
                .update(
                    vendor.toMap()
                ).await()
            Results.Success<TyreVendor>(code = Results.Success.CODE.UPDATE_SUCCESS)
        } catch (e: java.lang.Exception) {
            Results.Error(e)
        }
    }

    /***
     * Load all the records for this tyre  [TyreSurveyItem] [TyreMountItem] [TyreRepairItem]
     * @param sn serial number for the tyre
     * @return [List] of [Results]
     */
    suspend fun loadTyreRecords(sn: String) =
        coroutineScope {
            val deferredRecords = listOf(
                async { loadTyreSurveyRecords(sn) },
                async { loadTyreMountRecords(sn) },
                async { loadTyreRepairRecords(sn) }
            )
            deferredRecords.awaitAll()
        }
}