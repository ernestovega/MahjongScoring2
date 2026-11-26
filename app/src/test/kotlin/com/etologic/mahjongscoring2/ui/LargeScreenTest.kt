package com.etologic.mahjongscoring2.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.testutil.GameScenarios
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * Android 16 ignores the portrait lock (android:screenOrientation) on large screens (smallest width >= 600dp):
 * tablets, unfolded foldables and desktop windows show the app in landscape and resize it freely.
 * These screenshots show every main screen on a 10" tablet in landscape, and the tests check the state survives
 * the rotations and resizes that now recreate the activity.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@Config(application = HiltTestApplication::class, qualifiers = LargeScreenTest.TABLET_LANDSCAPE)
class LargeScreenTest : UiTestBase() {

    companion object {
        const val TABLET_LANDSCAPE = "w1280dp-h800dp-xlarge-long-notround-land-mdpi-keyshidden-nonav"
    }

    private fun openPenaltiesGame() {
        seed(GameScenarios.penalties)
        launchApp()
        openGame(GameScenarios.penalties.game.gameId)
    }

    @Test
    fun oldGamesList() {
        seed(GameScenarios.fullGame, GameScenarios.penalties, GameScenarios.specialNames, GameScenarios.newGame)
        launchApp()
        screenshot("tablet_land_old_games_list")
    }

    @Test
    fun drawer() {
        launchApp()
        openDrawer()
        screenshot("tablet_land_drawer")
    }

    @Test
    fun gameTable() {
        openPenaltiesGame()
        screenshot("tablet_land_game_table")
    }

    @Test
    fun gameList() {
        seed(GameScenarios.fullGame)
        launchApp()
        openGame(GameScenarios.fullGame.game.gameId)
        onView(withText(R.string.list)).perform(click())
        settle()
        screenshot("tablet_land_game_list")
    }

    @Test
    fun handActionsDialog() {
        openPenaltiesGame()
        seat(R.id.iGameTableSeatSouth).safeClick()
        screenshot("tablet_land_dialog_hand_actions")
    }

    @Test
    fun huDialog() {
        openPenaltiesGame()
        seat(R.id.iGameTableSeatSouth).safeClick()
        onDialogView(R.id.btHandActionsDialogHu).safeClick()
        screenshot("tablet_land_dialog_hu")
    }

    @Test
    fun rankingDialog() {
        seed(GameScenarios.fullGame)
        launchApp()
        openGame(GameScenarios.fullGame.game.gameId)
        onView(withId(R.id.fabGameTable)).safeClick()
        screenshot("tablet_land_dialog_ranking")
    }

    @Test
    fun combinations() {
        launchApp()
        navigateTo(R.id.combinationsFragment)
        screenshot("tablet_land_combinations")
    }

    @Test
    fun diffsCalculator() {
        launchApp()
        navigateTo(R.id.diffsCalculatorFragment)
        screenshot("tablet_land_diffs_calculator")
    }

    @Test
    fun rotatingToPortraitKeepsTheOpenGameAndTheHuDialog() {
        openPenaltiesGame()
        seat(R.id.iGameTableSeatSouth).safeClick()
        onDialogView(R.id.btHandActionsDialogHu).safeClick()

        RuntimeEnvironment.setQualifiers("w800dp-h1280dp-port")
        scenario!!.recreate()
        settle()

        assertEquals(R.id.handActionsDialogFragment, currentDestination())
        onDialogView(R.id.btCustomNumPad2).check { view, _ -> assertEquals(true, view.isShown) }
        screenshot("tablet_port_dialog_hu_after_rotation")
    }
}
