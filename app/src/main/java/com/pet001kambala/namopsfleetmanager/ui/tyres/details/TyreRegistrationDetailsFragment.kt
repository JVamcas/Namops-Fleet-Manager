package com.pet001kambala.namopsfleetmanager.ui.tyres.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import com.google.android.material.datepicker.MaterialDatePicker
import com.pet001kambala.namopsfleetmanager.databinding.FragmentTyreRegistrationBinding
import com.pet001kambala.namopsfleetmanager.repository.TyreRepo
import com.pet001kambala.namopsfleetmanager.utils.DateUtil.Companion.fromLong
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.getSNPrefix
import com.pet001kambala.namopsfleetmanager.utils.Results
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.android.synthetic.main.fragment_tyre_registration.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.text.DateFormatSymbols
import java.util.*


open class TyreRegistrationDetailsFragment : AbstractTyreDetailsFragment(),
    DatePickerDialog.OnDateSetListener {

    lateinit var binding: FragmentTyreRegistrationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTyreRegistrationBinding.inflate(inflater, container, false)
        binding.tyre = tyre

        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        tyre.apply {
            serialNumber = getSNPrefix()
            purchasePrice = "NAD"
        }

        recommended_pressure_layout.isEnabled = false

        register_tyre.setOnClickListener {
            showProgressBar("Just a moment...")
            val tyreRepo = TyreRepo()
            tyreModel.viewModelScope.launch {
                val searchResults = tyreRepo.findTyre(tyre.serialNumber!!)
                if (searchResults is Results.Success<*>) {
                    if (!searchResults.data.isNullOrEmpty()) {
                        endProgressBar()
                        showToast("Err: A tyre is registered on that serial number.")
                        return@launch
                    }

                    val registrationResults = tyreRepo.registerTyre(tyre)
                    endProgressBar()
                    if (registrationResults is Results.Success<*>) {
                        showToast("Registration success!")
                        navController.popBackStack()
                    } else parseRepoResults(registrationResults)
                } else
                    parseRepoResults(searchResults)
                endProgressBar()
            }
        }
        val now = Calendar.getInstance()
        val picker = DatePickerDialog.newInstance(
            this,
            now[Calendar.YEAR],  // Initial year selection
            now[Calendar.MONTH],  // Initial month selection
            now[Calendar.DAY_OF_MONTH] // Inital day selection
        )

        tyre_purchase_date.setOnClickListener {
            if (!picker.isVisible)
                picker.show(requireActivity().supportFragmentManager, "Datepickerdialog")
        }
    }

    override fun onDateSet(
        view: DatePickerDialog?,
        year: Int,
        monthOfYear: Int,
        dayOfMonth: Int
    ) {
        val dateFormat = DateFormatSymbols()
        val day = "$dayOfMonth ${dateFormat.months[monthOfYear]} $year"
        tyre_purchase_date.setText(day)
    }
}