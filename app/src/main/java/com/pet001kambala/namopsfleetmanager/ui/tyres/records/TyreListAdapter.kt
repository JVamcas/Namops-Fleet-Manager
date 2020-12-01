package com.pet001kambala.namopsfleetmanager.ui.tyres.records

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.pet001kambala.namopsfleetmanager.databinding.TyreLayoutBinding
import com.pet001kambala.namopsfleetmanager.model.Tyre
import com.pet001kambala.namopsfleetmanager.ui.AbstractAdapter

class TyreListAdapter(
    mListener: ModelViewClickListener<Tyre>
) :
    AbstractAdapter<Tyre, TyreListAdapter.ViewHolder>(mListener) {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var tyre: Tyre
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = TyreLayoutBinding.inflate(LayoutInflater.from(parent.context), null, false)
        return ViewHolder(
            binding.root
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tyre = get(position)
        val binding = DataBindingUtil.getBinding<TyreLayoutBinding>(holder.itemView)
        binding!!.tyre = holder.tyre
        binding.root.setOnClickListener {
            mListener.onModelClick(holder.tyre)
        }
    }
}