package com.pet001kambala.namopsfleetmanager.ui.vehicles

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import com.pet001kambala.namopsfleetmanager.model.Vehicle
import com.pet001kambala.namopsfleetmanager.ui.AbstractFragment
import com.pet001kambala.namopsfleetmanager.utils.Const
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.convert

abstract class AbstractVehicleDetailsFragment: AbstractFragment() {

    lateinit var vehicle: Vehicle
    val vehicleModel: VehiclesViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vehicle = Vehicle()
        arguments?.let {
            val json = it.getString(Const.VEHICLE)
            json?.let {
                vehicle = it.convert()
            }
        }
    }
}