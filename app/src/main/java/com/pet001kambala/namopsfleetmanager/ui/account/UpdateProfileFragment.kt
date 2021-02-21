package com.pet001kambala.namopsfleetmanager.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.databinding.FragmentUpdateAccountBinding
import com.pet001kambala.namopsfleetmanager.repository.AccountRepo
import com.pet001kambala.namopsfleetmanager.ui.account.auth.AbstractAuthFragment
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.isIncompleteAccount
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.stripCountryCode
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.android.synthetic.main.fragment_email_registration.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

class UpdateProfileFragment : AbstractAuthFragment() {

    private lateinit var binding: FragmentUpdateAccountBinding

    @ExperimentalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentUpdateAccountBinding.inflate(inflater,container,false)

        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var isMissingCred = false
        accountModel.currentAccount.observe(viewLifecycleOwner, Observer {

            it?.let {
                account = it
                it.cellphone = Firebase.auth.currentUser?.phoneNumber.stripCountryCode()
                isMissingCred = it.isIncompleteAccount()
                binding.account = it
                binding.isEmailAccount = AccountRepo().isEmailAuth()
            }
        })

        new_account_btn.text = getString(R.string.update)
        new_account_btn.setOnClickListener {
            accountModel.viewModelScope.launch {
                val accountRepo = AccountRepo()
                showProgressBar("Updating your profile...please wait.")
                val result =
                    if (isMissingCred)
                        accountRepo.createUserWithEmailAndPassword(account)
                    else
                        AccountRepo().updateAccountDetails(account)
                endProgressBar()
                if (result is Results.Success<*>) {
                    showToast("Update success.")
                    navController.popBackStack()
                }
            }
        }
    }

    override fun onBackClick() {
        navController.popBackStack()
    }
}