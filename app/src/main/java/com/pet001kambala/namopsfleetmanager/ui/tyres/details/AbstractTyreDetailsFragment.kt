package com.pet001kambala.namopsfleetmanager.ui.tyres.details

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.model.Tyre
import com.pet001kambala.namopsfleetmanager.model.TyreMountItem
import com.pet001kambala.namopsfleetmanager.ui.AbstractFragment
import com.pet001kambala.namopsfleetmanager.ui.tyres.TyresViewModel
import com.pet001kambala.namopsfleetmanager.utils.Const
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.convert

abstract class AbstractTyreDetailsFragment : AbstractFragment() {


    lateinit var tyre: Tyre
    val tyreModel: TyresViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tyre = Tyre()
        arguments?.apply {
            val json = getString(Const.TYRE)
            json?.let {
                tyre = it.convert()
            }
        }
    }

    override fun onBackClick() {
        navController.popBackStack(R.id.tyresListFragment, false)
    }

    protected fun tyreAtVendor(tyre: Tyre): Boolean {
        return !tyre.mounted && tyre.location != Const.defaultLocation
    }
}