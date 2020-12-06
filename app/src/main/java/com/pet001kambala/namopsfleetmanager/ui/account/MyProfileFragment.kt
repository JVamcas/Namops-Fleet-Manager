package com.pet001kambala.namopsfleetmanager.ui.account

import android.os.Bundle
import android.view.*
import android.view.View.GONE
import androidx.fragment.app.activityViewModels
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.databinding.FragmentMyProfileBinding
import com.pet001kambala.namopsfleetmanager.model.Account
import com.pet001kambala.namopsfleetmanager.ui.AbstractFragment
import com.pet001kambala.namopsfleetmanager.ui.account.PermissionListAdapter.PermissionItem
import com.pet001kambala.namopsfleetmanager.utils.AccessType.*
import com.pet001kambala.namopsfleetmanager.utils.Const
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.convert
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.isIncompleteAccount
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.toJson
import kotlinx.android.synthetic.main.fragment_my_profile.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

open class MyProfileFragment : AbstractFragment(), PermissionListAdapter.OnPermissionToggleListener {
    val accountModel: AccountViewModel by activityViewModels()
    lateinit var binding: FragmentMyProfileBinding
    lateinit var permissionMap: LinkedHashMap<String, List<PermissionItem>>
    private lateinit var mAdapter: PermissionListAdapter


    lateinit var account: Account
    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        accountModel.currentAccount.value?.let { account = it }
        arguments?.let {
            val json = it.getString(Const.ACCOUNT)
            json?.let {
                account = json.convert()
            }
        }
    }


    @ExperimentalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        initPermission()
        binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        binding.account = account
        permissionMap.setUserPermissionItem(account)
        binding.permissionList.setAdapter(mAdapter)

        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        save_btn.visibility  = GONE
        bottom_separator_view.visibility = GONE
        block_user_switch.visibility = GONE

        if(account.isIncompleteAccount())
            navController.navigate(R.id.action_myProfileFragment_to_updateProfileFragment)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.profile_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.update_account -> {
                val bundle = Bundle().also { it.putString(Const.ACCOUNT,it.toJson()) }
                navController.navigate(R.id.action_myProfileFragment_to_updateProfileFragment,bundle)
            }
        }
        return super.onOptionsItemSelected(item)
    }
    /**
     * Convert user permissions <Strings> to PermissionItem
     */
    private fun LinkedHashMap<String, List<PermissionItem>>.setUserPermissionItem(account: Account){
        this.toList().forEach {
            it.second.forEach { permission ->
                permission.state = permission.accessType.name in account.permissionList
            }
        }
    }

     fun initPermission(){
         permissionMap = LinkedHashMap()
         val permissionGroup = listOf(
             "Vehicles", "Trailers", "Tyre Vendors", "Tyres", "Administration"
         )
         permissionMap.apply {
             put(
                 permissionGroup[0], listOf(
                     PermissionItem(VIEW_VEHICLES),
                     PermissionItem(REG_VEHICLE),
                     PermissionItem(UPDATE_VEHICLE),
                     PermissionItem(EXPORT_VEHICLE)
                 )
             )
             put(
                 permissionGroup[1],
                 listOf(
                     PermissionItem(VIEW_TRAILER),
                     PermissionItem(REG_TRAILER),
                     PermissionItem(UPDATE_TRAILER),
                     PermissionItem(MOUNT_UNMOUNT_TRAILER),
                     PermissionItem(EXPORT_TRAILER)
                 )
             )
             put(
                 permissionGroup[2],
                 listOf(
                     PermissionItem(VIEW_TYRE_VENDOR),
                     PermissionItem(REG_TYRE_VENDOR),
                     PermissionItem(UPDATE_TYRE_VENDOR),
                     PermissionItem(EXPORT_VENDOR)
                 )
             )
             put(
                 permissionGroup[3],
                 listOf(
                     PermissionItem(VIEW_TYRE),
                     PermissionItem(REG_TYRE),
                     PermissionItem(UPDATE_TYRE),
                     PermissionItem(INSPECT_TYRE),
                     PermissionItem(VIEW_TYRE_RECORDS),
                     PermissionItem(MOUNT_OR_UNMOUNT_TYRE),
                     PermissionItem(SEND_OR_RECEIVE_TYRE_FROM_VENDOR),
                     PermissionItem(EXPORT_TYRE)
                 )
             )
             put(
                 permissionGroup[4],
                 listOf(PermissionItem(ADMIN))
             )
         }
         mAdapter = PermissionListAdapter(this,permissionMap)
    }

    override fun onToggle(permission: PermissionItem) {
    }

    override fun onBackClick() {
        navController.popBackStack()
    }
}