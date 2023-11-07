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

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat.START
import androidx.lifecycle.ViewModelProvider
import com.etologic.mahjongscoring2.BuildConfig
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.base.BaseActivity
import com.etologic.mahjongscoring2.app.main.activity.MainNavigator.goToScreen
import com.etologic.mahjongscoring2.app.main.activity.MainViewModel.MainScreens.COMBINATIONS
import com.etologic.mahjongscoring2.app.main.activity.MainViewModel.MainScreens.CONTACT
import com.etologic.mahjongscoring2.app.main.activity.MainViewModel.MainScreens.DIFFS_CALCULATOR
import com.etologic.mahjongscoring2.app.main.activity.MainViewModel.MainScreens.EMA_WEB
import com.etologic.mahjongscoring2.app.main.activity.MainViewModel.MainScreens.GREEN_BOOK_ENGLISH
import com.etologic.mahjongscoring2.app.main.activity.MainViewModel.MainScreens.GREEN_BOOK_SPANISH
import com.etologic.mahjongscoring2.app.main.activity.MainViewModel.MainScreens.MM_WEB
import com.etologic.mahjongscoring2.app.main.activity.MainViewModel.MainScreens.OLD_GAMES
import com.etologic.mahjongscoring2.databinding.MainActivityBinding
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class MainActivity : BaseActivity() {

    companion object {
        internal const val LAST_BACKPRESSED_MIN_TIME: Long = 2000
        internal const val KEY_WAS_GAME_ENDED = "WasGameEnded"
    }

    lateinit var binding: MainActivityBinding

    @Inject
    internal lateinit var viewModelFactory: MainActivityViewModelFactory
    private var viewModel: MainViewModel? = null
    private var lastBackPress: Long = 0

    val gameActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == RESULT_OK &&
            activityResult.data?.getBooleanExtra(KEY_WAS_GAME_ENDED, false) == true
        ) {
            viewModel?.showInAppReviewIfProceed(this)
        }
    }

    override val onBackBehaviour = {
        if (binding.drawerLayoutMain.isDrawerOpen(START))
            closeDrawer()
        else if (supportFragmentManager.backStackEntryCount > 1)
            supportFragmentManager.popBackStack()
        else {
            val currentTimeMillis = System.currentTimeMillis()
            if (currentTimeMillis - lastBackPress > LAST_BACKPRESSED_MIN_TIME) {
                Snackbar.make(binding.navigationViewMain, R.string.press_again_to_exit, Snackbar.LENGTH_LONG).show()
                lastBackPress = currentTimeMillis
            } else
                finish()
        }
    }

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
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        viewModel?.getCurrentToolbar()?.observe(this) { setToolbar(it) }
        viewModel?.getCurrentScreen()?.observe(this) { goToScreen(it, this) }
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
                R.id.action_diffs_calculator -> viewModel?.navigateTo(DIFFS_CALCULATOR)
                R.id.nav_greenbook_en -> viewModel?.navigateTo(GREEN_BOOK_ENGLISH)
                R.id.nav_greenbook_es -> viewModel?.navigateTo(GREEN_BOOK_SPANISH)
                R.id.nav_mm_web -> viewModel?.navigateTo(MM_WEB)
                R.id.nav_ema_web -> viewModel?.navigateTo(EMA_WEB)
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