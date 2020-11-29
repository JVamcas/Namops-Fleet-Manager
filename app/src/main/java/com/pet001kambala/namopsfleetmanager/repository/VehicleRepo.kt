package com.pet001kambala.namopsfleetmanager.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.pet001kambala.namopsfleetmanager.model.Vehicle
import com.pet001kambala.namopsfleetmanager.utils.Docs
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.toMap
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.commons.csv.CSVFormat
import java.io.StringReader
import kotlin.math.round

class VehicleRepo {

    private val DB = FirebaseFirestore.getInstance()

    suspend fun registerVehicle(vehicle: Vehicle): Results {
        return try {
            val docRef = DB.collection(Docs.VEHICLES.name).document()
            vehicle.id = docRef.id
            docRef.set(vehicle).await()
            Results.Success<Vehicle>(code = Results.Success.CODE.WRITE_SUCCESS)
        } catch (e: Exception) {
            Results.Error(e)
        }
    }

    @ExperimentalCoroutinesApi
    suspend fun vehicleChangeListener(): Flow<Results> = callbackFlow {

        val collection = DB.collection(Docs.VEHICLES.name)
        try {//1. first load the data
            val shot = collection.get().await()
            val data = shot.documents.mapNotNull { it.toObject(Vehicle::class.java) }
            offer(
                Results.Success<Vehicle>(
                    data = ArrayList(data),
                    code = Results.Success.CODE.LOAD_SUCCESS
                )
            )
        } catch (e: Exception) {
            offer(Results.Error(e))
        }
        //2.  then listen for document changes on the vehicle collection
        val subscription = collection.addSnapshotListener { shot, error ->
            error?.let {
                offer(Results.Error(error))
            }
            shot?.apply {
                val data =
                    if (!this.isEmpty)
                        ArrayList(shot.documents.mapNotNull { it.toObject(Vehicle::class.java) })
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

    suspend fun findVehicle(unitNumber: String): Results {
        return try {
            val shot =
                DB.collection(Docs.VEHICLES.name).whereEqualTo("unitNumber", unitNumber).get()
                    .await()
            val data = shot.documents.mapNotNull { it.toObject(Vehicle::class.java) }
            Results.Success<Vehicle>(
                data = ArrayList(data),
                code = Results.Success.CODE.LOAD_SUCCESS
            )
        } catch (e: java.lang.Exception) {
            Results.Error(e)
        }
    }

    suspend fun updateVehicleDetails(vehicle: Vehicle): Results {
        val docRef = DB.collection(Docs.VEHICLES.name).document(vehicle.id)
        return try {
            docRef.update(vehicle.toMap()).await()
            Results.Success<Vehicle>(code = Results.Success.CODE.UPDATE_SUCCESS)
        } catch (e: java.lang.Exception) {
            Results.Error(e)
        }
    }

    /***
     * Find the odometer reading for this vehicle from webfleet
     * @param vehicleNo for the vehicle in question
     * @return odometer reading else null
     */
    @InternalCoroutinesApi
    suspend fun findVehicleOdometer(vehicleNo: String): String {
        val url = "https://csv.telematics.tomtom.com/extern?" +
                "account=namops&username=Rauna&password=3Mili2,87&" +
                "apikey=0e7ddb3b-65b0-4991-82a9-1c5c6f312317&lang=en&action=showObjectReportExtern"
        val client = OkHttpClient.Builder().build()
        val request = Request.Builder()
            .url(url)
            .build()

        return try {
            withContext(Dispatchers.IO) {
                val results = client.newCall(request).execute()//wait for the results from webfleet
                val data = results.body?.string()
                val csvParser = CSVFormat.newFormat(';').withQuote('"').parse(StringReader(data))
                val vehicleRecord = csvParser.records.filter { it[0] == vehicleNo }
                (round(vehicleRecord.first()[30].toDouble()/10.0)).toString()// vehicle odometer reading
            }
        } catch (e: java.lang.Exception) {
            "0.0"
        }
    }
}