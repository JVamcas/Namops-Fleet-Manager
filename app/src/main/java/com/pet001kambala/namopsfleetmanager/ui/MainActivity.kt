package com.pet001kambala.namopsfleetmanager.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.databinding.NavHeaderMainBinding
import com.pet001kambala.namopsfleetmanager.model.Account
import com.pet001kambala.namopsfleetmanager.ui.account.AccountViewModel
import com.pet001kambala.namopsfleetmanager.utils.AccessType
import com.pet001kambala.namopsfleetmanager.utils.Const
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.isAuthorized
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.isIncompleteAccount
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var accountModel: AccountViewModel
    private var account: Account? = null

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        //notification bundle
        savedInstanceState?.let {
            println(savedInstanceState)
        }
        //TODO when

        accountModel = ViewModelProvider(this).get(AccountViewModel::class.java)

        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment, R.id.selectSignUpModeFragment),
            drawer_layout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        nav_view.setupWithNavController(navController)
        nav_view.setNavigationItemSelectedListener(this)

        val navBinding: NavHeaderMainBinding = NavHeaderMainBinding.bind(nav_view.getHeaderView(0))
        accountModel.currentAccount.observe(this, Observer {
            navBinding.user = it
            account = it
            it?.let {
                refreshMainActivity()
            }
        })
        refreshMainActivity()
    }

    @ExperimentalCoroutinesApi
    private fun refreshMainActivity() {
        //enable/disable menu items if access given or not
        val navMenu = nav_view.menu

        navMenu.findItem(R.id.nav_my_profile).isEnabled = !account.isIncompleteAccount()

        navMenu.findItem(R.id.nav_vehicles).isEnabled =
            account.isAuthorized(AccessType.VIEW_VEHICLES)
        navMenu.findItem(R.id.nav_tyre).isEnabled = account.isAuthorized(AccessType.VIEW_TYRE)
        navMenu.findItem(R.id.nav_trailer).isEnabled = account.isAuthorized(AccessType.VIEW_TRAILER)
        navMenu.findItem(R.id.nav_fuel).isEnabled = false

        if (account.isAuthorized(AccessType.ADMIN))
            FirebaseMessaging.getInstance().subscribeToTopic(Const.TYRE_WORN_OUT)

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    @ExperimentalCoroutinesApi
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawer_layout.closeDrawer(GravityCompat.START)
        val curDest = navController.currentDestination?.id

        when (item.itemId) {
            R.id.nav_vehicles -> {
                if (curDest != R.id.vehiclesListFragment)
                    navController.navigate(R.id.action_global_vehicleGraph)
            }
            R.id.nav_tyre -> {
                if (curDest != R.id.tyresListFragment)
                    navController.navigate(R.id.action_global_tyresListFragment)
            }
            R.id.nav_my_profile -> {
                if (curDest != R.id.myProfileFragment)
                    navController.navigate(R.id.action_global_accountGraph)
            }
            R.id.nav_trailer -> {

                if (curDest != R.id.trailerListFragment)
                    navController.navigate(R.id.action_global_trailerGraph)

            }
            R.id.about_developer -> {
                if (curDest != R.id.aboutDeveloperFragment)
                    navController.navigate(R.id.action_global_aboutDeveloperFragment)
            }
        }
        return false
    }

    @ExperimentalCoroutinesApi
    fun onSignOut(view: View) {
        drawer_layout.closeDrawer(GravityCompat.START)
        accountModel.signOut()
        accountModel.authState.observe(this, Observer { authState ->
            if (authState == AccountViewModel.AuthState.UNAUTHENTICATED) {
                accountModel.authState.removeObservers(this)
                Toast.makeText(this@MainActivity, "Logout Success!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showToast(msg: String?) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}