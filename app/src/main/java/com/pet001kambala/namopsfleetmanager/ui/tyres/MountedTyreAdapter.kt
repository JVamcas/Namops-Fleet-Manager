package com.pet001kambala.namopsfleetmanager.ui.tyres

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.pet001kambala.namopsfleetmanager.databinding.MountedTyreLayoutBinding
import com.pet001kambala.namopsfleetmanager.model.Tyre
import com.pet001kambala.namopsfleetmanager.ui.AbstractAdapter

class MountedTyreAdapter(
    private val mL: ModelViewClickListener<Tyre>
) : AbstractAdapter<Tyre, MountedTyreAdapter.ViewHolder>(mListener = mL) {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var tyre: Tyre
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            MountedTyreLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder((binding.root))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = DataBindingUtil.getBinding<MountedTyreLayoutBinding>(holder.itemView)
        val tyre = get(position)
        holder.tyre = tyre
        binding!!.tyre = holder.tyre

        holder.itemView.setOnClickListener {
            mL.onModelClick(tyre)
        }
    }
}