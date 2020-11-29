package com.pet001kambala.namopsfleetmanager.ui.account.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.databinding.FragmentEmailAuthBinding
import com.pet001kambala.namopsfleetmanager.model.Auth
import com.pet001kambala.namopsfleetmanager.model.AuthType
import com.pet001kambala.namopsfleetmanager.repository.AccountRepo
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.android.synthetic.main.fragment_email_auth.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch


class EmailAuthFragment : AbstractAuthFragment() {

    lateinit var binding: FragmentEmailAuthBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentEmailAuthBinding.inflate(inflater, container, false)
        binding.auth = Auth()
        binding.authType = AuthType.EMAIL
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        login_btn.setOnClickListener {
            val auth = binding.auth!!
            accountModel.viewModelScope.launch {
                showProgressBar("Authenticating...")
                val authResults =
                    AccountRepo().authenticateWithEmailAndPassword(
                        auth.idMailCell!!,
                        auth.password?:""
                    )
                endProgressBar()
                if (authResults is Results.Success<*>)
                    navController.popBackStack(R.id.selectSignUpModeFragment, false)
                parseRepoResults(authResults)
            }
        }

        create_new_account.setOnClickListener {
            navController.popBackStack(R.id.selectSignUpModeFragment, false)
        }

        reset_password.setOnClickListener {
            navController.navigate(R.id.action_emailAuthFragment_to_resetEmailPasswordFragment)
        }
    }
}