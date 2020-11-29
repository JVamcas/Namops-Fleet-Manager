package com.pet001kambala.namopsfleetmanager.model

import androidx.annotation.Keep
import androidx.databinding.Bindable
import com.google.firebase.firestore.Exclude
import com.pet001kambala.namopsfleetmanager.BR

/***
 * A survey to record the current depth of the tyre and any other comments
 */
@Keep
data class TyreSurveyItem(
    var date: String? = null,
    var account: String? = null,
    @Transient @get: Exclude var tyre: Tyre? = null
) : AbstractModel() {

    override fun data() = arrayListOf(
        Pair("Date", date),
        Pair("Current Tread Depth", depth),
        Pair("Original Tread Type", tyre?.purchaseThreadType),
        Pair("Current Tread Type",currentThreadType),
        Pair("Valve Condition", valveCondition),
        Pair("Cap Present?", if (capPresent) "Yes" else "No"),
        Pair("Recommended Pressure",tyre?.recommendedPressure),
        Pair("Actual Pressure",actualPressure),
        Pair("Current Condition",currentCondition),
        Pair("Comments",comment)
    )
    @Bindable
    var currentThreadType: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.currentThreadType)
            }
        }
    @Bindable
    var valveCondition: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.valveCondition)
            }
        }
    @Bindable
    var capPresent: Boolean = true
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.capPresent)
            }
        }
    @Bindable
    var actualPressure: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.actualPressure)
            }
        }
    @Bindable
    var tyreSN: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.tyreSN)
            }
        }
    @Bindable
    var currentCondition: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.currentCondition)
            }
        }
    @Bindable
    var depth: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.depth)
            }
        }
    @Bindable
    var comment: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.comment)
            }
        }
}