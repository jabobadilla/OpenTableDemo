package com.bobadilla.opentabledemo

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bobadilla.opentabledemo.objects.CommonFunctions
import com.bobadilla.opentabledemo.views.MainFragment
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var mainContent: FrameLayout? = null
    private var mainFragment: MainFragment? = null
    private var drawerLayout: DrawerLayout? = null
    private var navigationView: NavigationView? = null
    private var toolbar: Toolbar? = null
    private var hasSavedInstanceState: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Singleton.setCurrenActivity(this)

        if (savedInstanceState != null) { hasSavedInstanceState = true } else hasSavedInstanceState = false

        initView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    private fun initView() {
        mainContent = findViewById(R.id.main_content)
        drawerLayout = findViewById(R.id.drawer_layout)

        navigationView = findViewById(R.id.navigationView)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Configure action bar
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.title = "Open Table Demo"

        // Initialize the action bar drawer toggle instance
        val drawerToggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
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

        // Configure the drawer layout to add listener and show icon on toolbar
        drawerToggle.isDrawerIndicatorEnabled = true
        drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        // Set navigation view navigation item selected listener
        navigationView?.setNavigationItemSelectedListener{
            when (it.itemId){
                R.id.nav_home -> {
                    if (!supportFragmentManager.fragments[0].tag.equals("class com.bobadilla.opentabledemo.views.MainFragment")){
                        supportFragmentManager.popBackStackImmediate()
                        if (supportFragmentManager.fragments[0].tag.equals("class com.bobadilla.opentabledemo.views.RestaurantsFragment")) {
                            supportFragmentManager.popBackStackImmediate()
                        }
                    }
                }
            }
            // Close the drawer
            drawer_layout.closeDrawer(GravityCompat.START)
            true
        }

        mainFragment = MainFragment()
        Singleton.setFragmentManager(supportFragmentManager)
        initFragment()
    }

    private fun initFragment() {
        if (Singleton.getCurrentFragment() !== mainFragment) {
            if ( !hasSavedInstanceState ) {
                CommonFunctions.goToNextFragment(mainContent!!.id,"selectedItem")
            }
        }
    }

}
