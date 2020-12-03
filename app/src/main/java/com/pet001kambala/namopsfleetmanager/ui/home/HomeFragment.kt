package com.pet001kambala.namopsfleetmanager.ui.home

import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.databinding.FragmentHomeBinding
import com.pet001kambala.namopsfleetmanager.ui.AbstractFragment
import com.pet001kambala.namopsfleetmanager.ui.account.AccountViewModel
import com.pet001kambala.namopsfleetmanager.utils.AccessType
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
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

        accountModel.currentAccount.observe(viewLifecycleOwner, Observer {
            progress_bar.visibility = VISIBLE
            requireActivity().drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

            it?.let {
                currentAccount = it
                requireActivity().invalidateOptionsMenu()
                requireActivity().drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                progress_bar.visibility = GONE
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
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.accounts -> {
                navController.navigate(R.id.action_homeFragment_to_usersListFragment)
            }
        }

        return super.onOptionsItemSelected(item)
    }
}