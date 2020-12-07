package com.pet001kambala.namopsfleetmanager.ui.tyres.records

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.databinding.TyresListFragmentBinding
import com.pet001kambala.namopsfleetmanager.model.AbstractModel
import com.pet001kambala.namopsfleetmanager.model.Tyre
import com.pet001kambala.namopsfleetmanager.ui.AbstractTableFragment
import com.pet001kambala.namopsfleetmanager.ui.tyres.TyresViewModel
import com.pet001kambala.namopsfleetmanager.utils.Const
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.convert

abstract class AbstractTyreRecord<K: AbstractModel>: AbstractTableFragment<K>() {

    lateinit var binding: TyresListFragmentBinding
    lateinit var tyre: Tyre
    val tyreModel: TyresViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            val json = getString(Const.TYRE)
            json?.let {
                tyre = it.convert()
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = TyresListFragmentBinding.inflate(inflater, container, false)
        binding.registerTyre.visibility = GONE
        return binding.root
    }
}