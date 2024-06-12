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
package com.etologic.mahjongscoring2.app.screens

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat.START
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.etologic.mahjongscoring2.BuildConfig
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.screens.MainNavigator.goToScreen
import com.etologic.mahjongscoring2.app.screens.MainViewModel.MainScreens.COMBINATIONS
import com.etologic.mahjongscoring2.app.screens.MainViewModel.MainScreens.DIFFS_CALCULATOR
import com.etologic.mahjongscoring2.app.screens.MainViewModel.MainScreens.GAME
import com.etologic.mahjongscoring2.app.screens.MainViewModel.MainScreens.OLD_GAMES
import com.etologic.mahjongscoring2.app.utils.LanguageHelper
import com.etologic.mahjongscoring2.app.utils.setLocale
import com.etologic.mahjongscoring2.app.utils.shareFiles
import com.etologic.mahjongscoring2.app.utils.shareText
import com.etologic.mahjongscoring2.databinding.MainActivityBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        const val LAST_BACK_PRESSED_MIN_TIME: Long = 2000
    }

    lateinit var binding: MainActivityBinding

    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var languageHelper: LanguageHelper
    private var currentLanguage: String? = null

    private var lastBackPress: Long = 0

    val pickFileResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == RESULT_OK) {
            activityResult.data?.data?.let { uri ->
                viewModel.importGames(uri) { applicationContext.contentResolver }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    fun openDrawer() {
        binding.drawerLayoutMain.openDrawer(START, true)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setOnBackBehaviour()
        setupDrawer()
        startObservingViewModel()
    }

    private fun setOnBackBehaviour() {
        onBackPressedDispatcher.addCallback(this) {
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
    }

    private fun setupDrawer() {
        setAppVersion()
        setMenuItemSelectedListener()
    }

    private fun setAppVersion() {
        val tvAppVersion = binding.navigationViewMain.getHeaderView(0)?.findViewById<TextView>(R.id.tvDrawerHeaderAppVersion)
        tvAppVersion?.text = BuildConfig.VERSION_NAME
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    private fun setMenuItemSelectedListener() {
        binding.navigationViewMain.setNavigationItemSelectedListener { menuItem ->
            this.closeDrawer()
            when (menuItem.itemId) {
                R.id.nav_current_game -> viewModel.navigateTo(GAME)
                R.id.nav_old_games -> viewModel.navigateTo(OLD_GAMES)
                R.id.nav_combinations -> viewModel.navigateTo(COMBINATIONS)
                R.id.nav_diffs_calculator -> viewModel.navigateTo(DIFFS_CALCULATOR)
                R.id.nav_greenbook_en -> goToGreenBookEnglish()
                R.id.nav_greenbook_es -> goToGreenBookSpanish()
                R.id.nav_mm_web -> goToWebsiteMM()
                R.id.nav_ema_web -> goToWebsiteEMA()
                R.id.nav_contact_mahjong_madrid -> goToContactMM()
                R.id.nav_contact_app_support -> goToContactSupport()
                else -> return@setNavigationItemSelectedListener false
            }
            return@setNavigationItemSelectedListener true
        }
    }

    private fun closeDrawer() {
        binding.drawerLayoutMain.closeDrawer(START, true)
    }

    private fun startObservingViewModel() {
        with(lifecycleScope) {
            launch { repeatOnLifecycle(STARTED) { viewModel.errorFlow.collect(::showError) } }
            launch { repeatOnLifecycle(STARTED) { viewModel.currentScreenFlow.collect { currentScreenObserver(it) } } }
            launch { repeatOnLifecycle(STARTED) { viewModel.currentToolbarFlow.collect(::setToolbar) } }
            launch { repeatOnLifecycle(STARTED) { viewModel.exportedTextFlow.collect(::shareText) } }
            launch { repeatOnLifecycle(STARTED) { viewModel.exportedFilesFlow.collect(::shareFiles) } }
            launch { repeatOnLifecycle(STARTED) { viewModel.currentGameNameFlow.collect(::currentGameNameObserver) } }
        }
    }

    private fun showError(throwable: Throwable?) {
        if (throwable == null) getString(R.string.ups_something_wrong)
        else throwable.message?.let { showMessage(it) }
    }

    private fun showMessage(message: String) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogStyleMM)
        builder.setMessage(message).show()
    }

    private fun currentScreenObserver(screen: MainViewModel.MainScreens) {
        goToScreen(screen)
        if (screen == GAME) {
            binding.navigationViewMain.setCheckedItem(R.id.nav_current_game)
        }
    }

    private fun setToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        val actionBarDrawerToggle = ActionBarDrawerToggle(this, binding.drawerLayoutMain, R.string.open_drawer, R.string.close_drawer)
        binding.drawerLayoutMain.addDrawerListener(actionBarDrawerToggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        actionBarDrawerToggle.syncState()
    }

    private fun currentGameNameObserver(gameName: String?) {
        with(binding.navigationViewMain.menu.findItem(R.id.nav_current_game)) {
            title = gameName ?: getString(R.string.current_game)
            setVisible(gameName.isNullOrBlank().not())
        }
    }

    override fun onStart() {
        super.onStart()
        currentLanguage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales.get(0).language
        } else {
            @Suppress("DEPRECATION")
            resources.configuration.locale.language
        }
        val savedLanguage = languageHelper.currentLanguage

        if (currentLanguage != savedLanguage) {
            setLocale(savedLanguage)
            currentLanguage = savedLanguage
            recreate()
        }
    }
}