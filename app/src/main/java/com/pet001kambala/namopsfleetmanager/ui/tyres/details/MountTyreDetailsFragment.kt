package com.pet001kambala.namopsfleetmanager.ui.tyres.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.databinding.FragmentMountTyreBinding
import com.pet001kambala.namopsfleetmanager.model.Trailer
import com.pet001kambala.namopsfleetmanager.model.TyreMountItem
import com.pet001kambala.namopsfleetmanager.repository.TrailerRepo
import com.pet001kambala.namopsfleetmanager.repository.TyreRepo
import com.pet001kambala.namopsfleetmanager.repository.VehicleRepo
import com.pet001kambala.namopsfleetmanager.utils.DateUtil.Companion._24
import com.pet001kambala.namopsfleetmanager.utils.DateUtil.Companion.today
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.android.synthetic.main.fragment_mount_tyre.*
import kotlinx.coroutines.*

open class MountTyreDetailsFragment : AbstractTyreDetailsFragment() {

    lateinit var binding: FragmentMountTyreBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMountTyreBinding.inflate(inflater, container, false)

        if (this !is UnMountTyreDetailsFragment) {
            binding.unMountReasonLayout.visibility = View.GONE
        }
        binding.tyreSnLayout.isEnabled = false
        binding.tyre = tyre

        return binding.root
    }

    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tyreMount = TyreMountItem().also { it.unMountReason = " - " }
        binding.tyreUsage = tyreMount
        binding.resultsLoaded = true //dismiss progress bar

        mount_tyre.setOnClickListener {
            tyreMount.apply {
                tyreSN = binding.tyre!!.serialNumber
                mountDate = today()._24()
            }

            tyreModel.viewModelScope.launch {
                val tyreRepo = TyreRepo()

                // 1. validate that unit exist
                var trailer: Trailer? = null

                showProgressBar("Validating mount...")
                if (tyreMount.isHorse()) {
                    val horseSearch = VehicleRepo().findVehicle(tyreMount.vehicleNo!!)
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
                } else {
                    val trailerSearch = TrailerRepo().findTrailer(tyreMount.vehicleNo!!)
                    if (trailerSearch is Results.Success<*>) {
                        if (trailerSearch.data.isNullOrEmpty()) {
                            endProgressBar()
                            showToast("Err: Trailer not found. Is it registered!")
                            return@launch
                        }
                        trailer = trailerSearch.data.first() as Trailer
                    //success and not empty, bail out here to mount tyre
                    } else {
                        endProgressBar()
                        parseRepoResults(trailerSearch)
                        return@launch
                    }
                }
                //2. check if this position is available
                val availableResults = tyreRepo.isMountPositionAvailable(
                    tyreMount.vehicleNo!!,
                    tyreMount.mountPosition!!,
                    tyreMount.isHorse()
                )
                if (!availableResults) {
                    endProgressBar()
                    showToast("Err: Position already has a tyre mounted on it.")
                    return@launch
                }
                endProgressBar()//end validating progress

                //3. mount tyre if all pass
                showProgressBar("Mounting tyre ...")
                val mountResults = tyreRepo.mountTyre(tyre = tyre, mountItem = tyreMount, trailer = trailer)
                endProgressBar()
                if (mountResults is Results.Success<*>) {
                    showToast("Mount success.")
                    navController.popBackStack(R.id.tyresListFragment,false)
                } else parseRepoResults(mountResults)
            }
        }
    }
}
