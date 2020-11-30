package com.pet001kambala.namopsfleetmanager.ui.account

import android.os.Bundle
import android.view.*
import android.view.View.GONE
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.databinding.FragmentMyProfileBinding
import com.pet001kambala.namopsfleetmanager.ui.AbstractFragment
import com.pet001kambala.namopsfleetmanager.ui.account.PermissionListAdapter.PermissionItem
import com.pet001kambala.namopsfleetmanager.utils.AccessType.*
import kotlinx.android.synthetic.main.fragment_my_profile.*
import kotlinx.android.synthetic.main.text_separator_line.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

class MyProfileFragment : AbstractFragment() {
    val accountModel: AccountViewModel by activityViewModels()
    private lateinit var binding: FragmentMyProfileBinding
    lateinit var permissionMap: LinkedHashMap<String, List<PermissionItem>>
    private lateinit var mAdapter: PermissionListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionMap = LinkedHashMap()
        val permissionGroup = listOf(
            "Vehicles", "Trailers", "Tyre Vendors", "Tyres", "Administration"
        )
        permissionMap.apply {
            put(
                permissionGroup[0], listOf(
                    PermissionItem(VIEW_VEHICLES),
                    PermissionItem(REG_VEHICLE),
                    PermissionItem(UPDATE_VEHICLE)
                )
            )
            put(
                permissionGroup[1],
                listOf(
                    PermissionItem(VIEW_TRAILER),
                    PermissionItem(REG_TRAILER),
                    PermissionItem(UPDATE_TRAILER)
                )
            )
            put(
                permissionGroup[2],
                listOf(
                    PermissionItem(VIEW_TYRE_VENDOR),
                    PermissionItem(REG_TYRE_VENDOR),
                    PermissionItem(UPDATE_TYRE_VENDOR)
                )
            )
            put(
                permissionGroup[3],
                listOf(
                    PermissionItem(VIEW_TYRE),
                    PermissionItem(REG_TYRE),
                    PermissionItem(UPDATE_TYRE),
                    PermissionItem(INSPECT_TYRE),
                    PermissionItem(MOUNT_TYRE_OR_UNMOUNT_TYRE),
                    PermissionItem(SEND_OR_RECEIVE_TYRE_FROM_VENDOR)
                )
            )
            put(
                permissionGroup[4],
                listOf(PermissionItem(ADMIN))
            )
        }
        mAdapter = PermissionListAdapter(permissionMap)
    }

    @ExperimentalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        binding = FragmentMyProfileBinding.inflate(inflater, container, false)

        accountModel.currentAccount.observe(viewLifecycleOwner, Observer { account ->
            binding.account = account
            account?.let {
                permissionMap.toList().forEach {
                    it.second.forEach { permission ->
                        permission.state = permission.accessType.name in account.permissionList
                    }
                }
            }
        })
        binding.permissionList.setAdapter(mAdapter)


        binding.permissionList.setAdapter(mAdapter)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        save_btn.visibility  = GONE
        bottom_separator_view.visibility = GONE
        block_user_switch.visibility = GONE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.profile_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.update_account -> {
                navController.navigate(R.id.action_myProfileFragment_to_updateProfileFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}