package com.pet001kambala.namopsfleetmanager.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import com.pet001kambala.namopsfleetmanager.model.Account
import com.pet001kambala.namopsfleetmanager.repository.AccountRepo
import com.pet001kambala.namopsfleetmanager.ui.account.MyProfileFragment
import com.pet001kambala.namopsfleetmanager.ui.account.PermissionListAdapter
import com.pet001kambala.namopsfleetmanager.utils.Const
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.convert
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.android.synthetic.main.fragment_my_profile.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

/**
 * This class allow admin users to issue or remove permission to users
 */
class EditUserPermissionFragment : MyProfileFragment() {

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        save_btn.setOnClickListener {

            accountModel.viewModelScope.launch {
                val accountRepo = AccountRepo()
                showProgressBar("Updating permissions...")
                val updateResults = accountRepo.updateUserPermissions(account)
                endProgressBar()

                if(updateResults is Results.Success<*>)
                    navController.popBackStack()

                parseRepoResults(updateResults)
            }
        }
    }

    override fun onToggle(permission: PermissionListAdapter.PermissionItem) {
        super.onToggle(permission)

        if(permission.state)
            account.permissionList.add(permission.accessType.name)
        else account.permissionList.remove(permission.accessType.name)
    }
}