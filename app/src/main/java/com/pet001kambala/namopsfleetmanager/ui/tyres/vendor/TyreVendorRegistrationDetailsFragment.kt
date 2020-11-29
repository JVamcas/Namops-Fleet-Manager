package com.pet001kambala.namopsfleetmanager.ui.tyres.vendor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import com.pet001kambala.namopsfleetmanager.databinding.FragmentTyreVendorHomeBinding
import com.pet001kambala.namopsfleetmanager.databinding.FragmentTyreVendorRegistrationBinding
import com.pet001kambala.namopsfleetmanager.repository.TyreRepo
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.android.synthetic.main.fragment_tyre_vendor_registration.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

open class TyreVendorRegistrationDetailsFragment : AbstractTyreVendorDetailsFragment() {

    lateinit var binding: FragmentTyreVendorRegistrationBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTyreVendorRegistrationBinding.inflate(inflater,container,false)
        binding.vendor = vendor
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vendor_register.setOnClickListener {
            tyreModel.viewModelScope.launch {
                showProgressBar("Just a moment...")
                val tyreRepo = TyreRepo()
                val results = tyreRepo.registerTyreVendor(vendor)
                endProgressBar()
                if(results is Results.Success<*>)
                    navController.popBackStack()
                parseRepoResults(results)
            }
        }
    }
}