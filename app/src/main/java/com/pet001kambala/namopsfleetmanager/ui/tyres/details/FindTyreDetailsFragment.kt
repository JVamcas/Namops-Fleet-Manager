package com.pet001kambala.namopsfleetmanager.ui.tyres.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.databinding.FragmentFindTyreDetailsBinding
import com.pet001kambala.namopsfleetmanager.repository.TyreRepo
import com.pet001kambala.namopsfleetmanager.utils.Const
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.toJson
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.android.synthetic.main.fragment_find_tyre_details.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

class FindTyreDetailsFragment : AbstractTyreDetailsFragment() {
    private lateinit var binding: FragmentFindTyreDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFindTyreDetailsBinding.inflate(inflater, container, false)
        binding.tyre = tyre
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        find_tyre_btn.setOnClickListener {
            val tyreRepo = TyreRepo()
            tyreModel.viewModelScope.launch {
                showProgressBar("Searching...")
                val searchTyreResult = tyreRepo.findTyre(tyre.serialNumber!!)
                endProgressBar()
                if (searchTyreResult is Results.Success<*>) {
                    if (searchTyreResult.data.isNullOrEmpty()) {
                        showToast("No such tyre. Is it registered?")
                        return@launch
                    }

                    val bundle = Bundle().also { it.putString(Const.TYRE, tyre.toJson()) }
                    navController.navigate(
                        R.id.action_findTyreDetailsFragment_to_tyreHomeDetailsFragment,
                        bundle
                    )
                    return@launch
                }
                parseRepoResults(searchTyreResult)
            }
        }
    }
}
