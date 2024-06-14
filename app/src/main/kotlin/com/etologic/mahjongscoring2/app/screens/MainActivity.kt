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

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.etologic.mahjongscoring2.BuildConfig
import com.etologic.mahjongscoring2.R
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

    private lateinit var appBarConfiguration: AppBarConfiguration

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

    private val navController by lazy { (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment).navController }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        setOnBackBehaviour()
    }

    private fun setupToolbar() {
        appBarConfiguration = AppBarConfiguration(navController.graph, binding.drawerLayout)
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navigationView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun setOnBackBehaviour() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (navController.currentDestination?.id == R.id.oldGamesFragment) {
                    val currentTimeMillis = System.currentTimeMillis()
                    if (currentTimeMillis - lastBackPress > LAST_BACK_PRESSED_MIN_TIME) {
                        Snackbar.make(binding.navigationView, R.string.press_again_to_exit, Snackbar.LENGTH_LONG).show()
                        lastBackPress = currentTimeMillis
                    } else {
                        finish()
                    }
                } else {
                    onSupportNavigateUp()
                }
            }
        })
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setupDrawer()
        startObservingViewModel()
    }

    private fun setupDrawer() {
        with(binding.navigationView) {
            getHeaderView(0)?.findViewById<TextView>(R.id.tvDrawerHeaderAppVersion)?.text = BuildConfig.VERSION_NAME

            menu.findItem(R.id.nav_greenbook_en)?.setOnMenuItemClickListener { goToGreenBookEnglish(); true }
            menu.findItem(R.id.nav_greenbook_es)?.setOnMenuItemClickListener { goToGreenBookSpanish(); true }
            menu.findItem(R.id.nav_mm_web)?.setOnMenuItemClickListener { goToWebsiteMM(); true }
            menu.findItem(R.id.nav_ema_web)?.setOnMenuItemClickListener { goToWebsiteEMA(); true }
            menu.findItem(R.id.nav_contact_mahjong_madrid)?.setOnMenuItemClickListener { goToContactMM(); true }
            menu.findItem(R.id.nav_contact_app_support)?.setOnMenuItemClickListener { goToContactSupport(); true }
        }
    }

    private fun startObservingViewModel() {
        with(lifecycleScope) {
            launch { repeatOnLifecycle(STARTED) { viewModel.errorFlow.collect(::showError) } }
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

    private fun currentGameNameObserver(gameName: String?) {
        binding.navigationView.menu.findItem(R.id.gameFragment)?.apply {
            title = gameName ?: getString(R.string.current_game)
            isVisible = gameName.isNullOrBlank().not()
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

        if (currentLanguage != languageHelper.currentLanguage) {
            currentLanguage = languageHelper.currentLanguage
            setLocale(languageHelper.currentLanguage)
            restartMainActivity()
        }
    }

    private fun restartMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}