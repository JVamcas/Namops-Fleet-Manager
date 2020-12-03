package com.pet001kambala.namopsfleetmanager.ui.tyres.vendor

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.viewModelScope
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.repository.TyreRepo
import com.pet001kambala.namopsfleetmanager.utils.AccessType
import kotlinx.android.synthetic.main.fragment_tyre_vendor_registration.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch


class TyreVendorHomeDetailsFragment : TyreVendorRegistrationDetailsFragment() {


    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        vendor_register.text = getString(R.string.update)

        vendor_register.isVisible = isAuthorized(AccessType.UPDATE_TYRE_VENDOR)

        vendor_register.setOnClickListener {
            tyreModel.viewModelScope.launch {
                showProgressBar("Just a moment...")
                val tyreRepo = TyreRepo()
                val results = tyreRepo.updateTyreVendor(vendor)
                endProgressBar()
                navController.popBackStack()
                parseRepoResults(results)
            }
        }
    }
}