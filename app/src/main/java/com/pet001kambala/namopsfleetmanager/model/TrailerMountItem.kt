package com.pet001kambala.namopsfleetmanager.model

import androidx.annotation.Keep
import androidx.databinding.Bindable
import com.pet001kambala.namopsfleetmanager.BR
@Keep
class TrailerMountItem(
    var mountDate: String? = null,
    var unMountDate: String? = null
) :
    AbstractModel() {

    @Bindable
    var trailerNo: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.trailerNo)
            }
        }
    @Bindable
    var horseNo: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyPropertyChanged(BR.horseNo)
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