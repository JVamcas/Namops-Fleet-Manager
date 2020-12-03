package com.pet001kambala.namopsfleetmanager.ui.trailer

import android.os.Bundle
import android.view.*
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.databinding.FragmentTrailerListBinding
import com.pet001kambala.namopsfleetmanager.model.Cell
import com.pet001kambala.namopsfleetmanager.model.Trailer
import com.pet001kambala.namopsfleetmanager.ui.AbstractTableFragment
import com.pet001kambala.namopsfleetmanager.utils.AccessType
import com.pet001kambala.namopsfleetmanager.utils.DateUtil
import com.pet001kambala.namopsfleetmanager.utils.DateUtil.Companion._24
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.android.synthetic.main.fragment_trailer_list.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

class TrailerListFragment : AbstractTableFragment() {

    private lateinit var binding: FragmentTrailerListBinding
    private val trailerModel: TrailerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        binding = FragmentTrailerListBinding.inflate(inflater, container, false)

        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        register_trailer.isEnabled = isAuthorized(AccessType.REG_TRAILER)

        register_trailer.setOnClickListener {
            navController.navigate(R.id.action_trailerListFragment_to_trailerRegistrationDetailsFragment)
        }

        if(isAuthorized(AccessType.VIEW_TRAILER)) {
            trailerModel.trailersList.observe(viewLifecycleOwner, Observer {
                it?.let { results ->
                    when (results) {
                        Results.Loading -> {
                            binding.trailerCount = 1
                            showProgressBar("Loading trailers...")
                        }
                        is Results.Success<*> -> {
                            endProgressBar()
                            binding.trailerCount = results.data?.size ?: 0

                            if (!results.data.isNullOrEmpty()) {
                                val headers =
                                    results.data[0].data().map { it.first }//col headers text
                                val colHeader = headers.map { Cell(it) } as ArrayList
                                val rows =
                                    results.data.map {
                                        it.data().map { Cell(it.second) } as ArrayList
                                    }
                                val rowHeader =
                                    results.data.withIndex()
                                        .map { Cell((it.index + 1).toString()) } as ArrayList
                                initTable(
                                    colHeader, rows, rowHeader, trailer_table,
                                    R.id.action_trailerListFragment_to_trailerHomeDetailsFragment
                                )
                            }
                        }
                        else -> {
                            endProgressBar()
                            parseRepoResults(results)
                        }
                    }
                }
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.trailer_list_menu, menu)
        menu.findItem(R.id.export_all_history).isEnabled = isAuthorized(AccessType.EXPORT_TRAILER)
    }

    @ExperimentalCoroutinesApi
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.export_all_history -> {
                if (isStoragePermissionGranted()) {//permission must have been granted
                    trailerModel.trailersList.value?.apply {
                        val today = DateUtil.today()._24()
                        (this as? Results.Success<*>)?.data?.let {
                            toStorage("Namops Trailer Record_$today.xlsx", arrayListOf(today),
                                arrayListOf(it.map { it as Trailer } as ArrayList<Trailer>)
                            )
                        }
                    }
                    return true
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}