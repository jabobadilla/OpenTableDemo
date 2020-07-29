package com.bobadilla.opentabledemo

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.bobadilla.opentabledemo.common.Singleton
import com.bobadilla.opentabledemo.connection.ConnectionStatus
import com.bobadilla.opentabledemo.connection.api.APIRetriever
import com.bobadilla.opentabledemo.data.RestaurantRepository
import com.bobadilla.opentabledemo.data.dataLoad.DataNetLoad
import com.bobadilla.opentabledemo.data.database.DatabaseExists
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private var drawerLayout : DrawerLayout? = null
    private var navigationView : NavigationView? = null
    private var toolbar : Toolbar? = null

    private lateinit var navigationController : NavController

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()

        if (ConnectionStatus.isNetworkConnected(applicationContext)
            && DatabaseExists.databaseFileExists(this)
            && savedInstanceState == null) {
            CoroutineScope(Dispatchers.IO).launch {
                //val data = DataNetLoad.loadCities(false).await()
                val data = APIRetriever().getAPICities()
                RestaurantRepository(application).insertCity(data)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navigationController, drawerLayout)
    }

    override fun onBackPressed() {
        if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            drawerLayout?.closeDrawer(GravityCompat.START)
        }
        else {
            super.onBackPressed()
        }
    }

    private fun initView() {
        // Configure action bar
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

        navigationController = findNavController(R.id.navigationHostFragment)
        NavigationUI.setupActionBarWithNavController(this, navigationController, drawerLayout)
        NavigationUI.setupWithNavController(navigationView!!, navigationController)

        // Initialize the action bar drawer toggle instance
        val drawerToggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,
            drawerLayout,
            /*toolbar,*/ //If we set this option, then the fragments in the stack will open the navigation view menu instead of performing the back functionality
            R.string.drawer_open,
            R.string.drawer_close
        ){
            override fun onDrawerClosed(view:View){
                super.onDrawerClosed(view)
            }

            override fun onDrawerOpened(drawerView: View){
                super.onDrawerOpened(drawerView)
            }
        }

        // Configure the drawer layout toggle to display hamburger icon with nice animation and
        //Tie DrawerLayout events to the ActionBarToggle
        if (navigationController.currentDestination?.id?.equals(R.id.mainFragment)!!) {
            drawerToggle.isDrawerIndicatorEnabled = true
        }
        else {
            /*
            isDrawerIndicatorEnabled must be called on your ActionBarDrawerToggle before
            setDisplayHomeAsUpEnabled is called on your ActionBar. Both lines must be called for
            the default (tinted) arrow to show.
             */
            drawerToggle.isDrawerIndicatorEnabled = false
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        drawerLayout?.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        // Set navigation view navigation item selected listener
        navigationView?.setNavigationItemSelectedListener{
            when (it.itemId) {
                R.id.nav_home -> {
                    navigationController.popBackStack(R.id.mainFragment, false)
                }
            }
            // Close the drawer
            drawerLayout?.closeDrawer(GravityCompat.START)
            true
        }

        Singleton.setFragmentManager(supportFragmentManager)

    }

}
