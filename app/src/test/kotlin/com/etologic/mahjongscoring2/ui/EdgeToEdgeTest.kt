package com.etologic.mahjongscoring2.ui

import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.testutil.GameScenarios
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Apps targeting Android 15+ are always drawn edge-to-edge, so MainActivity applies the system bar, cutout and
 * keyboard insets itself. These tests send fake insets (in px) to the window and check where they end up.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class EdgeToEdgeTest : UiTestBase() {

    private val statusBar = 63
    private val navigationBar = 42

    private fun dispatch(
        bars: Insets = Insets.of(0, statusBar, 0, navigationBar),
        ime: Insets = Insets.NONE,
        cutout: Insets = Insets.NONE,
    ) {
        scenario!!.onActivity { activity ->
            val insets = WindowInsetsCompat.Builder()
                .setInsets(WindowInsetsCompat.Type.systemBars(), bars)
                .setInsets(WindowInsetsCompat.Type.ime(), ime)
                .setInsets(WindowInsetsCompat.Type.displayCutout(), cutout)
                .setVisible(WindowInsetsCompat.Type.ime(), ime != Insets.NONE)
                .build()
            ViewCompat.dispatchApplyWindowInsets(activity.binding.root, insets)
        }
        settle()
    }

    private data class Paddings(val appBarTop: Int, val content: List<Int>, val bottomNavBottom: Int)

    private fun paddings(): Paddings {
        var result: Paddings? = null
        scenario!!.onActivity { a ->
            val c = a.binding.mainContent
            result = Paddings(
                appBarTop = a.binding.appBarLayout.paddingTop,
                content = listOf(c.paddingLeft, c.paddingTop, c.paddingRight, c.paddingBottom),
                bottomNavBottom = a.binding.bottomNavigationView.paddingBottom,
            )
        }
        return result!!
    }

    @Test
    fun `status bar pads the app bar and navigation bar pads the bottom navigation`() {
        launchApp()
        val before = paddings()
        dispatch()
        val after = paddings()
        assertEquals(statusBar, after.appBarTop)
        assertEquals(listOf(0, 0, 0, 0), after.content)
        assertEquals(before.bottomNavBottom + navigationBar, after.bottomNavBottom)
    }

    @Test
    fun `insets are replaced, not accumulated, when they change`() {
        launchApp()
        val before = paddings()
        dispatch()
        dispatch()
        dispatch(bars = Insets.of(0, 30, 0, 10))
        val after = paddings()
        assertEquals(30, after.appBarTop)
        assertEquals(before.bottomNavBottom + 10, after.bottomNavBottom)
    }

    @Test
    fun `keyboard lifts the whole content above it`() {
        launchApp()
        val before = paddings()
        dispatch(ime = Insets.of(0, 0, 0, 700))
        val withKeyboard = paddings()
        assertEquals(listOf(0, 0, 0, 700), withKeyboard.content)
        assertEquals(before.bottomNavBottom, withKeyboard.bottomNavBottom)

        dispatch()
        val keyboardHidden = paddings()
        assertEquals(listOf(0, 0, 0, 0), keyboardHidden.content)
        assertEquals(before.bottomNavBottom + navigationBar, keyboardHidden.bottomNavBottom)
    }

    @Test
    fun `side bars and display cutouts in landscape pad the content sides`() {
        launchApp()
        dispatch(bars = Insets.of(0, statusBar, 126, 0), cutout = Insets.of(80, 0, 0, 0))
        val after = paddings()
        assertEquals(listOf(80, 0, 126, 0), after.content)
    }

    @Test
    fun `screenshot with system bars`() {
        launchApp()
        dispatch()
        screenshot("edge_to_edge_old_games")
    }

    @Test
    fun `screenshot of a game with system bars`() {
        seed(GameScenarios.penalties)
        launchApp()
        openGame(GameScenarios.penalties.game.gameId)
        dispatch()
        screenshot("edge_to_edge_game_table")
    }

    @Test
    fun `screenshot of the drawer with system bars`() {
        launchApp()
        dispatch()
        openDrawer()
        screenshot("edge_to_edge_drawer")
    }

    @Test
    fun `screenshot of the full screen hand actions dialog`() {
        seed(GameScenarios.penalties)
        launchApp()
        openGame(GameScenarios.penalties.game.gameId)
        dispatch()
        seat(R.id.iGameTableSeatSouth).safeClick()
        screenshot("edge_to_edge_dialog_hand_actions")
    }
}
