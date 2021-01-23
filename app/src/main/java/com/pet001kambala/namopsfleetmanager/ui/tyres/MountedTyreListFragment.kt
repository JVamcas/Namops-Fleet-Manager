package com.pet001kambala.namopsfleetmanager.ui.tyres

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.databinding.FragmentMountedTyreListBinding
import com.pet001kambala.namopsfleetmanager.model.Tyre
import com.pet001kambala.namopsfleetmanager.ui.AbstractListFragment
import com.pet001kambala.namopsfleetmanager.ui.MainActivity
import com.pet001kambala.namopsfleetmanager.utils.Const
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.toJson
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

class MountedTyreListFragment : AbstractListFragment<Tyre, MountedTyreAdapter.ViewHolder>(
    leftSwipe = false,
    rightSwipe = false
) {

    private lateinit var unitNo: String
    private val tyreModel: TyresViewModel by activityViewModels()
    private lateinit var binding: FragmentMountedTyreListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            unitNo = it.getString(Const.UNIT_NO)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMountedTyreListBinding.inflate(inflater, container, false)
        (requireActivity() as MainActivity).toolbar.title = "Tyres on $unitNo"
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleRecycleView(binding.tyreRecycler, this)
        tyreModel.loadMountedTyres(unitNo)
        tyreModel.mountedTyreList.observe(viewLifecycleOwner, Observer {
            mAdapter.modelList = arrayListOf() //reset the data already held by the adapter
            when (it) {
                is Results.Loading -> {
                    binding.tyreCount = 1
                    showProgressBar("Loading mounted tyres.")
                }

                is Results.Success<*> -> {
                    endProgressBar()
                    binding.tyreCount = it.data?.size ?: 0
                    if (!it.data.isNullOrEmpty()) {
                        val data = it.data as ArrayList<Tyre>
                        mAdapter.modelList = data
                    }
                }
                else -> {
                    endProgressBar()
                    parseRepoResults(it)
                }
            }
        })
    }

    override fun initAdapter() {
        mAdapter = MountedTyreAdapter(this)
    }

    override fun onEditModel(model: Tyre, pos: Int) {
    }

    override fun onDeleteModel(modelPos: Int) {
    }

    override fun onModelClick(model: Tyre) {
        val bundle = Bundle().apply { putString(Const.TYRE, model.toJson()) }
        navController.navigate(
            R.id.action_mountedTyreListFragment_to_tyreHomeDetailsFragment,
            bundle
        )
    }

    override fun onModelIconClick(model: Tyre) {

    }
}