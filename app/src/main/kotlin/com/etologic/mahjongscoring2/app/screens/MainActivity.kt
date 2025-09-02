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

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.etologic.mahjongscoring2.BuildConfig
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.utils.LanguageHelper
import com.etologic.mahjongscoring2.app.utils.setLocale
import com.etologic.mahjongscoring2.business.model.entities.GameId
import com.etologic.mahjongscoring2.business.model.entities.UiGame.Companion.NOT_SET_GAME_ID
import com.etologic.mahjongscoring2.databinding.MainActivityBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: MainActivityBinding

    private lateinit var appBarConfiguration: AppBarConfiguration

    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var languageHelper: LanguageHelper

    private var currentLanguage: String? = null

    private val navController by lazy { (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment).navController }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
    }

    private fun setupToolbar() {
        appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(R.id.oldGamesFragment, R.id.gameFragment, R.id.combinationsFragment),
            drawerLayout = binding.drawerLayout,
        )
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navigationView.setupWithNavController(navController)
        // binding.bottomNavigationView.setupWithNavController(navController) // removed
    }

    override fun onSupportNavigateUp(): Boolean =
        navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setupDrawer()
        setupBottomNav()
        syncBottomNavSelection()
        startObservingViewModel()
    }

    private fun setupDrawer() {
        with(binding.navigationView) {
            getHeaderView(0)?.findViewById<TextView>(R.id.tvDrawerHeaderAppVersion)?.text = BuildConfig.VERSION_NAME

            setNavigationItemSelectedListener { menuItem ->
                val handledGraph = when (menuItem.itemId) {
                    R.id.oldGamesFragment -> navigateTopLevel(R.id.oldGamesFragment)
                    R.id.gameFragment -> navigateTopLevel(R.id.gameFragment)
                    R.id.combinationsFragment -> navigateTopLevel(R.id.combinationsFragment)
                    R.id.diffsCalculatorFragment -> { navController.navigate(R.id.diffsCalculatorFragment); true }
                    else -> false
                }
                val handledExternal = when (menuItem.itemId) {
                    R.id.nav_greenbook_en -> { goToGreenBookEnglish(); true }
                    R.id.nav_greenbook_es -> { goToGreenBookSpanish(); true }
                    R.id.nav_mm_web -> { goToWebsiteMM(); true }
                    R.id.nav_ema_web -> { goToWebsiteEMA(); true }
                    R.id.nav_contact_mahjong_madrid -> { goToContactMM(); true }
                    R.id.nav_contact_app_support -> { goToContactSupport(); true }
                    else -> false
                }
                val handled = handledGraph || handledExternal
                if (handled) binding.drawerLayout.closeDrawers()
                handled
            }
        }
    }

    private fun setupBottomNav() {
        with(binding.bottomNavigationView) {
            setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.oldGamesFragment -> navigateTopLevel(R.id.oldGamesFragment)
                    R.id.gameFragment -> navigateTopLevel(R.id.gameFragment)
                    R.id.combinationsFragment -> navigateTopLevel(R.id.combinationsFragment)
                    else -> false
                }
            }
        }
    }

    @Suppress("SameReturnValue")
    private fun navigateTopLevel(@IdRes destinationId: Int): Boolean {
        if (navController.currentDestination?.id == destinationId) return true
        val graph = navController.graph
        val options = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setRestoreState(true)
            .setPopUpTo(graph.startDestinationId, false, true) // save state of previous top-level
            .build()
        navController.navigate(destinationId, null, options)
        return true
    }

    private fun syncBottomNavSelection() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.oldGamesFragment,
                R.id.gameFragment,
                R.id.combinationsFragment -> {
                    if (binding.bottomNavigationView.selectedItemId != destination.id) {
                        binding.bottomNavigationView.menu.findItem(destination.id)?.isChecked = true
                        binding.bottomNavigationView.selectedItemId = destination.id
                    }
                }
            }
        }
    }

    private fun startObservingViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.activeGameId.collect(::toggleGameBottomNav)
            }
        }
    }

    private fun toggleGameBottomNav(gameId: GameId) {
        listOf(
            binding.bottomNavigationView.menu.findItem(R.id.gameFragment),
            binding.navigationView.menu.findItem(R.id.gameFragment),
        ).onEach { it?.isEnabled = gameId != NOT_SET_GAME_ID }
    }

    override fun onStart() {
        super.onStart()
        currentLanguage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales.get(0).language
        } else {
            @Suppress("DEPRECATION")
            resources.configuration.locale.language
        }

        with(languageHelper.currentLanguage) {
            if (currentLanguage != this) {
                currentLanguage = this
                setLocale(this)
                recreate()
            }
        }
    }

    fun restartMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}