package com.pet001kambala.namopsfleetmanager.ui

import android.os.Bundle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pet001kambala.namopsfleetmanager.model.AbstractModel

abstract class AbstractListFragment<K: AbstractModel, T: RecyclerView.ViewHolder>
    : AbstractFragment(), AbstractAdapter.ModelViewClickListener<K> {

    lateinit var mAdapter: AbstractAdapter<K,T>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAdapter()
    }

    abstract fun initAdapter()

    fun handleRecycleView(recyclerView: RecyclerView, mListener: AbstractAdapter.ModelViewClickListener<K>) {

        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)

        val swipeCallback = object : ItemSwipeCallback<K, T>(mAdapter, mListener) {}
        val touchHelper = ItemTouchHelper(swipeCallback)
        touchHelper.attachToRecyclerView(recyclerView)
    }
}