package com.pet001kambala.namopsfleetmanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import com.evrencoskun.tableview.adapter.AbstractTableAdapter
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.databinding.TableViewCellLayoutBinding
import com.pet001kambala.namopsfleetmanager.databinding.TableViewColumnHeaderLayoutBinding
import com.pet001kambala.namopsfleetmanager.databinding.TableViewRowHeaderLayoutBinding
import com.pet001kambala.namopsfleetmanager.model.AbstractModel
import com.pet001kambala.namopsfleetmanager.model.Cell
import com.pet001kambala.namopsfleetmanager.utils.Const
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.toJson

class DataTableAdapter(
    private val navController: NavController,
    private val destination: Int? = null,
    private val model: AbstractModel? = null
    ):
    AbstractTableAdapter<Cell, Cell, Cell>() {
    class CellViewHolder(
        var view: View,
        var data: Cell? = null
    ) : AbstractViewHolder(view)

    class ColumnHeaderViewHolder(
        var view: View,
        var data: Cell? = null
    ) : AbstractViewHolder(view)

    class RowHeaderViewHolder(
        var view: View,
        var data: Cell? = null
    ) : AbstractViewHolder(view)

    override fun onCreateColumnHeaderViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ColumnHeaderViewHolder {
        val binding = TableViewColumnHeaderLayoutBinding.inflate(
            LayoutInflater.from(
                parent.context
            ), parent, false
        )
        return ColumnHeaderViewHolder(
            binding.root
        )
    }

    override fun onBindColumnHeaderViewHolder(
        holder: AbstractViewHolder,
        columnHeaderItemModel: Cell?,
        columnPosition: Int
    ) {
        DataBindingUtil.getBinding<TableViewColumnHeaderLayoutBinding>(holder.itemView)?.apply {
            columnHeaderTextView.text = columnHeaderItemModel?.data
        }
    }

    override fun onBindRowHeaderViewHolder(
        holder: AbstractViewHolder,
        rowHeaderItemModel: Cell?,
        rowPosition: Int
    ) {
        DataBindingUtil.getBinding<TableViewRowHeaderLayoutBinding>(holder.itemView)?.apply {
            rowHeaderTextView.text = rowHeaderItemModel?.data
        }
    }

    override fun onCreateRowHeaderViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RowHeaderViewHolder {
        val binding = TableViewRowHeaderLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return RowHeaderViewHolder(
            (binding.root)
        )
    }

    override fun getCellItemViewType(position: Int): Int {

        return position
    }

    override fun onCreateCellViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {

        val binding = TableViewCellLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CellViewHolder(
            binding.root
        )
    }

    override fun onCreateCornerView(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context)
            .inflate(R.layout.table_view_corner_layout, parent, false)
    }

    override fun onBindCellViewHolder(
        holder: AbstractViewHolder,
        cellItemModel: Cell?,
        columnPosition: Int,
        rowPosition: Int
    ) {
        val binding = DataBindingUtil.getBinding<TableViewCellLayoutBinding>(holder.itemView)
        val data = cellItemModel?.data
        binding?.apply { cellData.text = data }
        binding?.root?.setOnClickListener {
            destination?.let{
                val bundle = Bundle()
                bundle.putString(Const.ROW_POS, rowPosition.toString())
                bundle.putString(Const.TYRE,model.toJson())
                navController.navigate(destination, bundle)
            }
        }
    }

    override fun getColumnHeaderItemViewType(position: Int): Int {
        return 0
    }

    override fun getRowHeaderItemViewType(position: Int): Int {
        return 0
    }
}