package com.pet001kambala.namopsfleetmanager.ui.tyres.records

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.model.Cell
import kotlinx.android.synthetic.main.tyres_list_fragment.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

class TyreDetailsFragment : AbstractTyreRecord() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        setHasOptionsMenu(false)
        binding.registerTyre.visibility = View.VISIBLE

        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        register_tyre.visibility = GONE

        binding.tyresCount = 1

        val headers = tyre.data().map { it.first }//col headers text
        val colHeader = headers.map { Cell(it) } as ArrayList
        val rows = tyre.data().map { Cell(it.second) } as ArrayList

        initTable(
            colHeader, arrayListOf(rows), arrayListOf(Cell("1")), tyres_table,
            R.id.action_tyreDetailsFragment_to_tyreHomeDetailsFragment, tyre
        )
    }
}