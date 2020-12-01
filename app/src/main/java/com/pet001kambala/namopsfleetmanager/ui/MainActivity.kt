package com.pet001kambala.namopsfleetmanager.ui

import android.graphics.drawable.Drawable
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
import com.google.firebase.FirebaseApp
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.databinding.NavHeaderMainBinding
import com.pet001kambala.namopsfleetmanager.ui.account.AccountViewModel
import com.pet001kambala.namopsfleetmanager.utils.AccessType
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.isAuthorized
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var accountModel: AccountViewModel
    var navigationIcon: Drawable? = null
    val ERR_NO_AUTH: String = "Err: Permission denied!"


    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        navigationIcon = toolbar.navigationIcon

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
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    @ExperimentalCoroutinesApi
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawer_layout.closeDrawer(GravityCompat.START)
        val curDest = navController.currentDestination?.id
        val account = accountModel.currentAccount.value

        when (item.itemId) {
            R.id.nav_vehicles -> {
                if (!account.isAuthorized(AccessType.VIEW_VEHICLES)) {
                    showToast(ERR_NO_AUTH)
                    return true
                }
                if (curDest != R.id.vehiclesListFragment)
                    navController.navigate(R.id.action_global_vehicleGraph)
            }
            R.id.nav_tyre -> {
                if (!account.isAuthorized(AccessType.VIEW_TYRE)) {
                    showToast(ERR_NO_AUTH)
                    return true
                }
                if (curDest != R.id.tyresListFragment)
                    navController.navigate(R.id.action_global_tyresListFragment)
            }
            R.id.nav_my_profile -> {
                if (curDest != R.id.myProfileFragment)
                    navController.navigate(R.id.action_global_accountGraph)
            }
            R.id.nav_trailer -> {
                if (!account.isAuthorized(AccessType.VIEW_TRAILER)) {
                    showToast(ERR_NO_AUTH)
                    return true
                }
                if (curDest != R.id.trailerListFragment)
                    navController.navigate(R.id.action_global_trailerGraph)

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