package com.pet001kambala.namopsfleetmanager.ui.trailer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.databinding.FragmentTrailerRegistrationDetailsBinding
import com.pet001kambala.namopsfleetmanager.repository.TrailerRepo
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.yearOfMake
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.android.synthetic.main.fragment_trailer_registration_details.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

open class TrailerRegistrationDetailsFragment : AbstractTrailerDetailsFragment() {

    lateinit var binding: FragmentTrailerRegistrationDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTrailerRegistrationDetailsBinding.inflate(inflater,container,false)
        binding.trailer  = trailer
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        year_layout.setItems(yearOfMake())

        register.setOnClickListener {
            trailerModel.viewModelScope.launch {
                showProgressBar("Just a moment...")
                val trailerRepo = TrailerRepo()
                val searchResults = trailerRepo.findTrailer(trailer.unitNumber!!)
                if(searchResults is Results.Success<*>){
                    if(!searchResults.data.isNullOrEmpty()){
                        endProgressBar()
                        showToast("Err: Trailer is already registered.")
                        return@launch
                    }
                    val registrationResults = trailerRepo.registerTrailer(trailer)
                    endProgressBar()
                    if (registrationResults is Results.Success<*>){
                        showToast("Registration success.")
                        navController.popBackStack(R.id.trailerListFragment,false)
                    }
                    else parseRepoResults(registrationResults)
                }
                else
                    parseRepoResults(searchResults)
                endProgressBar()
            }
        }
    }
}