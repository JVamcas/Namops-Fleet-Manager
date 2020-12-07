package com.pet001kambala.namopsfleetmanager.model

import androidx.annotation.Keep
import com.google.firebase.firestore.Exclude


@Keep
class Notification : AbstractModel() {

    @get: Exclude
    @Transient
    override var photoUrl: String? = null
    var type: String? = null
    var title: String? = null
    var body: String? = null
    var tyreSN: String? = null
    var resolved: Boolean = false
    var date: String? = null
}