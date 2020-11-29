package com.pet001kambala.namopsfleetmanager.ui.vehicles

import android.os.Bundle
import android.view.View
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.model.Vehicle
import com.pet001kambala.namopsfleetmanager.utils.Const
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.toJson
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.android.synthetic.main.fragment_vehicle_registration.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

class VehicleHomeDetailsFragment : VehicleRegistrationDetailsFragment() {

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val rowIndex = it.getString(Const.ROW_POS)
            rowIndex?.let {
                val index = it.toInt()
                vehicle =
                    ((vehicleModel.vehiclesList.value as Results.Success<*>).data!![index] as Vehicle)
            }
        }
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        unit_number_layout.isEnabled = false

        vehicle_reg_continue.setOnClickListener {
            val json = vehicle.toJson()
            val bundle = Bundle().also { it.putString(Const.VEHICLE, json) }
            navController.navigate(
                R.id.action_vehicleHomeDetailsFragment_to_extendedVehicleHomeDetailsFragment,
                bundle
            )
        }
    }
}