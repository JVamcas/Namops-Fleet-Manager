package com.pet001kambala.namopsfleetmanager.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pet001kambala.namopsfleetmanager.databinding.FragmentAboutDeveloperBinding
import com.pet001kambala.namopsfleetmanager.model.Account
import com.pet001kambala.namopsfleetmanager.ui.AbstractFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

class AboutDeveloperFragment : AbstractFragment() {

    private lateinit var binding: FragmentAboutDeveloperBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding = FragmentAboutDeveloperBinding.inflate(inflater, container, false)
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.account = Account().apply {
            name = "Petrus Mesias Kambala"
            cellphone = "+264 81 326 4666"
            email = "kmbpet001@myuct.ac.za"
        }
    }
}