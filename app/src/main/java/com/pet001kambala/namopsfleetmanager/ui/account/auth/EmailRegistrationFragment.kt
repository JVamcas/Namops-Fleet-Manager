package com.pet001kambala.namopsfleetmanager.ui.account.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.databinding.FragmentEmailRegistrationBinding
import com.pet001kambala.namopsfleetmanager.repository.AccountRepo
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.android.synthetic.main.fragment_email_registration.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch


open class EmailRegistrationFragment : AbstractAuthFragment() {

    lateinit var binding: FragmentEmailRegistrationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding = FragmentEmailRegistrationBinding.inflate(inflater,container,false)
        binding.account = account
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        new_account_btn.setOnClickListener {
            showProgressBar("Creating your account... Please wait!")
            accountModel.viewModelScope.launch {
                val accountRepo = AccountRepo()
                val password = password.text.toString()
                val createResults = accountRepo.newUserWithEmailAndPassword(account,password)
                endProgressBar()
                if (createResults is Results.Success<*>){
                    showToast("Account creation success.")
                    navController.popBackStack(R.id.selectSignUpModeFragment,false)
                }
                else parseRepoResults(createResults)
            }
        }
    }
}