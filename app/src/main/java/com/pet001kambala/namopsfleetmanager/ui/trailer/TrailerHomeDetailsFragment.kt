package com.pet001kambala.namopsfleetmanager.ui.trailer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.model.Trailer
import com.pet001kambala.namopsfleetmanager.model.Vehicle
import com.pet001kambala.namopsfleetmanager.repository.TrailerRepo
import com.pet001kambala.namopsfleetmanager.repository.VehicleRepo
import com.pet001kambala.namopsfleetmanager.utils.Const
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.android.synthetic.main.fragment_extended_vehicle_registration.*
import kotlinx.android.synthetic.main.fragment_vehicle_registration.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

class TrailerHomeDetailsFragment : TrailerRegistrationDetailsFragment() {

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val rowIndex = it.getString(Const.ROW_POS)
            rowIndex?.let {
                val index = it.toInt()
                trailer =
                    ((trailerModel.trailersList.value as Results.Success<*>).data!![index] as Trailer)
            }
        }
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        register.text = getString(R.string.update)
        unit_number_layout.isEnabled = false
        register.setOnClickListener {
            trailerModel.viewModelScope.launch {
                showProgressBar("Just a moment...")
                val trailerRepo = TrailerRepo()
                val updateResults = trailerRepo.updateTrailerDetails(trailer)
                endProgressBar()

                if (updateResults is Results.Success<*>)
                    navController.popBackStack(R.id.trailerListFragment, false)
                parseRepoResults(updateResults)
            }
        }
    }
}