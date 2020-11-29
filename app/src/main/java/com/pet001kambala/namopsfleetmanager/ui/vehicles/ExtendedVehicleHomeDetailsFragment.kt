package com.pet001kambala.namopsfleetmanager.ui.vehicles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.repository.VehicleRepo
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.android.synthetic.main.fragment_extended_vehicle_registration.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

class ExtendedVehicleHomeDetailsFragment : ExtendedVehicleRegistrationDetailsFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        binding.register.text = getString(R.string.update)

        return view
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        register.setOnClickListener {
            vehicleModel.viewModelScope.launch {
                showProgressBar("Just a moment...")
                val vehicleRepo = VehicleRepo()
                val updateResults = vehicleRepo.updateVehicleDetails(vehicle)
                endProgressBar()

                if (updateResults is Results.Success<*>)
                    navController.popBackStack(R.id.vehiclesListFragment, false)
                parseRepoResults(updateResults)
            }
        }
    }
}