package com.pet001kambala.namopsfleetmanager.ui.trailer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.model.TrailerMountItem
import com.pet001kambala.namopsfleetmanager.repository.TrailerRepo
import com.pet001kambala.namopsfleetmanager.utils.DateUtil
import com.pet001kambala.namopsfleetmanager.utils.DateUtil.Companion._24
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.android.synthetic.main.fragment_mount_trailer.*
import kotlinx.android.synthetic.main.fragment_mount_trailer.vehicle_unit_no_layout
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

class UnMountTrailerDetailsFragment : MountTrailerDetailsFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState)

        return binding.root
    }

    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        trailer_no_layout.isEnabled = false
        vehicle_unit_no_layout.isEnabled = false

        mount_trailer.text = getString(R.string.un_mount_tyre)

        val trailerRepo = TrailerRepo()
        var trailerMountItem: TrailerMountItem?
        trailerModel.viewModelScope.launch {
            binding.resultsLoaded = false
            val mountItemSearchResults = trailerRepo.findTrailerMountItem(trailer.unitNumber!!)
            binding.resultsLoaded = true

            if (mountItemSearchResults is Results.Error) {
                parseRepoResults(mountItemSearchResults)
                navController.popBackStack()
                return@launch
            }

            val mountList = (mountItemSearchResults as Results.Success<*>).data

            if (mountList.isNullOrEmpty()) {
                //mount item cannot be found hence hide actual error - should not occur
                showToast("Err: Unknown error has occurred.")
                navController.popBackStack()
                return@launch
            }

            trailerMountItem = mountList.first() as TrailerMountItem
            binding.trailerMountItem = trailerMountItem

            (mountList.first() as TrailerMountItem).apply {
                unMountDate = DateUtil.today()._24()

                mount_trailer.setOnClickListener {
                    trailerModel.viewModelScope.launch {
                        showProgressBar("UnMounting trailer...")
                        val mountResults =
                            trailerRepo.unMountTrailer(trailer = trailer, mountItem = trailerMountItem!!)
                        endProgressBar()
                        if (mountResults is Results.Success<*>) {
                            showToast("UnMounted successfully.")
                            navController.popBackStack(R.id.trailerListFragment, false)
                        } else parseRepoResults(mountResults)
                        endProgressBar()
                    }
                }
            }
        }
    }
}