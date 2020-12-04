package com.pet001kambala.namopsfleetmanager.ui.account.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.databinding.FragmentPhoneRegistrationBinding
import com.pet001kambala.namopsfleetmanager.utils.Const
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.toJson
import kotlinx.android.synthetic.main.fragment_phone_registration.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch


class PhoneRegistrationFragment : AbstractAuthFragment() {

    lateinit var binding: FragmentPhoneRegistrationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentPhoneRegistrationBinding.inflate(inflater,container,false)
        binding.account = account
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        new_account_btn.setOnClickListener {
           accountModel.viewModelScope.launch {
               val bundle = Bundle().also {
                   it.putString(Const.ACCOUNT, account.toJson())
               }
               navController.navigate(R.id.action_phoneRegistrationFragment_to_verifyPhoneFragment,bundle)
           }
        }
    }
}