package com.pet001kambala.namopsfleetmanager.ui.trailer

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.model.Trailer
import com.pet001kambala.namopsfleetmanager.model.Vehicle
import com.pet001kambala.namopsfleetmanager.repository.TrailerRepo
import com.pet001kambala.namopsfleetmanager.repository.VehicleRepo
import com.pet001kambala.namopsfleetmanager.utils.Const
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.toJson
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.trailer_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mount_trailer -> {
                if (trailer.mounted) {
                    showToast("Err: Trailer is already mounted on ${trailer.horseNo}.")
                    return true
                }
                val bundle = Bundle().also { it.putString(Const.TRAILER, trailer.toJson()) }
                navController.navigate(
                    R.id.action_trailerHomeDetailsFragment_to_mountTrailerFragment,
                    bundle
                )
            }
            R.id.un_mount_trailer -> {
                if (!trailer.mounted) {
                    showToast("Err: Trailer is not mounted.")
                    return true
                }
                val bundle = Bundle().also { it.putString(Const.TRAILER, trailer.toJson()) }
                navController.navigate(
                    R.id.action_trailerHomeDetailsFragment_to_unMountTrailerDetailsFragment,
                    bundle
                )
            }
        }

        return super.onOptionsItemSelected(item)
    }
}