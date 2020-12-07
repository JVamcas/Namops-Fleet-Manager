package com.pet001kambala.namopsfleetmanager.ui.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.pet001kambala.namopsfleetmanager.databinding.NotificationViewBinding
import com.pet001kambala.namopsfleetmanager.model.Notification
import com.pet001kambala.namopsfleetmanager.ui.AbstractAdapter

class NotificationListAdapter(
    mListener: ModelViewClickListener<Notification>,
) : AbstractAdapter<Notification, NotificationListAdapter.ViewHolder>(mListener) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            NotificationViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val binding = DataBindingUtil.getBinding<NotificationViewBinding>(holder.itemView)
        val notification = get(position)
        binding!!.notification = notification.also { it.body = it.body?.replace("\n","") }
        binding.root.setOnClickListener {
            mListener.onModelClick(notification)
        }
    }
}