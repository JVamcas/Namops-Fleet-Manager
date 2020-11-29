package com.pet001kambala.namopsfleetmanager.ui.vehicles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.databinding.FragmentExtendedVehicleRegistrationBinding
import com.pet001kambala.namopsfleetmanager.repository.VehicleRepo
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.android.synthetic.main.fragment_extended_vehicle_registration.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

open class ExtendedVehicleRegistrationDetailsFragment : AbstractVehicleDetailsFragment() {

    lateinit var binding: FragmentExtendedVehicleRegistrationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentExtendedVehicleRegistrationBinding.inflate(inflater, container, false)
        binding.vehicle = vehicle
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        register.setOnClickListener {
            vehicleModel.viewModelScope.launch {
                showProgressBar("Just a moment...")
                val vehicleRepo = VehicleRepo()
                val searchResults = vehicle.unitNumber?.let { it1 -> vehicleRepo.findVehicle(it1) }
                if(searchResults is Results.Success<*>){
                    if(!searchResults.data.isNullOrEmpty()){
                        endProgressBar()
                        showToast("Err: Vehicle is already registered.")
                        return@launch
                    }
                    val registrationResults = vehicleRepo.registerVehicle(vehicle)
                    endProgressBar()
                    if (registrationResults is Results.Success<*>){
                        showToast("Registration success.")
                        navController.popBackStack(R.id.vehiclesListFragment,false)
                    }
                    else parseRepoResults(registrationResults)
                }
                else
                    parseRepoResults(searchResults)
                endProgressBar()
            }
        }
    }

    override fun onBackClick() {
        navController.popBackStack(R.id.vehiclesListFragment, false)
    }
}