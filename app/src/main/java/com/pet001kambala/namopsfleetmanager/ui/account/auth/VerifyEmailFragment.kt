package com.pet001kambala.namopsfleetmanager.ui.account.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import com.pet001kambala.namopsfleetmanager.databinding.FragmentVerifyEmailBinding
import com.pet001kambala.namopsfleetmanager.repository.AccountRepo
import kotlinx.coroutines.launch


class VerifyEmailFragment : AbstractAuthFragment() {

    private lateinit var binding: FragmentVerifyEmailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentVerifyEmailBinding.inflate(inflater, container, false)
        binding.reSendLink.setOnClickListener {

            accountModel.viewModelScope.launch {
                val accountRepo = AccountRepo()
                showProgressBar("Sending verification link...")
                val sendResult = accountRepo.sendVerificationEmail()
                endProgressBar()
                parseRepoResults(sendResult)

                accountModel.signOut()
                navController.popBackStack()
            }
        }
        return binding.root
    }
}