package com.pet001kambala.namopsfleetmanager.ui.vehicles

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.pet001kambala.namopsfleetmanager.databinding.VehicleLayoutBinding
import com.pet001kambala.namopsfleetmanager.model.Vehicle
import com.pet001kambala.namopsfleetmanager.ui.AbstractAdapter

class VehicleListAdapter(
    mListener: ModelViewClickListener<Vehicle>
) :
    AbstractAdapter<Vehicle, VehicleListAdapter.ViewHolder>(mListener) {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var vehicle: Vehicle
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = VehicleLayoutBinding.inflate(LayoutInflater.from(parent.context), null, false)
        return ViewHolder(
            binding.root
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.vehicle = get(position)
        val binding = DataBindingUtil.getBinding<VehicleLayoutBinding>(holder.itemView)
        binding!!.vehicle = holder.vehicle
        binding.root.setOnClickListener {
            mListener.onModelClick(holder.vehicle)
        }
    }
}