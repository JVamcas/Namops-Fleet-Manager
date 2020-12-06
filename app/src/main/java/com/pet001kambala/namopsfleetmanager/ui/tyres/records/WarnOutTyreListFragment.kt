package com.pet001kambala.namopsfleetmanager.ui.tyres.records

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.model.Cell
import com.pet001kambala.namopsfleetmanager.model.Tyre
import com.pet001kambala.namopsfleetmanager.model.TyreWornOutAlert
import com.pet001kambala.namopsfleetmanager.utils.AccessType
import com.pet001kambala.namopsfleetmanager.utils.Const
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.android.synthetic.main.tyres_list_fragment.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

class WarnOutTyreListFragment : AbstractTyreRecord<TyreWornOutAlert>() {


    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isAuthorized(AccessType.VIEW_TYRE)) {// if authorized to view tyres here
            tyreModel.tyresList.observe(viewLifecycleOwner, Observer {
                it?.let { results ->
                    when (results) {
                        Results.Loading -> {
                            binding.tyresCount = 1
                            showProgressBar("Loading worn out tyres...")
                        }
                        is Results.Success<*> -> {
                            endProgressBar()

                            if (!results.data.isNullOrEmpty()) {
                                var wornOutTyres =
                                    results.data
                                        .filter { (it as Tyre).currentThreadDepth?.toInt() ?: 0 <= Const.MIN_TYRE_DEPTH }

                                binding.tyresCount = wornOutTyres.size

                                wornOutTyres = (wornOutTyres as ArrayList<Tyre>).sorted().reversed()
                                if(wornOutTyres.isNotEmpty()) {
                                    val headers =
                                        wornOutTyres[0].data().map { it.first }//col headers text
                                    val colHeader = headers.map { Cell(it) } as ArrayList
                                    val rows = wornOutTyres.map {
                                        it.data().map { Cell(it.second) } as ArrayList
                                    }
                                    val rowHeader =
                                        wornOutTyres.withIndex()
                                            .map { Cell((it.index + 1).toString()) } as ArrayList
                                    initTable(
                                        colHeader = colHeader,
                                        rows = rows,
                                        rowHeader = rowHeader,
                                        tableView = tyres_table,
                                        destination = R.id.action_tyresListFragment_to_tyreHomeDetailsFragment,
                                        tableData = wornOutTyres as ArrayList<TyreWornOutAlert>

                                    )
                                }
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
}