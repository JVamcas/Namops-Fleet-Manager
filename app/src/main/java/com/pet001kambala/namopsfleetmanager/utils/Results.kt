package com.pet001kambala.namopsfleetmanager.utils

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.FirebaseFirestoreException.Code.ALREADY_EXISTS
import com.google.firebase.firestore.FirebaseFirestoreException.Code.PERMISSION_DENIED
import com.pet001kambala.namopsfleetmanager.model.AbstractModel


/**
 * Represent results of an async operation
 */
sealed class Results {

    companion object {
        fun loading() = Loading
    }

    object Loading : Results()

    class Success<T : AbstractModel>(
        val data: ArrayList<T>? = null,
        val code: CODE
    ) : Results() {
        enum class CODE {
            WRITE_SUCCESS,
            UPDATE_SUCCESS,
            LOAD_SUCCESS,
            AUTH_SUCCESS,
            LOGOUT_SUCCESS,
            DELETE_SUCCESS,
            VERIFICATION_EMAIL_SENT,
            PASSWORD_RESET_LINK_SENT,
            PHONE_VERIFY_CODE_SENT,
            PHONE_VERIFY_SUCCESS
        }
    }

    class Error(error: Exception?) : Results() {
        enum class CODE {
            NETWORK,
            PERMISSION_DENIED,
            UNKNOWN,
            ENTITY_EXISTS,
            AUTH,
            NO_RECORD,
            NO_ACCOUNT,
            INVALID_AUTH_CODE,
            PHONE_VERIFICATION_CODE_EXPIRED,
            NO_SUCH_USER,
            DUPLICATE_ACCOUNT,
            INCORRECT_EMAIL_PASSWORD_COMBO,
            EMAIL_NOT_VERIFIED,
            NO_AUTH
        }

        val code: CODE = when (error) {
            is AbstractModel.EntityExistException -> CODE.ENTITY_EXISTS
            is FirebaseAuthInvalidUserException -> CODE.NO_SUCH_USER
            is AbstractModel.InvalidPhoneAuthCodeException -> CODE.INVALID_AUTH_CODE
            is FirebaseAuthUserCollisionException -> CODE.DUPLICATE_ACCOUNT
            is FirebaseAuthException -> CODE.AUTH
            is FirebaseNetworkException -> CODE.NETWORK
            is AbstractModel.NoEntityException -> CODE.NO_RECORD
            is AbstractModel.NoAccountException -> CODE.NO_ACCOUNT
            is AbstractModel.PhoneVerificationCodeExpired -> CODE.PHONE_VERIFICATION_CODE_EXPIRED
            is AbstractModel.InvalidPasswordEmailException -> CODE.INCORRECT_EMAIL_PASSWORD_COMBO
            is AbstractModel.NoAuthException -> CODE.NO_AUTH
            is AbstractModel.EmailNotVerifiedException -> CODE.EMAIL_NOT_VERIFIED
            is FirebaseFirestoreException -> {
                when (error.code) {
                    PERMISSION_DENIED -> CODE.PERMISSION_DENIED
                    ALREADY_EXISTS -> CODE.ENTITY_EXISTS
                    else -> CODE.UNKNOWN
                }
            }
            else -> CODE.UNKNOWN
        }
    }
}