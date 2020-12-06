package com.pet001kambala.namopsfleetmanager.ui.account.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.databinding.FragmentSelectSignUpModeBinding
import com.pet001kambala.namopsfleetmanager.ui.MainActivity
import com.pet001kambala.namopsfleetmanager.ui.account.AccountViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_select_sign_up_mode.*
import kotlinx.coroutines.ExperimentalCoroutinesApi


open class SelectSignUpModeFragment : AbstractAuthFragment() {

    private lateinit var binding: FragmentSelectSignUpModeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding = FragmentSelectSignUpModeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        accountModel.authState.observe(viewLifecycleOwner, Observer {
            val curDestId = navController.currentDestination?.id

            if (it == AccountViewModel.AuthState.EMAIL_NOT_VERIFIED
                && curDestId != R.id.selectLoginModeFragment)
                navController.navigate(R.id.action_selectSignUpModeFragment_to_selectLoginModeFragment)

            else if (it == AccountViewModel.AuthState.AUTHENTICATED) {
                val activity = (activity as MainActivity)
                navController.popBackStack(R.id.homeFragment, false)
            }
        })

        with_email_btn.setOnClickListener {
            navController.navigate(R.id.action_selectSignUpModeFragment_to_emailRegistrationFragment)
        }

        with_phone_btn.setOnClickListener {
            navController.navigate(R.id.action_selectSignUpModeFragment_to_phoneRegistrationFragment)
        }
        have_an_account.setOnClickListener {
            navController.navigate(R.id.action_selectSignUpModeFragment_to_selectLoginModeFragment)
        }
    }
}