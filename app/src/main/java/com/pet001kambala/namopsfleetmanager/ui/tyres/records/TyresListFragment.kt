package com.pet001kambala.namopsfleetmanager.ui.tyres.records

import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.model.Cell
import com.pet001kambala.namopsfleetmanager.model.Tyre
import com.pet001kambala.namopsfleetmanager.utils.DateUtil.Companion._24
import com.pet001kambala.namopsfleetmanager.utils.DateUtil.Companion.today
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.android.synthetic.main.tyres_list_fragment.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

open class TyresListFragment : AbstractTyreRecord() {

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

        register_tyre.setOnClickListener {
            navController.navigate(R.id.action_tyresListFragment_to_tyreRegistrationFragment)
        }

        tyreModel.tyresList.observe(viewLifecycleOwner, Observer {
            it?.let { results ->
                when (results) {
                    Results.Loading -> showProgressBar("Loading tyres...")
                    is Results.Success<*> -> {
                        endProgressBar()

                        binding.tyresCount = results.data?.size?: 0

                        if (!results.data.isNullOrEmpty()) {

                            val headers = results.data[0].data().map { it.first }//col headers text
                            val colHeader = headers.map { Cell(it) } as ArrayList
                            val rows = results.data.map { it.data().map { Cell(it.second) } as ArrayList }
                            val rowHeader =
                                results.data.withIndex()
                                    .map { Cell((it.index + 1).toString()) } as ArrayList
                            initTable(colHeader, rows, rowHeader, tyres_table,
                                R.id.action_tyresListFragment_to_tyreHomeDetailsFragment)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.tyres_list_menu, menu)
    }

    @ExperimentalCoroutinesApi
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.find_tyre ->{
                navController.navigate(R.id.action_tyresListFragment_to_findTyreDetailsFragment)
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