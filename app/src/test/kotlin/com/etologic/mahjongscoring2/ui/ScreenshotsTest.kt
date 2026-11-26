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
        // Close it before tearing down so every test ends on the same clean state.
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

    @Test
    fun gameTable_alongAGame() {
        seed(*GameScenarios.progression.toTypedArray())
        launchApp()
        GameScenarios.progression.forEach { game ->
            openGame(game.game.gameId)
            screenshot("game_table_${game.name}")
            scenario!!.onActivity { androidx.navigation.Navigation.findNavController(it, R.id.fragmentContainerView).popBackStack(R.id.oldGamesFragment, false) }
            settle()
        }
    }

    @Test
    fun gameTable_bigSystemFont() {
        android.provider.Settings.System.putFloat(context.contentResolver, android.provider.Settings.System.FONT_SCALE, 1.6f)
        openGameScenario(GameScenarios.specialNames)
        screenshot("game_table_big_font")
    }

    @Test
    fun gameTable_emptyGameName() {
        openGameScenario(GameScenarios.emptyGameName)
        screenshot("game_table_empty_game_name")
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

    @Test
    fun gameList_newGame_emptyState() {
        openGameScenario(GameScenarios.newGame)
        openListTab()
        screenshot("game_list_empty")
    }

    @Test
    fun gameList_sixteenRounds() {
        openGameScenario(GameScenarios.sixteenDraws)
        openListTab()
        screenshot("game_list_sixteen_draws")
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
    fun dialog_handActions_withoutPenalties_andDiffsDisabled() {
        setDiffsFeatureEnabled(false)
        openGameScenario(GameScenarios.newGame)
        seatName(R.id.iGameTableSeatEast).safeClick()
        screenshot("dialog_hand_actions_simple")
    }

    @Test
    fun dialog_penaltyNotDivided() {
        openGameScenario(GameScenarios.penalties)
        seatName(R.id.iGameTableSeatNorth).safeClick()
        onDialogView(R.id.btHandActionsDialogPenalty).safeClick()
        onDialogView(R.id.btCustomNumPad5).safeClick()
        onDialogView(R.id.cbPenaltyDialog).safeClick()
        screenshot("dialog_penalty_not_divided")
    }

    @Test
    fun dialog_cancelPenaltiesConfirmation() {
        openGameScenario(GameScenarios.penalties)
        seatName(R.id.iGameTableSeatEast).safeClick()
        onDialogView(R.id.btHandActionsDialogPenaltiesCancel).safeClick()
        screenshot("dialog_cancel_penalties")
    }

    @Test
    fun dialog_dice_beforeRolling() {
        openGameScenario(GameScenarios.penalties)
        onView(withId(R.id.fabGameTable)).safeClick()
        screenshot("dialog_dice")
    }

    @Test
    fun dialog_ranking_ties() {
        openGameScenario(GameScenarios.tiePairs)
        navigateTo(R.id.rankingDialogFragment)
        screenshot("dialog_ranking_ties")
    }

    @Test
    fun dialog_ranking_sixteenHands_noResume() {
        openGameScenario(GameScenarios.sixteenDraws)
        onView(withId(R.id.fabGameTable)).safeClick()
        screenshot("dialog_ranking_no_resume")
    }

    @Test
    fun dialog_shareGame() {
        seed(GameScenarios.fullGame)
        launchApp()
        onView(withId(R.id.btOldGameItemShare)).safeClick()
        screenshot("dialog_share_game")
    }

    @Test
    fun dialog_deleteGame() {
        seed(GameScenarios.fullGame)
        launchApp()
        onView(withId(R.id.btOldGameItemDelete)).safeClick()
        screenshot("dialog_delete_game")
    }

    @Test
    fun dialog_changeLanguage() {
        launchApp()
        scenario!!.onActivity { org.robolectric.Shadows.shadowOf(it).clickMenuItem(R.id.action_change_language) }
        settle()
        screenshot("dialog_change_language")
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
    fun combinations_descriptions() {
        launchApp()
        navigateTo(R.id.combinationsFragment)
        scenario!!.onActivity { org.robolectric.Shadows.shadowOf(it).clickMenuItem(R.id.action_toggle_combination_explanation) }
        settle()
        screenshot("combinations_descriptions")
    }

    @Test
    fun combinations_noResults() {
        launchApp()
        navigateTo(R.id.combinationsFragment)
        scenario!!.onActivity { activity ->
            val fragment = activity.supportFragmentManager.findFragmentById(R.id.fragmentContainerView)!!
                .childFragmentManager.fragments.filterIsInstance<CombinationsFragment>().single()
            ViewModelProvider(fragment)[CombinationsViewModel::class.java].searchCombination("zzzz")
        }
        settle()
        screenshot("combinations_no_results")
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
        navigateTo(R.id.diffsCalculatorFragment)
        screenshot("lang_${language}_diffs_calculator")
        openDrawer()
        screenshot("lang_${language}_drawer")
        scenario!!.onActivity { it.binding.drawerLayout.closeDrawers() }
        openGame(GameScenarios.penalties.game.gameId)
        seatName(R.id.iGameTableSeatSouth).safeClick()
        screenshot("lang_${language}_dialog_hand_actions")
        onDialogView(R.id.btHandActionsDialogHu).safeClick()
        screenshot("lang_${language}_dialog_hu")
        onDialogView(R.id.btGameHuDialogCancel).safeClick()
        seatName(R.id.iGameTableSeatSouth).safeClick()
        onDialogView(R.id.btHandActionsDialogPenalty).safeClick()
        screenshot("lang_${language}_dialog_penalty")
        onDialogView(R.id.btPenaltyDialogCancel).safeClick()
        openGame(GameScenarios.fullGame.game.gameId)
        navigateTo(R.id.rankingDialogFragment)
        screenshot("lang_${language}_dialog_ranking")
    }

    // endregion
}
