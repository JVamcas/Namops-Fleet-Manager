package com.pet001kambala.namopsfleetmanager.ui.account.auth

import android.os.Bundle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.model.Account
import com.pet001kambala.namopsfleetmanager.ui.AbstractFragment
import com.pet001kambala.namopsfleetmanager.ui.MainActivity
import com.pet001kambala.namopsfleetmanager.ui.account.AccountViewModel
import com.pet001kambala.namopsfleetmanager.utils.Const
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.convert
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

abstract class AbstractAuthFragment: AbstractFragment() {

    val accountModel: AccountViewModel by activityViewModels()

    lateinit var account: Account

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        account = Account()
        arguments?.apply {
            val json = getString(Const.ACCOUNT)
            json?.let { account = json.convert() }
        }
    }

    override fun onResume() {
        super.onResume()
        val activity = (activity as MainActivity)
        activity.drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    override fun onBackClick() {
        navController.popBackStack(R.id.selectSignUpModeFragment,false)
    }
}