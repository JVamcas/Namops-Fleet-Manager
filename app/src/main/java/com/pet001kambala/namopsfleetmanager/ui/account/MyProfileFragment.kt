package com.pet001kambala.namopsfleetmanager.ui.account

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.databinding.FragmentMyProfileBinding
import com.pet001kambala.namopsfleetmanager.ui.AbstractFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

class MyProfileFragment : AbstractFragment() {
    val accountModel: AccountViewModel by activityViewModels()
    private lateinit var binding: FragmentMyProfileBinding

    @ExperimentalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        binding = FragmentMyProfileBinding.inflate(inflater,container,false)

        accountModel.currentAccount.observe(viewLifecycleOwner, Observer {
            binding.account = it
        })
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.profile_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.update_account -> {navController.navigate(R.id.action_myProfileFragment_to_updateProfileFragment)}
        }
        return super.onOptionsItemSelected(item)
    }
}