package com.pet001kambala.namopsfleetmanager.ui.tyres.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.databinding.FragmentTyreSurveyBinding
import com.pet001kambala.namopsfleetmanager.model.TyreInspectionItem
import com.pet001kambala.namopsfleetmanager.repository.TyreRepo
import com.pet001kambala.namopsfleetmanager.utils.DateUtil.Companion._24
import com.pet001kambala.namopsfleetmanager.utils.DateUtil.Companion.today
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.android.synthetic.main.fragment_tyre_survey.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

class TyreInspectionDetailsFragment : AbstractTyreDetailsFragment() {

    private lateinit var binding: FragmentTyreSurveyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTyreSurveyBinding.inflate(inflater, container, false)
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val survey = TyreInspectionItem()
        binding.tyreSurvey = survey
        binding.tyre = tyre
        tyre_sn_layout.isEnabled = false

        inspect_tyre.setOnClickListener {
            val tyreRepo = TyreRepo()
            survey.apply {
                tyre = super.tyre
                tyreSN = tyre!!.serialNumber!!
                date = today()._24()
            }
            tyreModel.viewModelScope.launch {
                showProgressBar("Recording survey...")
                val surveyRecord = tyreRepo.recordTyreInspection(tyre, survey)
                endProgressBar()
                if (surveyRecord is Results.Success<*>) {
                    showToast("Survey recorded.")
                    navController.popBackStack(R.id.tyresListFragment, false)
                } else parseRepoResults(surveyRecord)
            }
        }
        tyre_current_condition.apply {
            setOnSpinnerItemSelectedListener<String> { _, item ->
                survey.currentCondition = item
            }
            lifecycleOwner = viewLifecycleOwner
        }
    }
}