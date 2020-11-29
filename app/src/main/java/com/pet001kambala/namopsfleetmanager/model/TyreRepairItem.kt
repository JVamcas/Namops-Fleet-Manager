package com.pet001kambala.namopsfleetmanager.model

import androidx.annotation.Keep
import androidx.databinding.Bindable
import com.pet001kambala.namopsfleetmanager.BR
@Keep
class TyreRepairItem(
    var dateSent: String? = null,
    var dateReturned: String? = null
) : AbstractModel() {

    override fun data() = arrayListOf(
        Pair("Date Sent", dateSent),
        Pair("Date Returned", dateReturned),
        Pair("Vendor", repairVendor),
        Pair("Repair cost", repairCost),
        Pair("Condition when received", repairCondition),
        Pair("Send thread depth", sentThreadDepth),
        Pair("Received thread depth", repairThreadDepth),
        Pair("Send thread type", sentThreadType),
        Pair("Receive thread type", repairThreadType),
        Pair("Comments", comments)
    )

    @Bindable
    var repairThreadDepth: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.repairThreadDepth)
            }
        }

    @Bindable
    var repairCondition: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.repairCondition)
            }
        }
    @Bindable
    var repairThreadType: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.repairThreadType)
            }
        }
    @Bindable
    var sentThreadDepth: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.sentThreadDepth)
            }
        }
    @Bindable
    var sentThreadType: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.sentThreadType)
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
    var repairVendor: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.repairVendor)
            }
        }
    @Bindable
    var repairCost: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.repairCost)
            }
        }
    @Bindable
    var comments: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.comments)
            }
        }
}