package com.pet001kambala.namopsfleetmanager.ui.trailer

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import com.pet001kambala.namopsfleetmanager.model.Trailer
import com.pet001kambala.namopsfleetmanager.ui.AbstractFragment
import com.pet001kambala.namopsfleetmanager.utils.Const
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.convert

abstract class AbstractTrailerDetailsFragment : AbstractFragment() {

    lateinit var trailer: Trailer
    val trailerModel: TrailerViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        trailer = Trailer()
        arguments?.let {
            val json = it.getString(Const.VEHICLE)
            json?.let {
                trailer = it.convert()
            }
        }
    }
}