package com.pet001kambala.namopsfleetmanager.model

import androidx.annotation.Keep
import androidx.databinding.Bindable
import com.pet001kambala.namopsfleetmanager.BR

@Keep
class Vehicle : AbstractModel() {

    override fun data() = arrayListOf(
        Pair("Unit No", unitNumber),
        Pair("VIN No", vinNo),
        Pair("Type", type),
        Pair("Model", model),
        Pair("Year", year),
        Pair("Manufacturer", make),
        Pair("Plate No", plate),
        Pair("Color", vehicleColor),
        Pair("Engine No", engineNo),
        Pair("Department", department),
        Pair("Fuel Type", fuelType)
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
    var fuelType: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.fuelType)
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
    var model: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.model)
            }
        }
    @Bindable
    var vehicleColor: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.vehicleColor)
            }
        }
    @Bindable
    var type: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.type)
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
    var vinNo: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.vinNo)
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
    var plate: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.plate)
            }
        }
    @Bindable
    var engineNo: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.engineNo)
            }
        }

    override fun toString(): String {
        return "$year | $make | $model"
    }
}