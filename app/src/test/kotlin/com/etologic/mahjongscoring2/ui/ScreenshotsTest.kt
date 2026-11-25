package com.etologic.mahjongscoring2.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.etologic.mahjongscoring2.R
import androidx.lifecycle.ViewModelProvider
import com.etologic.mahjongscoring2.app.screens.combinations.CombinationsFragment
import com.etologic.mahjongscoring2.app.screens.combinations.CombinationsViewModel
import com.etologic.mahjongscoring2.business.model.enums.SeatsArrangement
import com.etologic.mahjongscoring2.testutil.GameScenario
import com.etologic.mahjongscoring2.testutil.GameScenarios
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.allOf
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment
import kotlinx.coroutines.runBlocking

/**
 * Golden screenshots of every screen and dialog (Roborazzi), stored in app/src/test/screenshots.
 *
 *  - Record:  ./gradlew recordRoborazziDebug
 *  - Verify:  ./gradlew verifyRoborazziDebug   (fails on any pixel difference)
 *  - Compare: ./gradlew compareRoborazziDebug  (writes *_compare.png diffs)
 *
 * Every screenshot uses fixed data (GameScenarios), UTC and a fixed device, so it is fully reproducible.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ScreenshotsTest : UiTestBase() {

    private fun openGameScenario(scenario: GameScenario) {
        seed(scenario)
        launchApp()
        openGame(scenario.game.gameId)
    }

    private fun seatName(seat: Int) = onView(allOf(withId(R.id.tvTableSeatBySeatName), isDescendantOfA(withId(seat))))

    private fun openListTab() {
        onView(withText(R.string.list)).perform(click())
        settle()
    }

    // region Old games

    @Test
    fun oldGames_empty() {
        launchApp()
        screenshot("old_games_empty")
    }

    @Test
    fun oldGames_list() {
        seed(GameScenarios.fullGame, GameScenarios.penalties, GameScenarios.specialNames, GameScenarios.newGame)
        launchApp()
        screenshot("old_games_list")
    }

    @Test
    fun oldGames_newGameDialog() {
        launchApp()
        onView(withId(R.id.fabOldGames)).safeClick()
        screenshot("old_games_new_game_dialog")
        // Close it before tearing down: destroying the activity with this dialog open crashes (see KnownIssuesTest).
        onDialogView(R.id.btNamesDialogCancel).safeClick()
    }

    @Test
    fun drawer_open() {
        launchApp()
        openDrawer()
        screenshot("drawer_open")
    }

    // endregion

    // region Game table

    @Test
    fun gameTable_newGame() {
        openGameScenario(GameScenarios.newGame)
        screenshot("game_table_new_game")
    }

    @Test
    fun gameTable_penalties_seatsDown() {
        openGameScenario(GameScenarios.penalties)
        screenshot("game_table_penalties_down")
    }

    @Test
    fun gameTable_penalties_seatsOut() {
        setSeatsArrangement(SeatsArrangement.OUT)
        openGameScenario(GameScenarios.penalties)
        screenshot("game_table_penalties_out")
    }

    @Test
    fun gameTable_penalties_seatsRanking() {
        setSeatsArrangement(SeatsArrangement.RANKING)
        openGameScenario(GameScenarios.penalties)
        screenshot("game_table_penalties_ranking")
    }

    @Test
    fun gameTable_diffsShown() {
        openGameScenario(GameScenarios.penalties)
        onView(withId(R.id.btTableSeatsShowDiffs)).pressAndHold()
        screenshot("game_table_penalties_diffs")
        onView(withId(R.id.btTableSeatsShowDiffs)).release()
    }

    @Test
    fun gameTable_rankingWithDiffs() {
        setSeatsArrangement(SeatsArrangement.RANKING)
        openGameScenario(GameScenarios.randomGames[14])
        onView(withId(R.id.btTableSeatsByRankingShowDiffs)).pressAndHold()
        screenshot("game_table_ranking_diffs")
        onView(withId(R.id.btTableSeatsByRankingShowDiffs)).release()
    }

    @Test
    fun gameTable_diffsFeatureDisabled() {
        setDiffsFeatureEnabled(false)
        openGameScenario(GameScenarios.penalties)
        screenshot("game_table_penalties_diffs_disabled")
    }

    @Test
    fun gameTable_southWind() = gameTableAtRound(GameScenarios.randomGames[4], "game_table_round_05_south")

    @Test
    fun gameTable_westWind() = gameTableAtRound(GameScenarios.randomGames[10], "game_table_round_11_west")

    @Test
    fun gameTable_northWind() = gameTableAtRound(GameScenarios.randomGames[14], "game_table_round_15_north")

    private fun gameTableAtRound(scenario: GameScenario, name: String) {
        check(scenario.game.endDate == null) { "${scenario.name} must be an ongoing game" }
        openGameScenario(scenario)
        screenshot(name)
    }

    @Test
    fun gameTable_endedGame() {
        openGameScenario(GameScenarios.fullGame)
        screenshot("game_table_ended")
    }

    @Test
    fun gameTable_specialNames() {
        openGameScenario(GameScenarios.specialNames)
        screenshot("game_table_special_names")
    }

    // endregion

    // region Game list

    @Test
    fun gameList_fullGame() {
        openGameScenario(GameScenarios.fullGame)
        openListTab()
        screenshot("game_list_full_game")
    }

    @Test
    fun gameList_penalties() {
        openGameScenario(GameScenarios.penalties)
        openListTab()
        screenshot("game_list_penalties")
    }

    // endregion

    // region Dialogs

    @Test
    fun dialog_handActions() {
        openGameScenario(GameScenarios.penalties)
        seatName(R.id.iGameTableSeatSouth).safeClick()
        screenshot("dialog_hand_actions")
    }

    @Test
    fun dialog_hu() {
        openGameScenario(GameScenarios.penalties)
        seatName(R.id.iGameTableSeatSouth).safeClick()
        onDialogView(R.id.btHandActionsDialogHu).safeClick()
        screenshot("dialog_hu")
    }

    @Test
    fun dialog_hu_withPointsAndDiscarder() {
        openGameScenario(GameScenarios.penalties)
        seatName(R.id.iGameTableSeatSouth).safeClick()
        onDialogView(R.id.btHandActionsDialogHu).safeClick()
        onDialogView(R.id.btCustomNumPad2).safeClick()
        onDialogView(R.id.btCustomNumPad4).safeClick()
        onDialogView(R.id.iDiscarderSelectorPlayer2Container).safeClick()
        screenshot("dialog_hu_filled")
    }

    @Test
    fun dialog_penalty() {
        openGameScenario(GameScenarios.penalties)
        seatName(R.id.iGameTableSeatWest).safeClick()
        onDialogView(R.id.btHandActionsDialogPenalty).safeClick()
        screenshot("dialog_penalty")
    }

    @Test
    fun dialog_ranking() {
        openGameScenario(GameScenarios.fullGame)
        onView(withId(R.id.fabGameTable)).safeClick()
        screenshot("dialog_ranking")
    }

    @Test
    fun dialog_editNames() {
        openGameScenario(GameScenarios.fullGame)
        navigateTo(R.id.editNamesDialogFragment)
        screenshot("dialog_edit_names")
        onDialogView(R.id.btNamesDialogCancel).safeClick()
    }

    // endregion

    // region Other screens

    @Test
    fun combinations() {
        launchApp()
        navigateTo(R.id.combinationsFragment)
        screenshot("combinations")
    }

    @Test
    fun combinations_filtered() {
        launchApp()
        navigateTo(R.id.combinationsFragment)
        scenario!!.onActivity { activity ->
            val fragment = activity.supportFragmentManager.findFragmentById(R.id.fragmentContainerView)!!
                .childFragmentManager.fragments.filterIsInstance<CombinationsFragment>().single()
            ViewModelProvider(fragment)[CombinationsViewModel::class.java].searchCombination("dragon")
        }
        settle()
        screenshot("combinations_filtered")
    }

    @Test
    fun diffsCalculator() {
        launchApp()
        navigateTo(R.id.diffsCalculatorFragment)
        screenshot("diffs_calculator")
    }

    // endregion

    // region Languages

    private fun inLanguage(language: String, block: () -> Unit) {
        RuntimeEnvironment.setQualifiers("+$language")
        runBlocking { languageRepository.save(language) }
        block()
    }

    @Test
    fun language_es() = languageSet("es")

    @Test
    fun language_fr() = languageSet("fr")

    @Test
    fun language_it() = languageSet("it")

    @Test
    fun language_nl() = languageSet("nl")

    @Test
    fun language_zh() = languageSet("zh")

    private fun languageSet(language: String) = inLanguage(language) {
        seed(GameScenarios.fullGame, GameScenarios.penalties)
        launchApp()
        screenshot("lang_${language}_old_games")
        openGame(GameScenarios.penalties.game.gameId)
        screenshot("lang_${language}_game_table")
        openListTab()
        screenshot("lang_${language}_game_list")
        navigateTo(R.id.combinationsFragment)
        screenshot("lang_${language}_combinations")
    }

    // endregion
}
