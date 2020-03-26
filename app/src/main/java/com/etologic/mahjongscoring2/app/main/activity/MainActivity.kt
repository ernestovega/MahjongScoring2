package com.etologic.mahjongscoring2.app.main.activity

import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat.START
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.etologic.mahjongscoring2.BuildConfig
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.base.BaseActivity
import com.etologic.mahjongscoring2.app.main.activity.MainActivityViewModel.MainScreens.*
import com.etologic.mahjongscoring2.app.main.activity.MainNavigator.goToScreen
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.main_drawer_oldgames_actionlayout.*
import javax.inject.Inject

class MainActivity : BaseActivity() {
    
    companion object {
        internal const val TAG = "MainActivity"
        internal const val CODE = 1
        internal const val GREEN_BOOK_CODE = 11
        internal const val RATE_CODE = 12
        internal const val CONTACT_CODE = 13
        internal const val GREEN_BOOK_URL = "https://docs.google.com/gview?embedded=true&url=mahjong-europe.org/portal/images/docs/mcr_EN.pdf"
        internal const val MARKET_URI_BASE = "market://details?id="
        internal const val PLAY_STORE_URL_BASE = "http://play.google.com/store/apps/details?id="
        internal const val EMAIL_SUBJECT = "Mahjong Scoring 2"
        internal const val EMAIL_ADDRESS = "mahjongmadrid@gmail.com"
        internal const val LAST_BACKPRESSED_MIN_TIME: Long = 2000
    }
    
    @Inject internal lateinit var viewModelFactory: MainActivityViewModelFactory
    private var viewModel: MainActivityViewModel? = null
    private var lastBackPress: Long = 0
    
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
    }
    
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        setupViewModel()
        setupDrawer()
        viewModel?.navigateTo(OLD_GAMES)
    }
    
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainActivityViewModel::class.java)
        viewModel?.getProgressState()?.observe(this, Observer(this::toggleProgress))
        viewModel?.getCurrentToolbar()?.observe(this, Observer(this::setToolbar))
        viewModel?.getCurrentScreen()?.observe(this, Observer { goToScreen(it, this) })
    }
    
    override fun onBackPressed() {
        if (drawerLayoutMain?.isDrawerOpen(START) == true)
            closeDrawer()
        else if (supportFragmentManager.backStackEntryCount > 1)
            super.onBackPressed()
        else {
            val currentTimeMillis = System.currentTimeMillis()
            if (currentTimeMillis - lastBackPress > LAST_BACKPRESSED_MIN_TIME) {
                Snackbar.make(navigationViewMain, R.string.press_again_to_exit, Snackbar.LENGTH_LONG).show()
                lastBackPress = currentTimeMillis
            } else
                finish()
        }
    }
    
    internal fun closeDrawer() { drawerLayoutMain.closeDrawer(START, true) }
    
    private fun setToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        val actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayoutMain, R.string.open_drawer, R.string.close_drawer)
        drawerLayoutMain?.addDrawerListener(actionBarDrawerToggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        actionBarDrawerToggle.syncState()
    }
    
    private fun setupDrawer() {
        setAppVersion()
        setOldGamesActionNewGameListener()
        setMenuItemSelectedListener()
    }
    
    private fun setAppVersion() {
        val tvAppVersion = navigationViewMain?.getHeaderView(0)?.findViewById<TextView>(R.id.tvDrawerHeaderAppVersion)
        tvAppVersion?.text = BuildConfig.VERSION_NAME
    }
    
    private fun setOldGamesActionNewGameListener() {
//        val oldGamesItem = navigationViewMain?.menu?.findItem(R.id.nav_oldgames)
//        oldGamesItem?.actionView?.findViewById<View>(R.id.
        ibMainDrawerOldGamesActionLayoutNewGame?.setOnClickListener { viewModel?.startNewGame() }//closeDrawer() }
    }
    
    private fun setMenuItemSelectedListener() {
        navigationViewMain?.setNavigationItemSelectedListener { menuItem ->
            this.closeDrawer()
            when (menuItem.itemId) {
                R.id.nav_oldgames -> viewModel?.navigateTo(OLD_GAMES)
                R.id.nav_combinations -> viewModel?.navigateTo(COMBINATIONS)
                R.id.nav_greenbook -> viewModel?.navigateTo(GREEN_BOOK)
                R.id.nav_rate -> viewModel?.navigateTo(RATE)
                R.id.nav_contact -> viewModel?.navigateTo(CONTACT)
                else -> return@setNavigationItemSelectedListener false
            }
            return@setNavigationItemSelectedListener true
        }
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            openDrawer()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    
    private fun openDrawer() {
        if (drawerLayoutMain != null) drawerLayoutMain?.openDrawer(START, true)
    }
}