package com.pet001kambala.namopsfleetmanager.ui.trailer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.pet001kambala.namopsfleetmanager.repository.TrailerRepo
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import java.lang.Exception

class TrailerViewModel : ViewModel() {

    private val trailerRepo = TrailerRepo()



    @ExperimentalCoroutinesApi
    val trailersList = liveData {
        emit(Results.loading())
        try {
            trailerRepo.trailerChangeListener().collect {
                emit(it)
            }
        }
        catch (e: Exception){
            emit(Results.Error(e))
        }
    }
}