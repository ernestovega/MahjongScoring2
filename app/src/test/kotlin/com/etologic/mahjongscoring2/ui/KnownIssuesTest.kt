package com.etologic.mahjongscoring2.ui

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.testutil.GameScenarios
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Bugs found while writing the test suite. Each test describes the EXPECTED behaviour and is @Ignore'd
 * because it fails today. Remove the @Ignore when the bug is fixed (in the Android app or in the KMP port).
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class KnownIssuesTest : UiTestBase() {

    /**
     * MainActivity.navigateTopLevel() navigates with popUpTo(start, saveState = true) + restoreState + singleTop.
     * Once Game has been visited, selecting Old games (the start destination) restores Game's saved back stack
     * instead: from Game nothing happens, and from Combinations the user lands on Game, not on Old games.
     * Before visiting Game, Combinations -> Old games works.
     */
    @Ignore("Bug: from the Game screen, the bottom navigation 'Old games' item does nothing")
    @Test
    fun bottomNavigation_fromGameToOldGames() {
        seed(GameScenarios.penalties)
        launchApp()
        openGame(GameScenarios.penalties.game.gameId)
        scenario!!.onActivity { it.findViewById<BottomNavigationView>(R.id.bottomNavigationView).selectedItemId = R.id.oldGamesFragment }
        settle()
        assertThat(currentDestination()).isEqualTo(R.id.oldGamesFragment)
    }

    @Ignore("Bug: after visiting Game, Combinations -> 'Old games' lands on Game")
    @Test
    fun bottomNavigation_fromCombinationsToOldGames_afterVisitingGame() {
        seed(GameScenarios.penalties)
        launchApp()
        openGame(GameScenarios.penalties.game.gameId)
        scenario!!.onActivity { it.findViewById<BottomNavigationView>(R.id.bottomNavigationView).selectedItemId = R.id.combinationsFragment }
        settle()
        scenario!!.onActivity { it.findViewById<BottomNavigationView>(R.id.bottomNavigationView).selectedItemId = R.id.oldGamesFragment }
        settle()
        assertThat(currentDestination()).isEqualTo(R.id.oldGamesFragment)
    }
}
