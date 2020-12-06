package com.pet001kambala.namopsfleetmanager.ui

import com.evrencoskun.tableview.TableView
import com.pet001kambala.namopsfleetmanager.model.AbstractModel
import com.pet001kambala.namopsfleetmanager.model.Cell

abstract class AbstractTableFragment<K : AbstractModel> :
    AbstractFragment() {
    fun initTable(
        colHeader: ArrayList<Cell>,
        rows: List<ArrayList<Cell>>,
        rowHeader: ArrayList<Cell>,
        tableView: TableView,
        destination: Int? = null, //where to go if row or column clicked
        tableData: List<K>
    ) {
        val tableAdapter = DataTableAdapter(navController, tableData, destination)
        tableView.setAdapter(tableAdapter)
        tableAdapter.setAllItems(colHeader, rowHeader, rows)
    }
}