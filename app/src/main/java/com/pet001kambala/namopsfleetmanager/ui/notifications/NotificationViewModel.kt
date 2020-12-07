package com.pet001kambala.namopsfleetmanager.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.pet001kambala.namopsfleetmanager.repository.NotificationRepo
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

class NotificationViewModel: ViewModel() {

    private val notificationRepo = NotificationRepo()

    @ExperimentalCoroutinesApi
    val notificationsList = liveData {
        emit(Results.loading())
        try {
            notificationRepo.notificationChangeListener().collect {
                emit(it)
            }
        } catch (e: Exception) {
            emit(Results.Error(e))
        }
    }
}