package com.pet001kambala.namopsfleetmanager.ui.tyres.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.repository.TyreRepo
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.android.synthetic.main.fragment_send_tyre_for_repair.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

class ReceiveTyreFromRepairDetailsFragment : SendTyreForRepairDetailsFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tyreRepair.apply {
            repairThreadDepth = null
            repairThreadType = null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState)
        binding.tyreRepairRecord.text = getString(R.string.receive_tyre_repair)
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        sent_thread_depth_layout.visibility = GONE
        sent_thread_type.visibility = GONE
        comment_layout.visibility = GONE
        tyre_vendor.isEnabled = false

        tyre_repair_record.setOnClickListener {
            if (!tyreAtVendor(tyre)){
                showToast("Err: Tyre is not at vendor.")
                return@setOnClickListener
            }
            showProgressBar("Just a moment...")
            tyreModel.viewModelScope.launch {
                val tyreRepo = TyreRepo()
                val results = tyreRepo.receiveTyreFromRepair(tyre, tyreRepair)
                endProgressBar()
                if(results is Results.Success<*>){
                    showToast("Received.")
                    navController.popBackStack(R.id.tyresListFragment,false)
                }else parseRepoResults(results)
            }
        }
    }
}