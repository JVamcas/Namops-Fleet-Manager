package com.pet001kambala.namopsfleetmanager.ui.tyres.records

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.model.Cell
import com.pet001kambala.namopsfleetmanager.model.Tyre
import com.pet001kambala.namopsfleetmanager.utils.AccessType
import com.pet001kambala.namopsfleetmanager.utils.Const
import com.pet001kambala.namopsfleetmanager.utils.DateUtil.Companion._24
import com.pet001kambala.namopsfleetmanager.utils.DateUtil.Companion.today
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.android.synthetic.main.tyres_list_fragment.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

open class TyresListFragment : AbstractTyreRecord<Tyre>() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        setHasOptionsMenu(true)
        binding.registerTyre.visibility = View.VISIBLE

        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        register_tyre.isVisible = isAuthorized(AccessType.REG_TYRE)

        register_tyre.setOnClickListener {
            navController.navigate(R.id.action_tyresListFragment_to_tyreRegistrationFragment)
        }

        if (isAuthorized(AccessType.VIEW_TYRE)) {// if authorized to view tyres here
            tyreModel.tyresList.observe(viewLifecycleOwner, Observer {
                it?.let { results ->
                    when (results) {
                        Results.Loading -> {
                            binding.tyresCount = 1
                            showProgressBar("Loading tyres...")
                        }
                        is Results.Success<*> -> {
                            endProgressBar()

                            binding.tyresCount = results.data?.size ?: 0

                            if (!results.data.isNullOrEmpty()) {
                                val tyreList = (results.data as ArrayList<Tyre>)
                                    .sorted()
                                    .reversed()
//                                    .take(Const.MAX_TYRE)
                                val headers =
                                    tyreList[0].data().map { it.first }//col headers text
                                val colHeader = headers.map { Cell(it) } as ArrayList
                                val rows = tyreList.map {
                                    it.data().map { Cell(it.second) } as ArrayList
                                }
                                val rowHeader =
                                    tyreList.withIndex()
                                        .map { Cell((it.index + 1).toString()) } as ArrayList
                                initTable(
                                    colHeader = colHeader,
                                    rows = rows,
                                    rowHeader = rowHeader,
                                    tableView = tyres_table,
                                    destination = R.id.action_tyresListFragment_to_tyreHomeDetailsFragment,
                                    tableData = tyreList
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
        inflater.inflate(R.menu.tyres_list_menu, menu)
        menu.findItem(R.id.tyre_vendors).isEnabled = isAuthorized(AccessType.VIEW_TYRE_VENDOR)
        menu.findItem(R.id.export_all_tyres_history).isEnabled =
            isAuthorized(AccessType.EXPORT_TYRE)
    }

    @ExperimentalCoroutinesApi
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.worn_out_tyres -> {
                navController.navigate(R.id.action_tyresListFragment_to_warnOutTyreListFragment)
            }
            R.id.find_tyre -> {
                navController.navigate(R.id.action_global_findTyreDetailsFragment)
            }
            R.id.tyre_vendors -> {
                navController.navigate(R.id.action_tyresListFragment_to_tyreVendors)
            }
            R.id.export_all_tyres_history -> {
                if (isStoragePermissionGranted()) {//permission must have been granted
                    tyreModel.tyresList.value?.apply {
                        val today = today()._24()
                        (this as? Results.Success<*>)?.data?.let {
                            toStorage("Namops Tyre Record_$today.xlsx", arrayListOf(today),
                                arrayListOf(it.map { it as Tyre } as ArrayList<Tyre>)
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