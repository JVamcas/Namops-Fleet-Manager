package com.pet001kambala.namopsfleetmanager.ui.vehicles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.databinding.FragmentVehicleRegistrationBinding
import com.pet001kambala.namopsfleetmanager.utils.Const
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.toJson
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.yearOfMake
import kotlinx.android.synthetic.main.fragment_vehicle_registration.*
import kotlinx.coroutines.ExperimentalCoroutinesApi


open class VehicleRegistrationDetailsFragment : AbstractVehicleDetailsFragment() {

    lateinit var binding: FragmentVehicleRegistrationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentVehicleRegistrationBinding.inflate(layoutInflater, container, false)
        binding.vehicle = vehicle
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        year_layout.setItems(yearOfMake())

        vehicle_reg_continue.setOnClickListener {
            val bundle = Bundle()
            val json = vehicle.toJson()
            bundle.putString(Const.VEHICLE, json)
            navController.navigate(
                R.id.action_vehicleRegistrationFragment_to_extendedVehicleRegistrationFragment,
                bundle
            )
        }
    }
}