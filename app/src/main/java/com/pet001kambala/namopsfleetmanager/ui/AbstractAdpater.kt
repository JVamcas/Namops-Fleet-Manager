package com.pet001kambala.namopsfleetmanager.ui

import androidx.annotation.UiThread
import androidx.recyclerview.widget.RecyclerView
import com.pet001kambala.namopsfleetmanager.model.AbstractModel

abstract class AbstractAdapter<K : AbstractModel, T : RecyclerView.ViewHolder>(
    var mListener: ModelViewClickListener<K>
) : RecyclerView.Adapter<T>() {
    var modelList: ArrayList<K> = ArrayList()
        @UiThread
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    open fun delete(pos: Int): K {
        val mItem: K = modelList.removeAt(pos)
        notifyItemRemoved(pos)
        return mItem
    }

    open fun undoDelete(pos: Int, model: K) {
        if (modelList.isEmpty()) modelList.add(model) else modelList.add(pos, model)
        notifyItemInserted(pos)
    }

    open fun add(model: K) {
        modelList.add(model)
        notifyItemInserted(itemCount - 1)
    }

    open fun get(index: Int): K {
        return modelList[index]
    }

    open fun get(model: K): Int {
        return if (modelList.contains(model)) modelList.indexOf(model) else -1
    }

    override fun getItemCount(): Int {
        return modelList.size
    }

    open fun updateItem(item: K) {
        val index = modelList.indexOf(item)
        modelList[index] = item
        notifyItemInserted(index)
    }

    interface ModelViewClickListener<K : AbstractModel?> {
        fun onEditModel(model: K, pos: Int)
        fun onDeleteModel(modelPos: Int)
        fun onModelClick(model: K)
        fun onModelIconClick(model: K)
    }
}