package com.pet001kambala.namopsfleetmanager.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.databinding.FragmentNotifactionsListBinding
import com.pet001kambala.namopsfleetmanager.model.Notification
import com.pet001kambala.namopsfleetmanager.repository.NotificationRepo
import com.pet001kambala.namopsfleetmanager.ui.AbstractListFragment
import com.pet001kambala.namopsfleetmanager.utils.Const
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.android.synthetic.main.fragment_notifactions_list.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

class NotificationsListFragment :
    AbstractListFragment<Notification, NotificationListAdapter.ViewHolder>() {


    private lateinit var binding: FragmentNotifactionsListBinding
    private val notificationModel: NotificationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentNotifactionsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleRecycleView(notification_recycler, this)

        notificationModel.notificationsList.observe(viewLifecycleOwner, Observer {
            when (it) {
                Results.loading() -> {
                    binding.notificationsCount = 1
                    showProgressBar("Just a moment...")
                }
                is Results.Success<*> -> {
                    endProgressBar()
                    // cannot issue yourself permission
                    mAdapter.modelList = it.data?.map { it as Notification } as ArrayList<Notification>
                    binding.notificationsCount = mAdapter.itemCount
                }
                else -> {
                    endProgressBar()
                    parseRepoResults(it)
                }
            }
        })
    }

    override fun initAdapter() {
        mAdapter = NotificationListAdapter(this)
    }

    override fun onEditModel(model: Notification, pos: Int) {

    }

    override fun onDeleteModel(modelPos: Int) {

    }

    override fun onModelClick(model: Notification) {
        when (model.type) {
            Const.TYRE_WORN_OUT -> {
                notificationModel.viewModelScope.launch {
                    NotificationRepo().updateNotification(
                        model.also { it.seen = true })
                }
                navController.navigate(R.id.action_notificationsListFragment_to_warnOutTyreListFragment)
            }
        }
    }

    override fun onModelIconClick(model: Notification) {

    }
}