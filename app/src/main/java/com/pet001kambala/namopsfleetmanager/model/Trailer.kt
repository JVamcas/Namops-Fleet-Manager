package com.pet001kambala.namopsfleetmanager.model

import androidx.databinding.Bindable
import com.pet001kambala.namopsfleetmanager.BR
import com.pet001kambala.namopsfleetmanager.utils.Const

data class Trailer(
    var location: String = Const.defaultLocation,
    var mounted: Boolean = false,
    var horseNo: String? = null,
    var mountedTyreIdList: ArrayList<String> = ArrayList()
)
    : AbstractModel() {

    override fun data()  = arrayListOf(
        Pair("Unit No",unitNumber),
        Pair("Plate No", plate),
        Pair("Manufacturer",make),
        Pair("Department",department),
        Pair("Location",location),
        Pair("Mounted?", if(mounted) "Yes" else "No"),
        Pair("Mounted On", horseNo)
    )

    @Bindable
    var department: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.department)
            }
        }

    @Bindable
    var make: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.make)
            }
        }

    @Bindable
    var unitNumber: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.unitNumber)
            }
        }

    @Bindable
    var plate: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.plate)
            }
        }
}