package com.pet001kambala.namopsfleetmanager.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.pet001kambala.namopsfleetmanager.databinding.UserViewBinding
import com.pet001kambala.namopsfleetmanager.model.Account
import com.pet001kambala.namopsfleetmanager.ui.AbstractAdapter

class UsersListAdapter(
    mListener: ModelViewClickListener<Account>
) : AbstractAdapter<Account, UsersListAdapter.ViewHolder>(mListener) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = UserViewBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = DataBindingUtil.getBinding<UserViewBinding>(holder.itemView)
        val account = get(position)
        binding!!.account = account

        binding.root.setOnClickListener {
            mListener.onModelClick(account)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}