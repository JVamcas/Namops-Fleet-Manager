package com.pet001kambala.namopsfleetmanager.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.PhoneAuthProvider
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.repository.AccountRepo
import com.pet001kambala.namopsfleetmanager.ui.account.auth.EmailRegistrationFragment
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.android.synthetic.main.fragment_email_registration.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

class UpdateProfileFragment : EmailRegistrationFragment() {

    @ExperimentalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState)
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        accountModel.currentAccount.observe(viewLifecycleOwner, Observer {

            it?.let {
                account = it
                binding.account = it

                password_layout.visibility = GONE
                re_password_layout.visibility = GONE
                email_layout.visibility = VISIBLE
                cellphone_layout.visibility = VISIBLE

                when (accountModel.getAuthType()) {
                    EmailAuthProvider.PROVIDER_ID -> {
                        email_layout.isEnabled = false
                        cellphone_layout.isEnabled = true
                    }
                    //cannot change your auth type
                    PhoneAuthProvider.PROVIDER_ID -> {
                        email_layout.isEnabled = true
                        cellphone_layout.isEnabled = false
                    }
                }
            }
        })

        new_account_btn.text = getString(R.string.update)
        new_account_btn.setOnClickListener {
            accountModel.viewModelScope.launch {
                showProgressBar("Updating your profile...please wait.")
                val result = AccountRepo().updateAccountDetails(account)
                endProgressBar()
                if (result is Results.Success<*>) {
                    showToast("Update success.")
                    onBackClick()
                }
            }
        }
    }
}