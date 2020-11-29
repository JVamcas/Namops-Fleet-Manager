package com.pet001kambala.namopsfleetmanager.ui.tyres.vendor

import android.os.Bundle
import android.view.*
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.databinding.FragmentTyreVendorListBinding
import com.pet001kambala.namopsfleetmanager.model.Cell
import com.pet001kambala.namopsfleetmanager.model.Tyre
import com.pet001kambala.namopsfleetmanager.model.TyreVendor
import com.pet001kambala.namopsfleetmanager.ui.AbstractTableFragment
import com.pet001kambala.namopsfleetmanager.ui.tyres.TyresViewModel
import com.pet001kambala.namopsfleetmanager.utils.DateUtil
import com.pet001kambala.namopsfleetmanager.utils.DateUtil.Companion._24
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.android.synthetic.main.fragment_tyre_vendor_list.*
import kotlinx.android.synthetic.main.fragment_tyre_vendor_registration.*
import kotlinx.android.synthetic.main.tyres_list_fragment.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

class TyreVendorListFragment : AbstractTableFragment() {

    private lateinit var binding: FragmentTyreVendorListBinding
    val tyreModel: TyresViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        setHasOptionsMenu(true)
        binding = FragmentTyreVendorListBinding.inflate(inflater, container, false)

        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        register_vendor.setOnClickListener {
            navController.navigate(R.id.action_tyreVendorListFragment_to_tyreVendorRegistrationDetailsFragment)
        }
        tyreModel.tyreVendorsList.observe(viewLifecycleOwner, Observer {
            it?.let { results ->
                when (results) {
                    Results.Loading -> showProgressBar("Loading tyre vendors...")
                    is Results.Success<*> -> {
                        endProgressBar()
                        if (!results.data.isNullOrEmpty()) {
                            binding.vendorsCount = results.data.size
                            val headers = results.data[0].data().map { it.first }//col headers text
                            val colHeader = headers.map { Cell(it) } as ArrayList
                            val rows = results.data.map { it.data().map { Cell(it.second) } as ArrayList }
                            val rowHeader =
                                results.data.withIndex()
                                    .map { Cell((it.index + 1).toString()) } as ArrayList
                            initTable(colHeader, rows, rowHeader, tyre_vendor_table,
                                R.id.action_tyreVendorListFragment_to_tyreVendorHomeFragment)
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
        inflater.inflate(R.menu.tyre_vendor_list_menu, menu)
    }
    @ExperimentalCoroutinesApi
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.export_all_history -> {
                if (isStoragePermissionGranted()) {//permission must have been granted
                    tyreModel.tyreVendorsList.value?.apply {
                        val today = DateUtil.today()._24()
                        (this as? Results.Success<*>)?.data?.let {
                            toStorage("Namops Tyre Vendors Record_$today.xlsx", arrayListOf(today),
                                arrayListOf(it.map { it as TyreVendor } as ArrayList<TyreVendor>)
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