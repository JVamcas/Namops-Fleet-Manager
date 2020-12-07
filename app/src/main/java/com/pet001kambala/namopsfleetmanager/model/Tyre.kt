package com.pet001kambala.namopsfleetmanager.model

import androidx.annotation.Keep
import androidx.databinding.Bindable
import com.google.firebase.firestore.Exclude
import com.pet001kambala.namopsfleetmanager.BR
import com.pet001kambala.namopsfleetmanager.utils.Const
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.extractValueFromMoneyString
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.moneyFormat

@Keep
class Tyre(
    var location: String = Const.defaultLocation,
    var mounted: Boolean = false,
    var horseNo: String? = null,
    var trailerNo: String? = null,
    var distanceCovered: Double = 0.0,
    var reference_vehicle_km: String? = null,
    var accumulatedCost: String? = null

) : AbstractModel(), Comparable<Tyre> {
    @get:Exclude
    @Transient
    override var id: String = ""

    override fun data() = arrayListOf(
        Pair("Serial Number", serialNumber),
        Pair("Brand", brand),
        Pair("Size", size),
        Pair("Recommended Pressure", recommendedPressure),
        Pair("Purchase Price", moneyFormat("NAD", extractValueFromMoneyString(purchasePrice).toDouble())),
        Pair("Accumulated Cost", moneyFormat("NAD", extractValueFromMoneyString(accumulatedCost).toDouble())),
        Pair("Date Purchased", purchaseDate),
        Pair("Current Condition", currentCondition),
        Pair("Location", location),
        Pair("Condition at Purchase", purchaseCondition),
        Pair("Mounted?", if (mounted) "Yes" else "No"),
        Pair("Mount Position", mountPosition),
        Pair("Mounted On", trailerNo ?: horseNo),
        Pair("Purchase Tread Type", purchaseThreadType),
        Pair("Current Tread Type", currentThreadType),
        Pair("Distance Covered", "$distanceCovered KM"),
        Pair("Purchase Tread Depth", "$purchaseThreadDepth mm"),
        Pair(
            "Current Tread Depth",
            if (currentThreadDepth.isNullOrEmpty()) " - " else "$currentThreadDepth mm"
        ),
        Pair(
            "Cost Per KM",
            moneyFormat("NAD", extractValueFromMoneyString(accumulatedCost).toDouble() / distanceCovered)
        )
    )

    @Bindable
    var recommendedPressure: String? = null
    set(value) {
        field = value
        notifyPropertyChanged(BR.recommendedPressure)
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
                recommendedPressure = if(value =="385/65R225") "800" else if( value == "315/80R225") "700" else null
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
                //when
                var totalPrice = extractValueFromMoneyString(accumulatedCost).toDouble()
                val purchasePrice = extractValueFromMoneyString(field).toDouble()
                val newPrice = extractValueFromMoneyString(value).toDouble()
                totalPrice = totalPrice - purchasePrice + newPrice
                accumulatedCost = moneyFormat("NAD",totalPrice)

                field = value
                notifyPropertyChanged(BR.purchasePrice)
            }
        }

    override fun compareTo(other: Tyre): Int {
        val matchSNOther = Regex("(\\d{4})NOL(\\d+)").find(other.serialNumber!!)
        val (prefixOther, tyreIndexOther) = matchSNOther!!.destructured

        val match = Regex("(\\d{4})NOL(\\d+)").find(serialNumber!!)
        val (prefix, tyreIndex) = match!!.destructured

        return when {
            prefix.toInt() != prefixOther.toInt() -> compareValues(prefix.toInt(), prefixOther.toInt())
            tyreIndex.toInt() != tyreIndexOther.toInt() -> compareValues(tyreIndex.toInt(), tyreIndexOther.toInt())
            else -> 0
        }
    }
}