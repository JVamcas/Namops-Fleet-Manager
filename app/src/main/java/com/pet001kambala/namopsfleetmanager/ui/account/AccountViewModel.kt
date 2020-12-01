package com.pet001kambala.namopsfleetmanager.ui.account

import androidx.lifecycle.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pet001kambala.namopsfleetmanager.model.Account
import com.pet001kambala.namopsfleetmanager.repository.AccountRepo
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.tasks.await

class AccountViewModel : ViewModel() {


    private val accountRepo = AccountRepo()

    enum class AuthState {
        AUTHENTICATED, UNAUTHENTICATED, EMAIL_NOT_VERIFIED, ACCOUNT_INFO_MISSING
    }

    private val userId = MutableLiveData<String>()

    @ExperimentalCoroutinesApi
    val currentAccount = userId.switchMap { userId ->
        liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            accountRepo.accountChangeListener(userId).collect {
                if (it is Results.Success<*> && !it.data.isNullOrEmpty())
                    emit(it.data.first() as Account)
            }
        }
    }

    @ExperimentalCoroutinesApi
    val accountList = liveData {
        emit(Results.loading())
        try {
            accountRepo.accountsChangeListener().collect{
                emit(it)
            }
        }
        catch (e: Exception){
            emit(Results.Error(e))
        }
    }

    @ExperimentalCoroutinesApi
    val authState = liveData {
        val userTask = Firebase.auth
        userTask.currentUser?.reload()?.await() //reload currently logged in user
        userTask.currentUser?.apply {
            if (isEmailVerified)
                userId.postValue(userTask.currentUser!!.uid)//trigger data load via transformation switchMap
        }
        accountRepo.listenForAuthChange().collect {
            when (it) {
                is Results.Success<*> -> {
                    userId.postValue(userTask.currentUser!!.uid)//trigger data load via transformation switchMap
                    emit(AuthState.AUTHENTICATED)
                }
                else -> {
                    val code = (it as Results.Error).code
                    if (code == Results.Error.CODE.EMAIL_NOT_VERIFIED)
                        emit(AuthState.EMAIL_NOT_VERIFIED)
                    else emit(AuthState.UNAUTHENTICATED)
                }
            }
        }
    }

    fun getAuthType():String?{
        return Firebase.auth.currentUser?.providerData?.get(1)?.providerId
    }

    fun signOut(){
        Firebase.auth.signOut()
    }
}