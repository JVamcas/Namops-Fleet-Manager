package com.pet001kambala.namopsfleetmanager.ui.account.auth

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.utils.Const
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil
import kotlinx.android.synthetic.main.fragment_phone_auth.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch


class PhoneAuthFragment : AbstractAuthFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_phone_auth, container, false)
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        phone_number.addTextChangedListener(object : PhoneNumberFormattingTextWatcher() {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                super.onTextChanged(p0, p1, p2, p3)
                val phone = phone_number.text.toString()
                val isValidPhone = (phone.isEmpty() || ParseUtil.isValidMobile(phone))
                phone_number.error = if (isValidPhone) null else "Invalid phone number."
                login_btn.isEnabled = ParseUtil.isValidMobile(phone)
            }
        })

        login_btn.setOnClickListener {
            accountModel.viewModelScope.launch {
                val phoneNumber = phone_number.text.toString()
                val bundle = Bundle().also { it.putString(Const.PHONE_NUMBER,phoneNumber) }
                navController.navigate(R.id.action_phoneAuthFragment_to_verifyPhoneFragment,bundle)
            }
        }
    }
}