package com.pet001kambala.namopsfleetmanager.ui.trailer

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.lifecycle.viewModelScope
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.model.Trailer
import com.pet001kambala.namopsfleetmanager.repository.TrailerRepo
import com.pet001kambala.namopsfleetmanager.utils.AccessType
import com.pet001kambala.namopsfleetmanager.utils.Const
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.convert
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
            val json = it.getString(Const.MODEL)
            json?.let {
                trailer = json.convert()
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
        year_layout.setItems(ParseUtil.yearOfMake())

        register.isVisible = isAuthorized(AccessType.UPDATE_TRAILER)

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

        menu.findItem(R.id.mount_trailer).isEnabled = isAuthorized(AccessType.MOUNT_UNMOUNT_TRAILER)
        menu.findItem(R.id.un_mount_trailer).isEnabled = isAuthorized(AccessType.MOUNT_UNMOUNT_TRAILER)
        menu.findItem(R.id.export_all_history).isEnabled = isAuthorized(AccessType.EXPORT_TRAILER)
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
            R.id.mounted_tyres ->{
                val bundle = Bundle().also { it.putString(Const.UNIT_NO, trailer.unitNumber) }
                navController.navigate(R.id.action_global_mountedTyreListFragment,bundle)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}