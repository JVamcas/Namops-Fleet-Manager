package com.pet001kambala.namopsfleetmanager.ui.account.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.databinding.FragmentVerifyPhoneBinding
import com.pet001kambala.namopsfleetmanager.model.Account
import com.pet001kambala.namopsfleetmanager.model.PhoneAuthCred
import com.pet001kambala.namopsfleetmanager.repository.AccountRepo
import com.pet001kambala.namopsfleetmanager.utils.BindingUtil
import com.pet001kambala.namopsfleetmanager.utils.Const
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.convert
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.android.synthetic.main.fragment_verify_phone.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class VerifyPhoneFragment : AbstractAuthFragment() {

    private lateinit var phoneNumber: String
    private lateinit var binding: FragmentVerifyPhoneBinding
    private var account: Account? = null
    private lateinit var verificationId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val accountJson = it.getString(Const.ACCOUNT)
            accountJson?.let {
                account = accountJson.convert()
                phoneNumber = account?.cellphone!!
            }
            val phoneJson = it.getString(Const.PHONE_NUMBER)
            phoneJson?.let {
                phoneNumber = it
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentVerifyPhoneBinding.inflate(inflater, container, false)

        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sendVerificationCode()

        submit_btn.setOnClickListener {
            accountModel.viewModelScope.launch {
                val verificationCode = verification_code.text.toString()
                val phoneCred = PhoneAuthProvider.getCredential(verificationId, verificationCode)
                signInAndCreateUserAccount(account, phoneCred)
            }
        }

        verification_code.addTextChangedListener(
            object : BindingUtil.Companion.TextChangeLister() {
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    super.onTextChanged(p0, p1, p2, p3)
                    val code = p0.toString()
                    val isValidPhoneCode = (code.isEmpty() || ParseUtil.isValidAuthCode(code))
                    verification_code.error =
                        if (isValidPhoneCode) null else "Invalid verification code."
                    submit_btn.isEnabled = ParseUtil.isValidAuthCode(code)
                }
            }
        )
        resend_verification_code.setOnClickListener {
            sendVerificationCode()
        }
    }

    @ExperimentalCoroutinesApi
    private fun sendVerificationCode() {
        accountModel.viewModelScope.launch {
            val accountRepo = AccountRepo()
            accountRepo.verifyPhoneNumber(phoneNumber, requireActivity()).collect {
                when (it) {
                    is Results.Loading -> showProgressBar("Sending verification code.")
                    is Results.Success<*> -> {
                        endProgressBar()
                        val code = it.code
                        val phoneAuthCred = (it.data?.first() as? PhoneAuthCred)
                        if (code == Results.Success.CODE.PHONE_VERIFY_CODE_SENT) {
                            verificationId = phoneAuthCred?.verificationId!!
                            showToast("Phone verification code sent.")
                        } else {
                            //Verification code detected automatically by android
                            val phoneCred = phoneAuthCred?.phoneAuthCredential!!
                            signInAndCreateUserAccount(account, phoneCred)
                        }
                    }
                    else -> parseRepoResults(it)
                }
            }
        }
    }

    private fun signInAndCreateUserAccount(
        account: Account? = null,
        phoneAuthCredential: PhoneAuthCredential
    ) {
        accountModel.viewModelScope.launch {
            val accountRepo = AccountRepo()
            val createMsg =
                if (account == null) "Authenticating..." else "Creating your account... please wait!"
            showProgressBar(createMsg)
            val signInResults =
                accountRepo.signInWithPhoneAuthCredential(phoneAuthCredential)

            if (signInResults is Results.Success<*>) {
                account?.let {
                    val accountCreationResults =
                        accountRepo.createUserWithPhone(account)
                    endProgressBar()
                    if (accountCreationResults is Results.Success<*>) {
                        showToast("Auth success.")
                        navController.popBackStack(R.id.selectSignUpModeFragment, false)
                    } else parseRepoResults(accountCreationResults)
                    return@launch
                }
                val resultsMsg =
                    if (account == null) "Auth success..." else "Account created successfully."
                showToast(resultsMsg)
                navController.popBackStack(R.id.selectSignUpModeFragment, false)
            } else parseRepoResults(signInResults)
            endProgressBar()
        }
    }
}