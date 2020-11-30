package com.pet001kambala.namopsfleetmanager.model

import androidx.databinding.Bindable
import com.google.firebase.firestore.Exclude
import com.pet001kambala.namopsfleetmanager.BR
import com.pet001kambala.namopsfleetmanager.utils.Const

data class Trailer(
    var location: String = Const.defaultLocation,
    var mounted: Boolean = false,
    var horseNo: String? = null,
    var mountedTyreSNList: ArrayList<String> = ArrayList()
) : AbstractModel() {
    @Transient
    @get:Exclude
    override var id: String = ""
    override fun data() = arrayListOf(
        Pair("Unit No", unitNumber),
        Pair("VIN No", vinNo),
        Pair("Plate No", plate),
        Pair("Manufacturer", make),
        Pair("Type", type),
        Pair("Year", year),
        Pair("Color", color),
        Pair("Department", department),
        Pair("Location", location),
        Pair("Mounted?", if (mounted) "Yes" else "No"),
        Pair("Mounted On", horseNo)
    )

    @Bindable
    var type: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.type)
            }
        }

    @Bindable
    var color: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.color)
            }
        }

    @Bindable
    var year: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.year)
            }
        }

    @Bindable
    var vinNo: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.vinNo)
            }
        }


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