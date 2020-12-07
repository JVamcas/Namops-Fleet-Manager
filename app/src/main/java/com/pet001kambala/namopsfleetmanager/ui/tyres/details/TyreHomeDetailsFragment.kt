package com.pet001kambala.namopsfleetmanager.ui.tyres.details

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.lifecycle.viewModelScope
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.model.TyreMountItem
import com.pet001kambala.namopsfleetmanager.model.TyreRepairItem
import com.pet001kambala.namopsfleetmanager.model.TyreInspectionItem
import com.pet001kambala.namopsfleetmanager.repository.TyreRepo
import com.pet001kambala.namopsfleetmanager.utils.AccessType
import com.pet001kambala.namopsfleetmanager.utils.Const
import com.pet001kambala.namopsfleetmanager.utils.DateUtil.Companion._24
import com.pet001kambala.namopsfleetmanager.utils.DateUtil.Companion.today
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.convert
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.toJson
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.android.synthetic.main.fragment_tyre_registration.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

class TyreHomeDetailsFragment : TyreRegistrationDetailsFragment() {

    private lateinit var tyreJson: String

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            val json = it.getString(Const.MODEL)
            json?.let {
                tyre = json.convert()
            }
        }
        tyreJson = tyre.toJson()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState)
        setHasOptionsMenu(true)
        binding.tyre = tyre
        binding.registerTyre.text = getString(R.string.update)
        binding.snNumberLayout.isEnabled = false
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        register_tyre.isVisible = isAuthorized(AccessType.UPDATE_TYRE)
        recommended_pressure_layout.isEnabled = false

        register_tyre.setOnClickListener {
            tyreModel.viewModelScope.launch {
                val tyreRepo = TyreRepo()
                showProgressBar("Just a moment...")
                val updateResults = tyreRepo.updateTyreDetails(tyre)
                endProgressBar()
                if (updateResults is Results.Success<*>)
                    onBackClick()
                parseRepoResults(updateResults)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.tyre_menu, menu)

        menu.findItem(R.id.send_tyre_for_repair).isEnabled = !tyreAtVendor(tyre) && isAuthorized(AccessType.SEND_OR_RECEIVE_TYRE_FROM_VENDOR)
        menu.findItem(R.id.receive_tyre_from_repair).isEnabled = tyreAtVendor(tyre) && isAuthorized(AccessType.SEND_OR_RECEIVE_TYRE_FROM_VENDOR)

        menu.findItem(R.id.mount_tyre).isEnabled =
            isAuthorized(AccessType.MOUNT_OR_UNMOUNT_TYRE)
        menu.findItem(R.id.unmount_tyre).isEnabled =
            isAuthorized(AccessType.MOUNT_OR_UNMOUNT_TYRE)
        menu.findItem(R.id.inspect_tyre).isEnabled = isAuthorized(AccessType.INSPECT_TYRE)
        menu.findItem(R.id.tyre_records).isEnabled = isAuthorized(AccessType.VIEW_TYRE_RECORDS)
        menu.findItem(R.id.export_to_excel).isEnabled = isAuthorized(AccessType.EXPORT_TYRE)
    }


    @ExperimentalCoroutinesApi
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val bundle = Bundle().also { it.putString(Const.TYRE, tyreJson) }
        when (item.itemId) {
            R.id.mount_tyre -> {
                if (tyre.mounted) {
                    showToast("Err: Tyre is already mounted on ${tyre.trailerNo?: tyre.horseNo}.")
                    return true
                }
                navController.navigate(R.id.action_tyreDetailsFragment_to_mountTyreFragment, bundle)
            }
            R.id.unmount_tyre -> {
                if (!tyre.mounted) {
                    showToast("Err: Tyre is not mounted.")
                    return true
                }
                navController.navigate(
                    R.id.action_tyreDetailsFragment_to_unMountTyreFragment,
                    bundle
                )
            }
            R.id.inspect_tyre -> {
                navController.navigate(
                    R.id.action_tyreDetailsFragment_to_tyreSurveyFragment,
                    bundle
                )
            }

            R.id.mount_record -> {
                navController.navigate(
                    R.id.action_tyreDetailsFragment_to_tyreMountRecordsFragment,
                    bundle
                )
            }
            R.id.survey_record -> {
                navController.navigate(
                    R.id.action_tyreDetailsFragment_to_tyreSurveyRecordsFragment,
                    bundle
                )
            }

            R.id.repair_record -> {
                navController.navigate(
                    R.id.action_tyreDetailsFragment_to_tyreRepairRecordsFragment,
                    bundle
                )
            }

            R.id.send_tyre_for_repair -> {
                when {
                    tyreAtVendor((tyre)) -> {
                        showToast("Err: Tyre not yet received from ${tyre.location}.")
                        return true
                    }
                    tyre.mounted -> {
                        showToast("Err: You need to unMount tyre first.")
                        return true
                    }
                }
                navController.navigate(
                    R.id.action_tyreDetailsFragment_to_tyreRepairFragment,
                    bundle
                )
            }
            R.id.receive_tyre_from_repair -> {
                if (!tyreAtVendor(tyre)) {
                    showToast("Err: Tyre is not at any vendor.")
                    return true
                }

                tyreModel.viewModelScope.launch {
                    val tyreRepo = TyreRepo()
                    val results = tyreRepo.findTyreRepairItem(tyre.serialNumber!!)
                    if (results is Results.Success<*> && !results.data.isNullOrEmpty()) {
                        results.data.let {
                            val tyreRepair = results.data[0] as TyreRepairItem
                            val json = tyreRepair.toJson()
                            bundle.putString(Const.TYRE_REPAIR, json)
                            navController.navigate(
                                R.id.action_tyreDetailsFragment_to_receiveTyreFromRepairFragment,
                                bundle
                            )
                        }
                    } else parseRepoResults(results)
                }
            }

            R.id.export_to_excel -> {
                tyreModel.viewModelScope.launch {
                    if (isStoragePermissionGranted()) {//permission must have been granted
                        showProgressBar("Loading tyre data....")
                        val recordResults = TyreRepo().loadTyreRecords(tyre.serialNumber!!)
                        endProgressBar()

                        var surveyRecords =
                            ((recordResults[0] as? Results.Success<*>)?.data as? ArrayList<TyreInspectionItem>
                                ?: ArrayList())
                        surveyRecords = surveyRecords.map {
                            it.also {
                                it.tyre = tyre
                            }
                        } as ArrayList<TyreInspectionItem>

                        val mountRecords =
                            (recordResults[1] as? Results.Success<*>)?.data as? ArrayList<TyreMountItem>
                                ?: ArrayList()
                        val repairRecords =
                            (recordResults[2] as? Results.Success<*>)?.data as? ArrayList<TyreRepairItem>
                                ?: ArrayList()


                        toStorage(
                            "Namops _${tyre.serialNumber}_ Record_${today()._24()}.xlsx",
                            arrayListOf(
                                "Tyre Mount Records",
                                "Tyre Survey Records",
                                "Tyre Repair Records"

                            ),
                            arrayListOf(mountRecords, surveyRecords, repairRecords)
                        )
                    }
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}