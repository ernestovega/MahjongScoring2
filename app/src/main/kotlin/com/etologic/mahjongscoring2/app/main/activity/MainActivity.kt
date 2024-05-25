/*
 *     Copyright © 2024  Ernesto Vega de la Iglesia Soria
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.etologic.mahjongscoring2.app.main.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat.START
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.etologic.mahjongscoring2.BuildConfig
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.base.BaseActivity
import com.etologic.mahjongscoring2.app.main.activity.MainNavigator.goToScreen
import com.etologic.mahjongscoring2.app.main.activity.MainViewModel.MainScreens.COMBINATIONS
import com.etologic.mahjongscoring2.app.main.activity.MainViewModel.MainScreens.CONTACT_APP_SUPPORT
import com.etologic.mahjongscoring2.app.main.activity.MainViewModel.MainScreens.CONTACT_MAHJONG_MADRID
import com.etologic.mahjongscoring2.app.main.activity.MainViewModel.MainScreens.DIFFS_CALCULATOR
import com.etologic.mahjongscoring2.app.main.activity.MainViewModel.MainScreens.EMA_WEB
import com.etologic.mahjongscoring2.app.main.activity.MainViewModel.MainScreens.GREEN_BOOK_ENGLISH
import com.etologic.mahjongscoring2.app.main.activity.MainViewModel.MainScreens.GREEN_BOOK_SPANISH
import com.etologic.mahjongscoring2.app.main.activity.MainViewModel.MainScreens.MM_WEB
import com.etologic.mahjongscoring2.app.main.activity.MainViewModel.MainScreens.OLD_GAMES
import com.etologic.mahjongscoring2.app.utils.shareFiles
import com.etologic.mahjongscoring2.app.utils.shareText
import com.etologic.mahjongscoring2.databinding.MainActivityBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    companion object {
        const val LAST_BACK_PRESSED_MIN_TIME: Long = 2000
        const val KEY_WAS_GAME_ENDED = "WasGameEnded"
    }

    lateinit var binding: MainActivityBinding

    private val viewModel: MainViewModel by viewModels()

    private var lastBackPress: Long = 0
    private var enableCalcsItem: MenuItem? = null
    private var disableCalcsItem: MenuItem? = null

    val gameActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == RESULT_OK &&
            activityResult.data?.getBooleanExtra(KEY_WAS_GAME_ENDED, false) == true
        ) {
            viewModel.showInAppReviewIfProceed(this)
        }
    }

    override val onBackBehaviour = {
        if (binding.drawerLayoutMain.isDrawerOpen(START))
            closeDrawer()
        else if (supportFragmentManager.backStackEntryCount > 1)
            supportFragmentManager.popBackStack()
        else {
            val currentTimeMillis = System.currentTimeMillis()
            if (currentTimeMillis - lastBackPress > LAST_BACK_PRESSED_MIN_TIME) {
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

    private fun openDrawer() {
        binding.drawerLayoutMain.openDrawer(START, true)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setupDrawer()
        observeViewModel()
        viewModel.navigateTo(OLD_GAMES)
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
                R.id.nav_oldgames -> viewModel.navigateTo(OLD_GAMES)
                R.id.nav_combinations -> viewModel.navigateTo(COMBINATIONS)
                R.id.action_diffs_calculator -> viewModel.navigateTo(DIFFS_CALCULATOR)
                R.id.nav_greenbook_en -> viewModel.navigateTo(GREEN_BOOK_ENGLISH)
                R.id.nav_greenbook_es -> viewModel.navigateTo(GREEN_BOOK_SPANISH)
                R.id.nav_mm_web -> viewModel.navigateTo(MM_WEB)
                R.id.nav_ema_web -> viewModel.navigateTo(EMA_WEB)
                R.id.nav_contact_mahjong_madrid -> viewModel.navigateTo(CONTACT_MAHJONG_MADRID)
                R.id.nav_contact_app_support -> viewModel.navigateTo(CONTACT_APP_SUPPORT)
                else -> return@setNavigationItemSelectedListener false
            }
            return@setNavigationItemSelectedListener true
        }
    }

    private fun closeDrawer() {
        binding.drawerLayoutMain.closeDrawer(START, true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu)

        enableCalcsItem = menu.findItem(R.id.action_enable_diffs_calcs)
        disableCalcsItem = menu.findItem(R.id.action_disable_diffs_calcs)

        toggleDiffsEnabling(viewModel.isDiffsCalcsFeatureEnabledFlow.value)

        menu.findItem(R.id.action_export_db).isVisible = BuildConfig.DEBUG

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> openDrawer()
            R.id.action_export_db -> lifecycleScope.launch { viewModel.exportDb { getExternalFilesDir(null) } }
            R.id.action_share_all_games -> lifecycleScope.launch { viewModel.shareAllGames { getExternalFilesDir(null) } }
            R.id.action_enable_diffs_calcs -> viewModel.toggleDiffsFeature(true)
            R.id.action_disable_diffs_calcs -> viewModel.toggleDiffsFeature(false)
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun observeViewModel() {
        viewModel.getError().observe(this) { showError(it) }
        viewModel.getCurrentToolbar().observe(this) { setToolbar(it) }
        viewModel.getCurrentScreen().observe(this) { goToScreen(it, this, viewModel) }
        viewModel.getExportedFiles().observe(this) { shareFiles(it) }
        viewModel.getExportedText().observe(this) { shareText(it) }
        viewModel.getExportedFiles().observe(this) { shareFiles(it) }
        lifecycleScope.launch { repeatOnLifecycle(Lifecycle.State.STARTED) { viewModel.isDiffsCalcsFeatureEnabledFlow.collect(::toggleDiffsEnabling) } }
    }

    private fun setToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        val actionBarDrawerToggle = ActionBarDrawerToggle(this, binding.drawerLayoutMain, R.string.open_drawer, R.string.close_drawer)
        binding.drawerLayoutMain.addDrawerListener(actionBarDrawerToggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        actionBarDrawerToggle.syncState()
    }

    private fun toggleDiffsEnabling(shouldShowDiffs: Boolean) {
        if (shouldShowDiffs) {
            enableCalcsItem?.isVisible = false
            disableCalcsItem?.isVisible = true
        } else {
            enableCalcsItem?.isVisible = true
            disableCalcsItem?.isVisible = false
        }
    }
}