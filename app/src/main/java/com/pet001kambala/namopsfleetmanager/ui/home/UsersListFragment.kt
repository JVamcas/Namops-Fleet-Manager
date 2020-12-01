package com.pet001kambala.namopsfleetmanager.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.databinding.FragmentUsersListBinding
import com.pet001kambala.namopsfleetmanager.model.Account
import com.pet001kambala.namopsfleetmanager.ui.AbstractListFragment
import com.pet001kambala.namopsfleetmanager.ui.account.AccountViewModel
import com.pet001kambala.namopsfleetmanager.utils.Const
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.toJson
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.android.synthetic.main.fragment_users_list.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

class UsersListFragment : AbstractListFragment<Account, UsersListAdapter.ViewHolder>() {

    private lateinit var binding: FragmentUsersListBinding
    private val accountModel: AccountViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUsersListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initAdapter() {
        mAdapter = UsersListAdapter(this)
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleRecycleView(accounts_recycler, this)

        accountModel.accountList.observe(viewLifecycleOwner, Observer {
            when (it) {
                Results.loading() -> showProgressBar("Loading accounts...")
                is Results.Success<*> -> {
                    endProgressBar()
                    val currentAccount = accountModel.currentAccount.value
                    // cannot issue yourself permission
                    mAdapter.modelList = (it.data?.filterNot { it as Account == currentAccount }) as ArrayList<Account>
                    binding.usersCount = mAdapter.itemCount
                }
                else -> {
                    endProgressBar()
                    parseRepoResults(it)
                }
            }
        })
    }

    override fun onEditModel(model: Account, pos: Int) {


        val bundle = Bundle().apply { putString(Const.ACCOUNT, model.toJson()) }
        navController.navigate(R.id.action_usersListFragment_to_editUserPermissionFragment, bundle)
    }

    override fun onDeleteModel(modelPos: Int) {

    }

    override fun onModelClick(model: Account) {

    }

    override fun onModelIconClick(model: Account) {

    }
}