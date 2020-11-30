package com.pet001kambala.namopsfleetmanager.ui.account

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import androidx.databinding.DataBindingUtil
import com.google.android.material.switchmaterial.SwitchMaterial
import com.pet001kambala.namopsfleetmanager.databinding.PermissionGroupViewBinding
import com.pet001kambala.namopsfleetmanager.databinding.PermissionListItemBinding
import com.pet001kambala.namopsfleetmanager.utils.AccessType
import kotlin.collections.LinkedHashMap


class PermissionListAdapter(
    private val permissionMap: LinkedHashMap<String, List<PermissionItem>>
) : BaseExpandableListAdapter() {
    override fun getGroupCount(): Int {
        return permissionMap.keys.size
    }

    override fun getChildrenCount(i: Int): Int {
        return permissionMap.toList()[i].second.size
    }

    override fun getGroup(i: Int): String {
        return permissionMap.toList()[i].first
    }

    override fun getChild(i: Int, i1: Int): PermissionItem {
        return permissionMap.toList()[i].second[i1]
    }

    override fun getGroupId(i: Int): Long {
        return i.toLong()
    }

    override fun getChildId(i: Int, i1: Int): Long {
        return i1.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPos: Int,
        isExpanded: Boolean,
        view: View?,
        viewGroup: ViewGroup
    ): View {

        val binding =
            if (view == null)
                PermissionGroupViewBinding.inflate(
                    LayoutInflater.from(viewGroup.context),
                    viewGroup,
                    false
                )
            else DataBindingUtil.bind(view)!!
        binding.groupName = getGroup(groupPos)
        return binding.root
    }

    override fun getChildView(
        groupPos: Int,
        permissionPos: Int,
        b: Boolean,
        view: View?,
        viewGroup: ViewGroup
    ): View {
        val listItemBinding =
            if (view == null) PermissionListItemBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false
            ) else DataBindingUtil.getBinding(view)!!

        val permission = getChild(groupPos,permissionPos)
        val mSwitch: SwitchMaterial = listItemBinding.permission
        mSwitch.isChecked = permission.state

        listItemBinding.setPermission(permission.accessType.toString())
        listItemBinding.permission.setOnClickListener{
            permission.state = mSwitch.isChecked
        }
        return listItemBinding.root
    }

    override fun isChildSelectable(i: Int, i1: Int): Boolean {
        return true
    }

    class PermissionItem(var accessType: AccessType) {
        var state = false
    }
}