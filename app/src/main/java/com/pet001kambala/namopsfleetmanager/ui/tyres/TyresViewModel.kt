package com.pet001kambala.namopsfleetmanager.ui.tyres

import androidx.lifecycle.*
import com.pet001kambala.namopsfleetmanager.model.Account
import com.pet001kambala.namopsfleetmanager.repository.TyreRepo
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

class TyresViewModel : ViewModel() {

    private val tyreRepo = TyreRepo()
    lateinit var mountRecord: LiveData<Results>
    lateinit var surveyRecord: LiveData<Results>
    lateinit var repairRecords: LiveData<Results>

    @ExperimentalCoroutinesApi
    val tyreVendorsList = liveData {
        emit(Results.loading())
        try {
            tyreRepo.tyreVendorChangeListener().collect {
                emit(it)
            }
        } catch (e: java.lang.Exception) {
            emit(Results.Error(e))
        }
    }

    private val unitNo = MutableLiveData<String>()

    @ExperimentalCoroutinesApi
    val mountedTyreList = unitNo.switchMap { unitNo ->
        liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(Results.loading())
            try {
                tyreRepo.loadTyresOnUnit(unitNo).collect {
                    emit(it)
                }
            } catch (e: Exception) {
                Results.Error(e)
            }
        }
    }


    @ExperimentalCoroutinesApi
    val tyresList = liveData {
        emit(Results.loading())// load state will be loading if loading
        try {
            tyreRepo.tyreChangeListener().collect {
                emit(it)
            }
        } catch (e: Exception) { //on exception, Results.Error will be emitted
            emit(Results.Error(e))
        }
    }

    @ExperimentalCoroutinesApi
    fun loadTyreMountRecord(sn: String): LiveData<Results> {
        mountRecord = liveData {
            emit(Results.loading())
            try {
                tyreRepo.mountChangeListener(sn).collect {
                    emit(it)
                }
            } catch (e: Exception) {
                emit(Results.Error(e))
            }
        }
        return mountRecord
    }

    @ExperimentalCoroutinesApi
    fun loadTyreSurveyRecord(sn: String): LiveData<Results> {
        surveyRecord = liveData {
            emit(Results.loading())
            try {
                tyreRepo.surveyChangeListener(sn).collect {
                    emit(it)
                }
            } catch (e: Exception) {
                emit(Results.Error(e))
            }
        }
        return surveyRecord
    }

    @ExperimentalCoroutinesApi
    fun loadTyreRepairRecord(sn: String): LiveData<Results> {
        repairRecords = liveData {
            emit(Results.loading())
            try {
                tyreRepo.repairChangeListener(sn).collect {
                    emit(it)
                }
            } catch (e: Exception) {
                emit(Results.Error(e))
            }
        }
        return repairRecords
    }

    fun loadMountedTyres(unitNo: String) {
        if (unitNo != this.unitNo.value)
            this.unitNo.postValue(unitNo)

    }
}
