package com.pet001kambala.namopsfleetmanager.ui.tyres.records

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.pet001kambala.namopsfleetmanager.model.Cell
import com.pet001kambala.namopsfleetmanager.model.TyreMountItem
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.android.synthetic.main.tyres_list_fragment.*
import kotlinx.coroutines.ExperimentalCoroutinesApi


open class TyreMountRecordsFragment : AbstractTyreRecord<TyreMountItem>() {

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        tyreModel.loadTyreMountRecord(tyre.serialNumber!!).observe(viewLifecycleOwner, Observer {
            it?.let { results ->
                when (results) {
                    Results.Loading -> showProgressBar("Loading tyre mount record...")
                    is Results.Success<*> -> {
                        endProgressBar()

                        binding.tyresCount = results.data?.size ?: 0

                        if (!results.data.isNullOrEmpty()) {
                            binding.tyresCount = results.data.size
                            val headers = results.data[0].data().map { it.first }//col headers text
                            val colHeader = headers.map { Cell(it) } as ArrayList
                            val rows =
                                results.data.map { it.data().map { Cell(it.second) } as ArrayList }
                            val rowHeader =
                                results.data.withIndex()
                                    .map { Cell((it.index + 1).toString()) } as ArrayList
                            initTable(
                                colHeader = colHeader,
                                rows = rows,
                                rowHeader = rowHeader,
                                tableView = tyres_table,
                                destination = null,
                                tableData = results.data as ArrayList<TyreMountItem>
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