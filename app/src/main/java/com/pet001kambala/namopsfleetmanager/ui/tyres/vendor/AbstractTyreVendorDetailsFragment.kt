package com.pet001kambala.namopsfleetmanager.ui.tyres.vendor

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import com.pet001kambala.namopsfleetmanager.model.TyreVendor
import com.pet001kambala.namopsfleetmanager.ui.AbstractFragment
import com.pet001kambala.namopsfleetmanager.ui.AbstractTableFragment
import com.pet001kambala.namopsfleetmanager.ui.tyres.TyresViewModel
import com.pet001kambala.namopsfleetmanager.utils.Const
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.convert
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.coroutines.ExperimentalCoroutinesApi

abstract class AbstractTyreVendorDetailsFragment: AbstractFragment() {


    lateinit var vendor: TyreVendor
    val tyreModel: TyresViewModel by activityViewModels()

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vendor = TyreVendor()
        arguments?.let {
            val json = it.getString(Const.TYRE_VENDOR)
            json?.let {
                vendor = json.convert()
            }

            val rowIndexJson = it.getString(Const.ROW_POS)
            rowIndexJson?.let {
                val rowIndex = rowIndexJson.toInt()
                vendor = (tyreModel.tyreVendorsList.value as? Results.Success<*>)?.data?.get(rowIndex) as TyreVendor
            }
        }
    }
}