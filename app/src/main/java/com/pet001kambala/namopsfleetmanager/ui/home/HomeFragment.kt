package com.pet001kambala.namopsfleetmanager.ui.home

import android.os.Bundle
import android.view.*
import androidx.drawerlayout.widget.DrawerLayout
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.databinding.FragmentHomeBinding
import com.pet001kambala.namopsfleetmanager.ui.AbstractFragment
import com.pet001kambala.namopsfleetmanager.ui.MainActivity
import kotlinx.android.synthetic.main.activity_main.*

class HomeFragment : AbstractFragment() {

    private lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        setHasOptionsMenu(true)
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val activity = (activity as MainActivity)
        activity.drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    override fun onBackClick() {
        requireActivity().finish()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.accounts -> {
                navController.navigate(R.id.action_homeFragment_to_usersListFragment)
            }
        }

        return super.onOptionsItemSelected(item)
    }
}