package com.pet001kambala.namopsfleetmanager.model

import androidx.annotation.Keep
import androidx.databinding.Bindable
import java.util.*
import com.pet001kambala.namopsfleetmanager.BR
@Keep
class Auth
    : AbstractModel() {
    @Bindable
    var idMailCell: String? = null
        set(value) {
            if (field != value) {
                field = value?.toLowerCase(Locale.ROOT)
                notifyPropertyChanged(BR.idMailCell)
            }
        }

    @Bindable
    var password: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.password)
            }
        }
}