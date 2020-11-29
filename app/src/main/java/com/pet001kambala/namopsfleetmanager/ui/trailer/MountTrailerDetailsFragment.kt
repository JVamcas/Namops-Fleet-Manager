package com.pet001kambala.namopsfleetmanager.ui.trailer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.databinding.FragmentMountTrailerBinding
import com.pet001kambala.namopsfleetmanager.model.Trailer
import com.pet001kambala.namopsfleetmanager.model.TrailerMountItem
import com.pet001kambala.namopsfleetmanager.repository.TrailerRepo
import com.pet001kambala.namopsfleetmanager.repository.VehicleRepo
import com.pet001kambala.namopsfleetmanager.utils.DateUtil.Companion._24
import com.pet001kambala.namopsfleetmanager.utils.DateUtil.Companion.today
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.android.synthetic.main.fragment_mount_trailer.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

open class MountTrailerDetailsFragment : AbstractTrailerDetailsFragment() {

    lateinit var binding: FragmentMountTrailerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMountTrailerBinding.inflate(inflater,container,false)
        return binding.root
    }

    @InternalCoroutinesApi
    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.resultsLoaded = true //dismiss progress bar

        val trailerMountItem = TrailerMountItem().also { it.trailerNo = trailer.unitNumber }
        trailer_no_layout.isEnabled = false

        binding.trailerMountItem = trailerMountItem

        mount_trailer.setOnClickListener {
            trailerMountItem.mountDate = today()._24()
            val trailerRepo = TrailerRepo()
            trailerModel.viewModelScope.launch {
                showProgressBar("Validating mount...")
                //1. validate that horse registered
                val horseSearch = VehicleRepo().findVehicle(trailerMountItem.horseNo!!)
                if (horseSearch is Results.Success<*>) {
                    if (horseSearch.data.isNullOrEmpty()) {
                        endProgressBar()
                        showToast("Err: Vehicle not found. Is it registered!")
                        return@launch
                    }//success and not empty, bail out here to mount tyre
                } else {
                    endProgressBar()
                    parseRepoResults(horseSearch)
                    return@launch
                }
                endProgressBar()
                //2. horse available - mount trailer
                showProgressBar("Mounting trailer...")
                val mountResults = trailerRepo.mountTrailer(trailer,trailerMountItem)
                endProgressBar()
                if(mountResults is Results.Success<*>){
                    showToast("Mount success.")
                    navController.popBackStack(R.id.trailerListFragment,false)
                }else parseRepoResults(mountResults)
            }
        }
    }
}