package com.pet001kambala.namopsfleetmanager.ui.account.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import com.pet001kambala.namopsfleetmanager.databinding.FragmentResetEmailPasswordBinding
import com.pet001kambala.namopsfleetmanager.repository.AccountRepo
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.android.synthetic.main.fragment_reset_email_password.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

class ResetEmailPasswordFragment : AbstractAuthFragment() {


    private lateinit var binding: FragmentResetEmailPasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentResetEmailPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reset_password_btn.setOnClickListener {
            val email = email_reset.text.toString()
            if (!ParseUtil.isValidEmail(email))
                showToast("Invalid email address.")
            else {
                accountModel.viewModelScope.launch {
                    showProgressBar("Sending password reset email...")
                    val sendResults = AccountRepo().resetPassword(email)
                    endProgressBar()
                    if (sendResults is Results.Success<*>) {
                        showToast("A password reset link has been sent to your email.")
                        navController.popBackStack()
                    }
                    else parseRepoResults(sendResults)
                }
            }
        }
    }
}