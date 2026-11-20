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

package com.etologic.mahjongscoring2.app.crashes

import androidx.hilt.navigation.HiltViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.screens.MainActivity
import com.etologic.mahjongscoring2.app.screens.MainViewModel
import com.etologic.mahjongscoring2.app.screens.game.GameUiState
import com.etologic.mahjongscoring2.app.screens.game.GameViewModel
import com.etologic.mahjongscoring2.business.model.entities.GameId
import com.etologic.mahjongscoring2.business.use_cases.CreateGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.GetOneGameFlowUseCase
import com.etologic.mahjongscoring2.business.use_cases.HuDrawUseCase
import dagger.hilt.android.testing.HiltAndroidRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import javax.inject.Inject

/**
 * Base class for the tests that reproduce Crashlytics crashes.
 * A crash on the main thread stops the test process, so the test fails.
 */
abstract class BaseCrashTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var createGameUseCase: CreateGameUseCase

    @Inject
    lateinit var getOneGameFlowUseCase: GetOneGameFlowUseCase

    @Inject
    lateinit var huDrawUseCase: HuDrawUseCase

    protected lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setUp() {
        hiltRule.inject()
        scenario = ActivityScenario.launch(MainActivity::class.java)
        waitForIdle()
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    protected fun waitForIdle() {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }

    /** Waits until the condition is true, or fails after the timeout. */
    protected fun waitUntil(timeoutMs: Long = 5_000, condition: () -> Boolean) {
        val end = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < end) {
            waitForIdle()
            if (condition()) return
            Thread.sleep(50)
        }
        throw AssertionError("Condition not met in $timeoutMs ms")
    }

    /** Waits the given time and lets the main thread run its pending work. */
    protected fun settle(ms: Long = 1_000) {
        Thread.sleep(ms)
        waitForIdle()
    }

    protected fun <T> onActivity(block: (MainActivity) -> T): T {
        var result: T? = null
        scenario.onActivity { result = block(it) }
        @Suppress("UNCHECKED_CAST")
        return result as T
    }

    protected fun MainActivity.navController(): NavController =
        (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment).navController

    protected fun navigate(destinationId: Int) {
        onActivity { it.navController().navigate(destinationId) }
        waitForIdle()
    }

    protected fun currentDestinationId(): Int? =
        onActivity { it.navController().currentDestination?.id }

    /** Returns the same GameViewModel instance that the game screens use. */
    protected fun gameViewModel(): GameViewModel = onActivity { activity ->
        val entry = activity.navController().getBackStackEntry(R.id.nav_graph_main)
        ViewModelProvider(entry, HiltViewModelFactory(activity, entry))[GameViewModel::class.java]
    }

    protected fun createGame(draws: Int = 0): GameId = runBlocking {
        val gameId = createGameUseCase("Test game", "East", "South", "West", "North").getOrThrow()
        repeat(draws) {
            huDrawUseCase(getOneGameFlowUseCase(gameId).first()).getOrThrow()
        }
        gameId
    }

    /** Opens the game screen with the given game, and waits until the game is loaded. */
    protected fun openGame(gameId: GameId) {
        onActivity { ViewModelProvider(it)[MainViewModel::class.java].updateActiveGameId(gameId) }
        navigate(R.id.gameFragment)
        val viewModel = gameViewModel()
        waitUntil { viewModel.gameUiStateFlow.value is GameUiState.Loaded }
    }

    protected fun loadedState(): GameUiState.Loaded =
        gameViewModel().gameUiStateFlow.value as GameUiState.Loaded
}
