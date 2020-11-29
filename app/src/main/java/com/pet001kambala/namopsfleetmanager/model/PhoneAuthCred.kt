package com.pet001kambala.namopsfleetmanager.model

import androidx.annotation.Keep
import com.google.firebase.auth.PhoneAuthCredential
@Keep
data class PhoneAuthCred(
    var phoneAuthCredential: PhoneAuthCredential? = null,
    var verificationId: String? = null
) : AbstractModel(){
}