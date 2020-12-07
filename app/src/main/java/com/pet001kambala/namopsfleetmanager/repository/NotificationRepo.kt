package com.pet001kambala.namopsfleetmanager.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.pet001kambala.namopsfleetmanager.model.Notification
import com.pet001kambala.namopsfleetmanager.utils.Docs
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.toMap
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class NotificationRepo {

    private val DB = FirebaseFirestore.getInstance()


    @ExperimentalCoroutinesApi
    suspend fun notificationChangeListener(): Flow<Results> = callbackFlow {

        val collection = DB.collection(Docs.NOTIFICATIONS.name).whereEqualTo("resolved",false)
        try {//1. first load the tyre data
            val shot = collection.get().await()
            val data = shot.documents.mapNotNull { it.toObject(Notification::class.java) }

            offer(
                Results.Success<Notification>(
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
                        shot.documents.mapNotNull { it.toObject(Notification::class.java) }
                    else arrayListOf()

                val results = Results.Success(
                    data = ArrayList(data),
                    code = Results.Success.CODE.LOAD_SUCCESS
                )
                offer(results)
            }
        }
        awaitClose { subscription.remove() }
    }

    suspend fun updateNotification(note: Notification): Results {
        return try {
            DB.collection(Docs.NOTIFICATIONS.name)
                .document(note.id)
                .update(
                    note.toMap()
                ).await()
            Results.Success<Notification>(code = Results.Success.CODE.UPDATE_SUCCESS)
        } catch (e: java.lang.Exception) {
            Results.Error(e)
        }
    }
}