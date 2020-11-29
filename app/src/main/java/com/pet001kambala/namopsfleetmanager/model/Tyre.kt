package com.pet001kambala.namopsfleetmanager.model

import androidx.annotation.Keep
import androidx.databinding.Bindable
import com.pet001kambala.namopsfleetmanager.BR
import com.pet001kambala.namopsfleetmanager.utils.Const
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.extractDigit
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.moneyFormat
@Keep
data class Tyre(
    var location: String = Const.defaultLocation,
    var mounted: Boolean = false,
    var horseNo: String? = null,
    var trailerNo: String? = null,
    var distanceCovered: Double = 0.0,
    var reference_vehicle_km: String? = null,
    var accumulatedCost: String? = null
) : AbstractModel() {

    override fun data() = arrayListOf(
        Pair("Serial Number", serialNumber),
        Pair("Brand", brand),
        Pair("Size", size),
        Pair("Recommended Pressure", recommendedPressure),
        Pair("Purchase Price", moneyFormat("NAD", extractDigit(purchasePrice).toDouble())),
        Pair("Accumulated Cost", moneyFormat("NAD",extractDigit(accumulatedCost).toDouble())),
        Pair("Date Purchased", purchaseDate),
        Pair("Current Condition", currentCondition),
        Pair("Location",location),
        Pair("Condition at Purchase", purchaseCondition),
        Pair("Mounted?", if (mounted) "Yes" else "No"),
        Pair("Mount Position", mountPosition),
        Pair("Mounted On", trailerNo?: horseNo),
        Pair("Purchase Tread Type", purchaseThreadType),
        Pair("Current Tread Type", currentThreadType),
        Pair("Distance Covered", "$distanceCovered KM"),
        Pair("Purchase Tread Depth", "$purchaseThreadDepth mm"),
        Pair("Current Tread Depth", if(currentThreadDepth.isNullOrEmpty()) " - " else "$currentThreadDepth mm"),
        Pair(
            "Cost Per KM",
            moneyFormat("NAD", extractDigit(accumulatedCost).toDouble() / distanceCovered)
        )
    )
    @Bindable
    var recommendedPressure: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.recommendedPressure)
            }
        }
    @Bindable
    var brand: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.brand)
            }
        }
    @Bindable
    var purchaseThreadType: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.purchaseThreadType)
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
    var mountPosition: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.mountPosition)
            }
        }
    @Bindable
    var currentThreadType: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.purchaseThreadType)
            }
        }
    @Bindable
    var purchaseCondition: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.purchaseCondition)
            }
        }
    @Bindable
    var currentThreadDepth: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.currentThreadDepth)
            }
        }
    @Bindable
    var purchaseThreadDepth: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.purchaseThreadDepth)
            }
        }
    @Bindable
    var serialNumber: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.serialNumber)
            }
        }
    @Bindable
    var size: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.size)
            }
        }
    @Bindable
    var purchaseDate: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.purchaseDate)
            }
        }
    @Bindable
    var purchasePrice: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.purchasePrice)
            }
        }
}