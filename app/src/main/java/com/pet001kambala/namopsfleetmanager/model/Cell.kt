package com.pet001kambala.namopsfleetmanager.model

import androidx.annotation.Keep

/***
 * Represent a table cell either for column header, row header or ordinal cell
 */
@Keep
data class Cell(private var _data: String?) {
    val data: String?
        get() = if (_data.isNullOrEmpty()) " - " else _data
}