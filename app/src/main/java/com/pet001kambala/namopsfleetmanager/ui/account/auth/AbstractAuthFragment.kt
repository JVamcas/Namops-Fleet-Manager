package com.pet001kambala.namopsfleetmanager.ui.account.auth

import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import com.pet001kambala.namopsfleetmanager.ui.AbstractFragment
import com.pet001kambala.namopsfleetmanager.ui.MainActivity
import com.pet001kambala.namopsfleetmanager.ui.account.AccountViewModel
import kotlinx.android.synthetic.main.activity_main.*

abstract class AbstractAuthFragment: AbstractFragment() {

    val accountModel: AccountViewModel by activityViewModels()

    override fun onResume() {
        super.onResume()
        val activity = (activity as MainActivity)
        activity.drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }
}