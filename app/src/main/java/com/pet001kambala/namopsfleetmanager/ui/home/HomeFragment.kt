package com.pet001kambala.namopsfleetmanager.ui.home

import android.os.Bundle
import android.view.*
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mikepenz.actionitembadge.library.ActionItemBadge
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.databinding.FragmentHomeBinding
import com.pet001kambala.namopsfleetmanager.ui.AbstractFragment
import com.pet001kambala.namopsfleetmanager.ui.account.AccountViewModel
import com.pet001kambala.namopsfleetmanager.ui.notifications.NotificationViewModel
import com.pet001kambala.namopsfleetmanager.utils.AccessType
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.isIncompleteAccount
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

class HomeFragment : AbstractFragment() {

    private lateinit var binding: FragmentHomeBinding
    private val accountModel: AccountViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        accountModel.authState.observe(viewLifecycleOwner, Observer {
            if(it == AccountViewModel.AuthState.AUTHENTICATED)
                showProgressBar("Loading your profile...")
        })
        accountModel.currentAccount.observe(viewLifecycleOwner, Observer {
            it?.let {
                endProgressBar()
                if (it.isIncompleteAccount())
                    navController.navigate(R.id.action_global_accountGraph)

                else {
                    requireActivity().invalidateOptionsMenu() //force refresh app home_menu
                    currentAccount = it
                    requireActivity().drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                }
            }
        })
    }


    override fun onBackClick() {
        requireActivity().finish()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)
        menu.findItem(R.id.accounts).isEnabled = isAuthorized(AccessType.ADMIN)

        val notificationView = menu.findItem(R.id.nav_notifications)
        notificationView.isEnabled = isAuthorized(AccessType.ADMIN)
    }

    @ExperimentalCoroutinesApi
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val notificationView = menu.findItem(R.id.nav_notifications)

        val notificationModel = ViewModelProvider(this).get(NotificationViewModel::class.java)
        notificationModel.notificationsList.observe(this, Observer {
            it?.apply {
                if (it is Results.Success<*> && !it.data.isNullOrEmpty()) {
                    ActionItemBadge.update(
                        requireActivity(),
                        notificationView,
                        notificationView.icon,
                        ActionItemBadge.BadgeStyles.RED,
                        it.data.size
                    )
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.accounts -> {
                navController.navigate(R.id.action_homeFragment_to_usersListFragment)
            }

            R.id.nav_notifications ->{
                navController.navigate(R.id.action_homeFragment_to_notificationsListFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}