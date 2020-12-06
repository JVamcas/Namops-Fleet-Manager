package com.pet001kambala.namopsfleetmanager.ui.vehicles

import android.os.Bundle
import android.view.*
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.databinding.VehiclesFragmentBinding
import com.pet001kambala.namopsfleetmanager.model.Cell
import com.pet001kambala.namopsfleetmanager.model.Vehicle
import com.pet001kambala.namopsfleetmanager.ui.AbstractTableFragment
import com.pet001kambala.namopsfleetmanager.utils.AccessType
import com.pet001kambala.namopsfleetmanager.utils.DateUtil.Companion._24
import com.pet001kambala.namopsfleetmanager.utils.DateUtil.Companion.today
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.android.synthetic.main.tyres_list_fragment.*
import kotlinx.android.synthetic.main.vehicles_fragment.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

class VehiclesListFragment : AbstractTableFragment<Vehicle>() {

    private lateinit var binding: VehiclesFragmentBinding
    private val vehicleModel: VehiclesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        binding = VehiclesFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        register_vehicle.isEnabled = isAuthorized(AccessType.REG_VEHICLE)

        register_vehicle.setOnClickListener {
            navController.navigate(R.id.action_vehiclesFragment_to_vehicleRegistrationFragment)
        }
        if (isAuthorized(AccessType.VIEW_VEHICLES)) {
            vehicleModel.vehiclesList.observe(viewLifecycleOwner, Observer {
                it?.let { results ->
                    when (results) {
                        Results.Loading -> {
                            binding.vehiclesCount = 1
                            showProgressBar("Loading vehicles...")
                        }
                        is Results.Success<*> -> {
                            endProgressBar()
                            binding.vehiclesCount = results.data?.size ?: 0

                            if (!results.data.isNullOrEmpty()) {
                                val headers =
                                    results.data[0].data().map { it.first }//col headers text
                                val colHeader = headers.map { Cell(it) } as ArrayList
                                val rows = results.data.map {
                                    it.data().map { Cell(it.second) } as ArrayList
                                }
                                val rowHeader =
                                    results.data.withIndex()
                                        .map { Cell((it.index + 1).toString()) } as ArrayList
                                initTable(
                                    colHeader = colHeader,
                                    rows = rows,
                                    rowHeader = rowHeader,
                                    tableView = tyres_table,
                                    destination = R.id.action_vehiclesListFragment_to_vehicleHomeDetailsFragment,
                                    tableData = results.data as ArrayList<Vehicle>
                                )
                            }
                        }
                        else -> {
                            endProgressBar()
                            parseRepoResults(results)
                        }
                    }
                }
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (isAuthorized(AccessType.VIEW_VEHICLES))
            inflater.inflate(R.menu.vehicles_list_menu, menu)

        menu.findItem(R.id.export_to_excel).isEnabled = isAuthorized(AccessType.EXPORT_VEHICLE)
        menu.findItem(R.id.import_from_excel).isEnabled = isAuthorized(AccessType.REG_VEHICLE)
    }

    @ExperimentalCoroutinesApi
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.find_vehicle -> {

            }
            R.id.export_to_excel -> {
                if (isStoragePermissionGranted()) {//permission must have been granted
                    vehicleModel.vehiclesList.value?.apply {
                        val today = today()._24()
                        (this as? Results.Success<*>)?.data?.let {
                            toStorage("Namops Vehicle Record_$today.xlsx", arrayListOf(today),
                                arrayListOf(it.map { it as Vehicle } as ArrayList<Vehicle>)
                            )
                        }
                    }
                    return true
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}