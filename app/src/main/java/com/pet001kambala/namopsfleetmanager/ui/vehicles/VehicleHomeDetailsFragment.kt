package com.pet001kambala.namopsfleetmanager.ui.vehicles

import android.os.Bundle
import android.view.*
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.vehicle_menu, menu)
    }
    @ExperimentalCoroutinesApi
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val bundle = Bundle().also { it.putString(Const.UNIT_NO, vehicle.unitNumber) }
        when (item.itemId) {
            R.id.mounted_tyres ->{
                navController.navigate(R.id.action_global_mountedTyreListFragment,bundle)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}