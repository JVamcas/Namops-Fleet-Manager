package com.pet001kambala.namopsfleetmanager.ui.tyres.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.model.TyreMountItem
import com.pet001kambala.namopsfleetmanager.repository.TyreRepo
import com.pet001kambala.namopsfleetmanager.utils.DateUtil.Companion._24
import com.pet001kambala.namopsfleetmanager.utils.DateUtil.Companion.today
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.android.synthetic.main.fragment_mount_tyre.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

class UnMountTyreDetailsFragment : MountTyreDetailsFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState)

        binding.mountTyre.text = getString(R.string.un_mount_tyre)

        binding.mountPositionLayout.isEnabled = false
        binding.vehicleUnitNoLayout.isEnabled = false
        return binding.root
    }

    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val tyreRepo = TyreRepo()
        var tyreMountItem: TyreMountItem?
        tyreModel.viewModelScope.launch {
            binding.resultsLoaded = false
            val mountItemSearchResults = tyreRepo.findTyreMountItem(tyre.serialNumber!!)
            binding.resultsLoaded = true

            if (mountItemSearchResults is Results.Error) {
                parseRepoResults(mountItemSearchResults)
                navController.popBackStack()
                return@launch
            }

            val defaultReason = resources.getStringArray(R.array.Removal_reasons)[0]
            val mountList = (mountItemSearchResults as Results.Success<*>).data

            if (mountList.isNullOrEmpty()) {
                //mount item cannot be found hence hide actual error - should not occur
                showToast("Err: Unknown error has occurred.")
                navController.popBackStack()
                return@launch
            }

            tyreMountItem = mountList.first() as TyreMountItem
            tyreMountItem!!.unMountReason = defaultReason
            binding.tyreUsage = tyreMountItem

            (mountList.first() as TyreMountItem).apply {
                unMountReason = defaultReason
                unMountDate = today()._24()
                binding.tyreUsage = this

                mount_tyre.setOnClickListener {
                    tyreModel.viewModelScope.launch {
                        showProgressBar("UnMounting tyre...")
                        val mountResults =
                            tyreRepo.unMountTyre(tyre = tyre, mountItem = tyreMountItem!!)
                        endProgressBar()
                        if (mountResults is Results.Success<*>) {
                            showToast("UnMounted successfully.")
                            navController.popBackStack(R.id.tyresListFragment, false)
                        } else parseRepoResults(mountResults)
                        endProgressBar()
                    }
                }
            }
        }
    }
}