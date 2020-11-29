package com.pet001kambala.namopsfleetmanager.model

import androidx.databinding.BaseObservable

abstract class AbstractModel (
    open var id: String = "",
    var photoUrl: String? =  null
): BaseObservable(){
    class EntityExistException : Exception()
    open class NoEntityException : Exception()
    class PhoneVerificationCodeExpired : Exception()
    class InvalidPasswordEmailException : Exception()
    class InvalidPhoneAuthCodeException : Exception()
    class NoAccountException: NoEntityException()
    class EmailNotVerifiedException: Exception()
    class NoAuthException: java.lang.Exception()

    open fun data() = arrayListOf<Pair<String,String?>>()
}