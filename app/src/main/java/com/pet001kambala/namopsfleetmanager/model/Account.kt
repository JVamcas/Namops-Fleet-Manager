package com.pet001kambala.namopsfleetmanager.model

import androidx.annotation.Keep
import androidx.databinding.Bindable
import com.pet001kambala.namopsfleetmanager.BR

@Keep
enum class AuthType {
    EMAIL, PHONE
}
@Keep
class Account
    : AbstractModel() {


    @Bindable
    var permissionList = ArrayList<String>()
    set(value) {
        if(field != value){
            field = value
            notifyPropertyChanged(BR.permissionList)
        }
    }

    @Bindable
    var name: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.name)
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
    var cellphone: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.cellphone)
            }
        }
    @Bindable
    var companyNumber: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.companyNumber)
            }
        }
    @Bindable
    var companyName: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.companyName)
            }
        }

    override fun equals(other: Any?): Boolean {
        if(other == null || other !is Account)
            return false
        return other.id == id
    }

    override fun toString(): String {
        return "$name - $companyName"
    }
}