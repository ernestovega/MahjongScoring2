package com.etologic.mahjongscoring2.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.testutil.GameScenarios
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Regression tests: the activity is destroyed (process death, "Don't keep activities", system kill)
 * while a names dialog is open. Before the fix, onDismiss() read a binding already nulled -> NPE.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class DialogLifecycleTest : UiTestBase() {

    @Test
    fun newGameDialog_survivesActivityDestruction() {
        launchApp()
        onView(withId(R.id.fabOldGames)).safeClick()
        scenario!!.close()
        scenario = null
    }

    @Test
    fun editNamesDialog_survivesActivityDestruction() {
        seed(GameScenarios.penalties)
        launchApp()
        openGame(GameScenarios.penalties.game.gameId)
        navigateTo(R.id.editNamesDialogFragment)
        scenario!!.close()
        scenario = null
    }
}
