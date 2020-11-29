package com.pet001kambala.namopsfleetmanager.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.pet001kambala.namopsfleetmanager.model.Trailer
import com.pet001kambala.namopsfleetmanager.model.Vehicle
import com.pet001kambala.namopsfleetmanager.utils.Docs
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.toMap
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TrailerRepo {

    val DB = FirebaseFirestore.getInstance()

    suspend fun registerTrailer(trailer: Trailer): Results {
        return try {
            val docRef = DB.collection(Docs.TRAILER.name).document()
            trailer.id = docRef.id
            docRef.set(trailer).await()
            Results.Success<Trailer>(code = Results.Success.CODE.WRITE_SUCCESS)
        } catch (e: Exception) {
            Results.Error(e)
        }
    }
    suspend fun findTrailer(unitNumber: String): Results {
        return try {
            val shot =
                DB.collection(Docs.TRAILER.name).whereEqualTo("unitNumber", unitNumber).get()
                    .await()
            val data = shot.documents.mapNotNull { it.toObject(Trailer::class.java) }
            Results.Success<Trailer>(
                data = ArrayList(data),
                code = Results.Success.CODE.LOAD_SUCCESS
            )
        } catch (e: java.lang.Exception) {
            Results.Error(e)
        }
    }


    @ExperimentalCoroutinesApi
    suspend fun trailerChangeListener(): Flow<Results> = callbackFlow {

        val collection = DB.collection(Docs.TRAILER.name)
        try {//1. first load the trailers data
            val shot = collection.get().await()
            val data = shot.documents.mapNotNull { it.toObject(Trailer::class.java) }
            offer(
                Results.Success<Trailer>(
                    data = ArrayList(data),
                    code = Results.Success.CODE.LOAD_SUCCESS
                )
            )
        } catch (e: Exception) {
            offer(Results.Error(e))
        }
        //2.  then listen for document changes on the trailers collection
        val subscription = collection.addSnapshotListener { shot, error ->
            error?.let {
                offer(Results.Error(error))
            }
            shot?.apply {
                val data =
                    if (!this.isEmpty)
                        ArrayList(shot.documents.mapNotNull { it.toObject(Trailer::class.java) })
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

    suspend fun updateTrailerDetails(trailer: Trailer): Results {
        val docRef = DB.collection(Docs.TRAILER.name).document(trailer.id)
        return try {
            docRef.update(trailer.toMap()).await()
            Results.Success<Trailer>(code = Results.Success.CODE.UPDATE_SUCCESS)
        } catch (e: java.lang.Exception) {
            Results.Error(e)
        }
    }
}