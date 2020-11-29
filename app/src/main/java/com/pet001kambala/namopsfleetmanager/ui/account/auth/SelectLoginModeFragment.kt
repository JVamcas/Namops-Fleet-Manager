package com.pet001kambala.namopsfleetmanager.ui.account.auth

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.ui.account.AccountViewModel
import kotlinx.android.synthetic.main.fragment_select_sign_up_mode.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Subclass of [SelectSignUpModeFragment].
 */
class SelectLoginModeFragment : SelectSignUpModeFragment() {

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with_email_btn.text = getString(R.string.sign_in_with_email)
        with_phone_btn.text = getString(R.string.sign_in_with_phone)
        have_an_account.text = getString(R.string.create_account)

        with_email_btn.setOnClickListener {
            navController.navigate(R.id.action_selectLoginModeFragment_to_emailAuthFragment)
        }
        with_phone_btn.setOnClickListener {
            navController.navigate(R.id.action_selectLoginModeFragment_to_phoneAuthFragment)
        }
        have_an_account.setOnClickListener {
            navController.popBackStack()
        }
        accountModel.authState.observe(viewLifecycleOwner, Observer {
            if(it == AccountViewModel.AuthState.AUTHENTICATED)
                navController.popBackStack(R.id.selectSignUpModeFragment,false)
        })
    }
}
