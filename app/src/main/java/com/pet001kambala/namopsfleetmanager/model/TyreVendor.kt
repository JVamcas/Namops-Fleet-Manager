package com.pet001kambala.namopsfleetmanager.model

import androidx.annotation.Keep
import androidx.databinding.Bindable
import com.pet001kambala.namopsfleetmanager.BR

@Keep
class TyreVendor :
    AbstractModel() {

    override fun data() = arrayListOf(
        Pair("Name", name),
        Pair("Type", type),
        Pair("Phone 1", phone_1),
        Pair("Phone 2", phone_2),
        Pair("Fax", fax),
        Pair("Email address", email),
        Pair("Contact", contact),
        Pair("Address 1", address_1),
        Pair("Address 2", address_2),
        Pair("City", city),
        Pair("Country", country)
    )

    var name: String? = null
        @Bindable
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.name)
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
    var phone_1: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.phone_1)
            }
        }
    @Bindable
    var phone_2: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.phone_2)
            }
        }
    @Bindable
    var fax: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.fax)
            }
        }
    @Bindable
    var email: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.email)
            }
        }

    @Bindable
    var contact: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.contact)
            }
        }

    @Bindable
    var address_1: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.address_1)
            }
        }
    @Bindable
    var address_2: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.address_2)
            }
        }
    @Bindable
    var city: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.city)
            }
        }
    @Bindable
    var country: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.country)
            }
        }

    override fun toString(): String {
        return "$name [ $type ]"
    }
}