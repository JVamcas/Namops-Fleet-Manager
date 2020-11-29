package com.pet001kambala.namopsfleetmanager.ui.tyres.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.databinding.FragmentSendTyreForRepairBinding
import com.pet001kambala.namopsfleetmanager.model.TyreRepairItem
import com.pet001kambala.namopsfleetmanager.model.TyreVendor
import com.pet001kambala.namopsfleetmanager.repository.TyreRepo
import com.pet001kambala.namopsfleetmanager.utils.Const
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.convert
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.android.synthetic.main.fragment_send_tyre_for_repair.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch


open class SendTyreForRepairDetailsFragment : AbstractTyreDetailsFragment() {

    lateinit var binding: FragmentSendTyreForRepairBinding
    lateinit var tyreRepair: TyreRepairItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tyreRepair = TyreRepairItem()
        arguments?.let {
            val repairJson = it.getString(Const.TYRE_REPAIR)
            repairJson?.let {
                tyreRepair = it.convert()
            }
        }
        tyreRepair.apply {
            repairCost = "NAD"
            repairThreadDepth = " - "
            repairThreadType = " - "
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSendTyreForRepairBinding.inflate(inflater, container, false)
        binding.tyre = tyre
        binding.tyreRepair = tyreRepair
        binding.tyreSnLayout.isEnabled = false
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repair_thread_type.visibility = GONE
        repair_thread_depth_layout.visibility = GONE
        repair_cost_layout.visibility = GONE

        tyreModel.tyreVendorsList.observe(viewLifecycleOwner, Observer {
            if (it is Results.Success<*>) {
                tyre_vendor.setItems(it.data!!.map { (it as TyreVendor).toString() })
            }
        })

        tyre_repair_record.setOnClickListener {

            tyreModel.viewModelScope.launch {
                val tyreRepo = TyreRepo()
                showProgressBar("Just a moment...")
                val result = tyreRepo.sendTyreForRepair(tyre, tyreRepair)
                endProgressBar()
                if (result is Results.Success<*>) {
                    showToast("Sent.")
                    navController.popBackStack(R.id.tyresListFragment, false)
                } else parseRepoResults(result)
            }
        }
    }
}