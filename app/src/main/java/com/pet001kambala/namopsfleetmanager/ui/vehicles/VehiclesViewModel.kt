package com.pet001kambala.namopsfleetmanager.ui.vehicles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.pet001kambala.namopsfleetmanager.repository.VehicleRepo
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

class VehiclesViewModel : ViewModel() {

    private val vehicleRepo = VehicleRepo()

    @ExperimentalCoroutinesApi
    val vehiclesList = liveData {
        emit(Results.loading())// load state will be loading if loading
        try {
            vehicleRepo.vehicleChangeListener().collect {
                emit(it)
            }
        }catch (e:  Exception){ //on exception, Results.Error will be emitted
            emit(Results.Error(e))
        }
    }
}