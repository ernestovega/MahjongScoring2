/*
    Copyright (C) 2020  Ernesto Vega de la Iglesia Soria
 
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.etologic.mahjongscoring2.app.main.activity

import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat.START
import androidx.lifecycle.ViewModelProvider
import com.etologic.mahjongscoring2.BuildConfig
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.base.BaseActivity
import com.etologic.mahjongscoring2.app.main.activity.MainNavigator.goToScreen
import com.etologic.mahjongscoring2.app.main.activity.MainViewModel.MainScreens.*
import com.etologic.mahjongscoring2.databinding.MainActivityBinding
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class MainActivity : BaseActivity() {
    
    companion object {
        internal const val TAG = "MainActivity"
        
        @Suppress("unused")
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
    @Inject
    internal lateinit var viewModelFactory: MainActivityViewModelFactory
    private var viewModel: MainViewModel? = null
    private var lastBackPress: Long = 0

    lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
    
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setupViewModel()
        setupDrawer()
        viewModel?.navigateTo(OLD_GAMES)
    }
    
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        viewModel?.getCurrentToolbar()?.observe(this) { setToolbar(it) }
        viewModel?.getCurrentScreen()?.observe(this) { goToScreen(it, this) }
    }

    override fun onBackPressed() {
        if (binding.drawerLayoutMain.isDrawerOpen(START))
            closeDrawer()
        else if (supportFragmentManager.backStackEntryCount > 1)
            super.onBackPressed()
        else {
            val currentTimeMillis = System.currentTimeMillis()
            if (currentTimeMillis - lastBackPress > LAST_BACKPRESSED_MIN_TIME) {
                Snackbar.make(binding.navigationViewMain, R.string.press_again_to_exit, Snackbar.LENGTH_LONG).show()
                lastBackPress = currentTimeMillis
            } else
                finish()
        }
    }
    
    private fun closeDrawer() {
        binding.drawerLayoutMain.closeDrawer(START, true)
    }
    
    private fun setToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        val actionBarDrawerToggle = ActionBarDrawerToggle(this, binding.drawerLayoutMain, R.string.open_drawer, R.string.close_drawer)
        binding.drawerLayoutMain.addDrawerListener(actionBarDrawerToggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        actionBarDrawerToggle.syncState()
    }
    
    private fun setupDrawer() {
        setAppVersion()
        setMenuItemSelectedListener()
    }
    
    private fun setAppVersion() {
        val tvAppVersion = binding.navigationViewMain.getHeaderView(0)?.findViewById<TextView>(R.id.tvDrawerHeaderAppVersion)
        tvAppVersion?.text = BuildConfig.VERSION_NAME
    }
    
    private fun setMenuItemSelectedListener() {
        binding.navigationViewMain.setNavigationItemSelectedListener { menuItem ->
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
        binding.drawerLayoutMain.openDrawer(START, true)
    }
}