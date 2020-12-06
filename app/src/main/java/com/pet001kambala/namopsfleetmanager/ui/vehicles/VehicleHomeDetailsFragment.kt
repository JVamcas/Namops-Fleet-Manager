package com.pet001kambala.namopsfleetmanager.ui.vehicles

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.model.Vehicle
import com.pet001kambala.namopsfleetmanager.utils.AccessType
import com.pet001kambala.namopsfleetmanager.utils.Const
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.convert
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.toJson
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.android.synthetic.main.fragment_vehicle_registration.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

class VehicleHomeDetailsFragment : VehicleRegistrationDetailsFragment() {

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val json = it.getString(Const.MODEL)
            json?.let {
                vehicle = json.convert()
            }
        }
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        unit_number_layout.isEnabled = false

        year_layout.setItems(ParseUtil.yearOfMake())

        vehicle_reg_continue.isVisible = isAuthorized(AccessType.UPDATE_VEHICLE)

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