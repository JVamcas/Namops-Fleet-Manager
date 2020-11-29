package com.pet001kambala.namopsfleetmanager.model

import androidx.annotation.Keep
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.firestore.Exclude

@Keep
data class PhoneAuthCred(
    @Exclude @Transient override var id: String = "",
    var phoneAuthCredential: PhoneAuthCredential? = null,
    var verificationId: String? = null
) : AbstractModel(){
}