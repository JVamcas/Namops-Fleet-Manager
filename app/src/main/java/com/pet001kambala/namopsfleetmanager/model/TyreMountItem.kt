package com.pet001kambala.namopsfleetmanager.model

import androidx.annotation.Keep
import androidx.databinding.Bindable
import com.pet001kambala.namopsfleetmanager.BR
@Keep
data class TyreMountItem(
    var mountDate: String? = null,
    var unMountDate: String? = null

) : AbstractModel() {

    fun isHorse() =
        !this.vehicleNo.isNullOrEmpty() && this.vehicleNo?.first()?.toLowerCase() == 'h'

    override fun data() = arrayListOf(
        Pair("Dated Mounted", mountDate),
        Pair("Date UnMounted", unMountDate),
        Pair("Mount Position", mountPosition),
        Pair("Unit Number", vehicleNo),
        Pair("Reason For UnMounting", unMountReason),
        Pair("Comments", comment)
    )
    @Bindable
    var tyreSN: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.tyreSN)
            }
        }
    @Bindable
    var unMountReason: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.unMountReason)
            }
        }
    @Bindable
    var vehicleNo: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.vehicleNo)
            }
        }
    @Bindable
    var mountPosition: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.mountPosition)
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