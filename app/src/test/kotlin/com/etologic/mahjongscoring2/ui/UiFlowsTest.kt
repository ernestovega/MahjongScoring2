package com.etologic.mahjongscoring2.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Looper
import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import org.hamcrest.Matcher
import org.robolectric.shadows.ShadowLooper
import java.time.Duration
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.viewpager2.widget.ViewPager2
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.business.model.entities.GameId
import com.etologic.mahjongscoring2.business.model.enums.SeatsArrangement
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.EAST
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NONE
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NORTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.SOUTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.WEST
import com.etologic.mahjongscoring2.business.use_cases.mappers.toUiGame
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbRound
import com.etologic.mahjongscoring2.data_source.repositories.SelectedSeatsOrientationRepository
import com.etologic.mahjongscoring2.testutil.GameScenarios
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.allOf
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowAlertDialog
import org.robolectric.shadows.ShadowDialog

/**
 * End-to-end user flows on the real UI (Espresso on Robolectric) against a real in-memory Room database.
 * Each flow asserts both what the user sees and what ends up persisted.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class UiFlowsTest : UiTestBase() {


    // region Old games

    @Test
    fun createGame_fromOldGames_opensTheGameAndPersistsIt() {
        launchApp()
        onView(withId(R.id.emptyLayoutOldGames)).check(matches(isDisplayed()))
        onView(withId(R.id.fabOldGames)).safeClick()
        onDialogView(R.id.tietNamesDialogGameName).perform(replaceText("Liga"))
        onDialogView(R.id.tietNamesDialogEast).perform(replaceText("Ana"))
        onDialogView(R.id.tietNamesDialogSouth).perform(replaceText("Bruno"))
        onDialogView(R.id.tietNamesDialogWest).perform(replaceText("   "))
        onDialogView(R.id.tietNamesDialogNorth).perform(replaceText("David"))
        onDialogView(R.id.btNamesDialogSave).safeClick()

        assertThat(currentDestination()).isEqualTo(R.id.gameFragment)
        val game = allGames().single()
        assertThat(game.gameName).isEqualTo("Liga")
        // A blank name falls back to the default "Player 3" label.
        assertThat(listOf(game.nameP1, game.nameP2, game.nameP3, game.nameP4))
            .containsExactly("Ana", "Bruno", context.getString(R.string.player_three), "David").inOrder()
        assertThat(rounds(game.gameId)).hasSize(1)
        onView(withId(R.id.tvGameTableGameName)).check(matches(withText("Liga")))
        seat(R.id.iGameTableSeatEast).check(matches(withText("Ana")))
        seat(R.id.iGameTableSeatSouth).check(matches(withText("Bruno")))
        seat(R.id.iGameTableSeatNorth).check(matches(withText("David")))
    }

    @Test
    fun createGame_defaultValues() {
        launchApp()
        onView(withId(R.id.fabOldGames)).safeClick()
        onDialogView(R.id.tietNamesDialogEast).check(matches(withText(R.string.player_one)))
        onDialogView(R.id.tietNamesDialogNorth).check(matches(withText(R.string.player_four)))
        onDialogView(R.id.btNamesDialogSave).safeClick()
        val game = allGames().single()
        assertThat(game.gameName).matches("""\d{2}/\d{2}/\d{4} \d{2}:\d{2}""")
    }

    @Test
    fun cancelNewGame_createsNothing() {
        launchApp()
        onView(withId(R.id.fabOldGames)).safeClick()
        onDialogView(R.id.btNamesDialogCancel).safeClick()
        assertThat(allGames()).isEmpty()
        assertThat(currentDestination()).isEqualTo(R.id.oldGamesFragment)
    }

    @Test
    fun oldGameCard_opensThatGame() {
        seed(GameScenarios.penalties, GameScenarios.fullGame)
        launchApp()
        onView(allOf(withId(R.id.cvOldGameItem), hasDescendant(withText("penalties")))).safeClick()
        assertThat(currentDestination()).isEqualTo(R.id.gameFragment)
        onView(withId(R.id.tvGameTableGameName)).check(matches(withText("penalties")))
    }

    @Test
    fun deleteGame_afterConfirmation() {
        seed(GameScenarios.penalties)
        launchApp()
        onView(withId(R.id.btOldGameItemDelete)).safeClick()
        assertThat(allGames()).hasSize(1)
        confirmLatestAlertDialog()
        assertThat(allGames()).isEmpty()
        onView(withId(R.id.emptyLayoutOldGames)).check(matches(isDisplayed()))
    }

    // endregion

    // region Navigation

    @Test
    fun bottomNavigation_gameIsEnabledOnlyWithAnActiveGame() {
        seed(GameScenarios.penalties)
        launchApp()
        fun gameItemEnabled(): Boolean {
            var enabled = false
            scenario!!.onActivity { enabled = it.findViewById<BottomNavigationView>(R.id.bottomNavigationView).menu.findItem(R.id.gameFragment).isEnabled }
            return enabled
        }
        assertThat(gameItemEnabled()).isFalse()
        openGame(GameScenarios.penalties.game.gameId)
        assertThat(gameItemEnabled()).isTrue()

        scenario!!.onActivity { it.findViewById<BottomNavigationView>(R.id.bottomNavigationView).selectedItemId = R.id.combinationsFragment }
        settle()
        assertThat(currentDestination()).isEqualTo(R.id.combinationsFragment)
        scenario!!.onActivity { it.findViewById<BottomNavigationView>(R.id.bottomNavigationView).selectedItemId = R.id.gameFragment }
        settle()
        assertThat(currentDestination()).isEqualTo(R.id.gameFragment)
        scenario!!.onActivity { it.findViewById<BottomNavigationView>(R.id.bottomNavigationView).selectedItemId = R.id.oldGamesFragment }
        settle()
        assertThat(currentDestination()).isEqualTo(R.id.oldGamesFragment)
    }

    private fun selectBottomNavItem(itemId: Int) {
        scenario!!.onActivity { it.findViewById<BottomNavigationView>(R.id.bottomNavigationView).selectedItemId = itemId }
        settle()
    }

    @Test
    fun bottomNavigation_fromGameToOldGames() {
        seed(GameScenarios.penalties)
        launchApp()
        openGame(GameScenarios.penalties.game.gameId)
        selectBottomNavItem(R.id.oldGamesFragment)
        assertThat(currentDestination()).isEqualTo(R.id.oldGamesFragment)
    }

    @Test
    fun bottomNavigation_fromCombinationsToOldGames_afterVisitingGame() {
        seed(GameScenarios.penalties)
        launchApp()
        openGame(GameScenarios.penalties.game.gameId)
        selectBottomNavItem(R.id.combinationsFragment)
        selectBottomNavItem(R.id.oldGamesFragment)
        assertThat(currentDestination()).isEqualTo(R.id.oldGamesFragment)
        // And the game tab still opens the active game.
        selectBottomNavItem(R.id.gameFragment)
        assertThat(currentDestination()).isEqualTo(R.id.gameFragment)
        onView(withId(R.id.tvGameTableGameName)).check(matches(withText("penalties")))
    }

    @Test
    fun drawer_navigatesToDiffsCalculator() {
        launchApp()
        openDrawer()
        onView(withId(R.id.navigationView)).perform(NavigationViewActions.navigateTo(R.id.diffsCalculatorFragment))
        settle()
        assertThat(currentDestination()).isEqualTo(R.id.diffsCalculatorFragment)
    }

    // endregion

    // region Hands

    @Test
    fun huByDiscard_isSavedWithInitialSeatsAndShowsTheList() {
        seed(GameScenarios.newGame)
        launchApp()
        openGame(1)
        seat(R.id.iGameTableSeatSouth).safeClick()
        onDialogView(R.id.btHandActionsDialogHu).safeClick()
        typePoints(R.id.cnpGameHuDialog, 24)
        onDialogView(R.id.iDiscarderSelectorPlayer2Container).safeClick() // second loser for South winner = West
        onDialogView(R.id.btGameHuDialogOk).safeClick()

        val stored = rounds(1)
        assertThat(stored).hasSize(2)
        assertThat(stored[0].winnerInitialSeat).isEqualTo(SOUTH)
        assertThat(stored[0].discarderInitialSeat).isEqualTo(WEST)
        assertThat(stored[0].handPoints).isEqualTo(24)
        assertThat(stored[1].isOngoing()).isTrue()
        assertThat(gamePagerItem()).isEqualTo(1) // list page
        onView(withText("Totals")).check(matches(isDisplayed()))
    }

    @Test
    fun huBySelfPick_whenNoDiscarderIsSelected() {
        seed(GameScenarios.newGame)
        launchApp()
        openGame(1)
        seat(R.id.iGameTableSeatNorth).safeClick()
        onDialogView(R.id.btHandActionsDialogHu).safeClick()
        typePoints(R.id.cnpGameHuDialog, 8)
        onDialogView(R.id.btGameHuDialogOk).safeClick()
        val first = rounds(1).first()
        assertThat(first.winnerInitialSeat).isEqualTo(NORTH)
        assertThat(first.discarderInitialSeat).isEqualTo(NONE)
        assertThat(game(1).ongoingRound.totalPointsP4).isEqualTo(48)
    }

    @Test
    fun hu_belowMinimumPoints_isRejected() {
        seed(GameScenarios.newGame)
        launchApp()
        openGame(1)
        seat(R.id.iGameTableSeatEast).safeClick()
        onDialogView(R.id.btHandActionsDialogHu).safeClick()
        typePoints(R.id.cnpGameHuDialog, 7)
        onDialogView(R.id.btGameHuDialogOk).safeClick()
        assertThat(rounds(1)).hasSize(1)
        assertThat(isDialogShowing()).isTrue()
    }

    @Test
    fun hu_inALaterWind_mapsCurrentSeatsToInitialSeats() {
        // Round 5 (South wind): the current East seat is the initial South player (Bruno).
        seed(GameScenarios.penalties)
        launchApp()
        openGame(GameScenarios.penalties.game.gameId)
        seat(R.id.iGameTableSeatEast).check(matches(withText("Bruno")))
        seat(R.id.iGameTableSeatEast).safeClick()
        onDialogView(R.id.btHandActionsDialogHu).safeClick()
        typePoints(R.id.cnpGameHuDialog, 10)
        onDialogView(R.id.btGameHuDialogOk).safeClick()
        val stored = rounds(GameScenarios.penalties.game.gameId)
        assertThat(stored[4].winnerInitialSeat).isEqualTo(SOUTH)
        assertThat(stored[4].discarderInitialSeat).isEqualTo(NONE)
    }

    @Test
    fun draw_isSaved() {
        seed(GameScenarios.newGame)
        launchApp()
        openGame(1)
        seat(R.id.iGameTableSeatWest).safeClick()
        onDialogView(R.id.btHandActionsDialogDraw).safeClick()
        assertThat(rounds(1).map { it.winnerInitialSeat }).containsExactly(NONE, null).inOrder()
    }

    @Test
    fun penalty_dividedAndCancelled() {
        seed(GameScenarios.newGame)
        launchApp()
        openGame(1)
        seat(R.id.iGameTableSeatSouth).safeClick()
        onDialogView(R.id.btHandActionsDialogPenalty).safeClick()
        typePoints(R.id.cnpPenaltyDialog, 30)
        onDialogView(R.id.cbPenaltyDialog).check(matches(isChecked())) // "Divided" is checked by default
        onDialogView(R.id.btPenaltyDialogSave).safeClick()
        with(rounds(1).single()) {
            assertThat(listOf(penaltyP1, penaltyP2, penaltyP3, penaltyP4)).containsExactly(10, -30, 10, 10).inOrder()
        }
        seatPoints(R.id.iGameTableSeatSouth).check(matches(withText("-30")))

        seat(R.id.iGameTableSeatEast).safeClick()
        onDialogView(R.id.btHandActionsDialogPenaltiesCancel).safeClick()
        confirmLatestAlertDialog()
        assertThat(rounds(1).single().areTherePenaltiesInDb()).isFalse()
    }

    @Test
    fun penalty_dividedMustBeMultipleOfThree() {
        seed(GameScenarios.newGame)
        launchApp()
        openGame(1)
        seat(R.id.iGameTableSeatSouth).safeClick()
        onDialogView(R.id.btHandActionsDialogPenalty).safeClick()
        typePoints(R.id.cnpPenaltyDialog, 10)
        onDialogView(R.id.btPenaltyDialogSave).safeClick()
        assertThat(rounds(1).single().areTherePenaltiesInDb()).isFalse()
        onDialogView(R.id.btPenaltyDialogSave).check(matches(isDisplayed()))
        // Not divided, any amount is accepted.
        onDialogView(R.id.cbPenaltyDialog).safeClick()
        onDialogView(R.id.btPenaltyDialogSave).safeClick()
        with(rounds(1).single()) {
            assertThat(listOf(penaltyP1, penaltyP2, penaltyP3, penaltyP4)).containsExactly(0, -10, 0, 0).inOrder()
        }
    }

    private fun DbRound.areTherePenaltiesInDb() = penaltyP1 != 0 || penaltyP2 != 0 || penaltyP3 != 0 || penaltyP4 != 0

    // endregion

    // region Game list & game lifecycle

    @Test
    fun deleteRound_fromTheList() {
        seed(GameScenarios.selfPicks)
        launchApp()
        openGame(GameScenarios.selfPicks.game.gameId)
        onView(withText(R.string.list)).safeClick()
        onView(allOf(withId(R.id.llGameListItemContainer), hasDescendant(allOf(withId(R.id.tvGameListItemRoundNumber), withText("1"))))).safeClick()
        onView(withText(R.string.delete)).inRoot(isPlatformPopup()).perform(click())
        settle()
        confirmLatestAlertDialog()
        val remaining = rounds(GameScenarios.selfPicks.game.gameId)
        assertThat(remaining.map { it.winnerInitialSeat }).containsExactly(SOUTH, WEST, NORTH, null).inOrder()
    }

    @Test
    fun endGame_thenRankingShowsAndResume() {
        seed(GameScenarios.selfPicks)
        launchApp()
        openGame(GameScenarios.selfPicks.game.gameId)
        clickMenu(R.id.action_end_game)
        assertThat(game(GameScenarios.selfPicks.game.gameId).isEnded).isTrue()

        onView(withId(R.id.fabGameTable)).safeClick()
        assertThat(currentDestination()).isEqualTo(R.id.rankingDialogFragment)
        onDialogView(R.id.tvRankingDialogPlayer1Name).check(matches(withText("David")))
        onDialogView(R.id.tvRankingDialogPlayer1Points).check(matches(withText("4")))
        onDialogView(R.id.tvRankingDialogBestHandPlayerName).check(matches(withText("David")))
        onDialogView(R.id.btRankingDialogResume).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        onDialogView(R.id.btRankingDialogResume).safeClick()
        assertThat(game(GameScenarios.selfPicks.game.gameId).isEnded).isFalse()
    }

    @Test
    fun sixteenDraws_endTheGameAndShowTheRanking() {
        seed(GameScenarios.newGame)
        launchApp()
        openGame(1)
        repeat(16) {
            seat(R.id.iGameTableSeatEast).safeClick()
            if (it < 15) {
                onDialogView(R.id.btHandActionsDialogDraw).safeClick()
                // After each hand the list is shown: go back to the table.
                onView(withText(R.string.table)).safeClick()
            } else {
                // The app races its 300ms "highlight last round" delay against Room's update that ends the game.
                // On a device Room wins; here the clock is fake, so let the database settle before time moves.
                onDialogView(R.id.btHandActionsDialogDraw).perform(performClickWithoutAdvancingTime())
                repeat(20) { Thread.sleep(25); ShadowLooper.idleMainLooper() }
                settle()
            }
        }
        val finished = game(1)
        assertThat(finished.uiRounds).hasSize(16)
        assertThat(finished.isEnded).isTrue()
        assertThat(currentDestination()).isEqualTo(R.id.rankingDialogFragment)
        onDialogView(R.id.btRankingDialogResume).check(matches(withEffectiveVisibility(Visibility.GONE)))
        assertThat(runBlocking { SelectedSeatsOrientationRepository(context).selectedSeatsArrangementFlow.first() })
            .isEqualTo(SeatsArrangement.RANKING)
    }

    @Test
    fun editNames_fromTheMenu() {
        seed(GameScenarios.penalties)
        launchApp()
        openGame(GameScenarios.penalties.game.gameId)
        clickMenu(R.id.action_edit_names)
        onDialogView(R.id.tietNamesDialogGameName).perform(replaceText("Renamed \"game\""))
        onDialogView(R.id.tietNamesDialogEast).perform(replaceText(" Eva "))
        onDialogView(R.id.btNamesDialogSave).safeClick()
        val updated = game(GameScenarios.penalties.game.gameId)
        assertThat(updated.gameName).isEqualTo("Renamed game")
        assertThat(updated.nameP1).isEqualTo("Eva")
        onView(withId(R.id.tvGameTableGameName)).check(matches(withText("Renamed game")))
    }

    @Test
    fun seatsOrientation_menuCyclesArrangements() {
        seed(GameScenarios.penalties)
        launchApp()
        openGame(GameScenarios.penalties.game.gameId)
        val repository = SelectedSeatsOrientationRepository(context)
        fun current() = runBlocking { repository.selectedSeatsArrangementFlow.first() }
        assertThat(current()).isEqualTo(SeatsArrangement.DOWN)
        clickMenu(R.id.action_seats_orientation)
        assertThat(current()).isEqualTo(SeatsArrangement.OUT)
        clickMenu(R.id.action_seats_orientation)
        assertThat(current()).isEqualTo(SeatsArrangement.RANKING)
        onView(withId(R.id.iTableSeatsByRanking)).check(matches(isDisplayed()))
        clickMenu(R.id.action_seats_orientation)
        assertThat(current()).isEqualTo(SeatsArrangement.DOWN)
        onView(withId(R.id.iTableSeatsBySeat)).check(matches(isDisplayed()))
    }

    @Test
    fun diceDialog_opensFromTheTable() {
        seed(GameScenarios.newGame)
        launchApp()
        openGame(1)
        onView(withId(R.id.fabGameTable)).safeClick()
        assertThat(currentDestination()).isEqualTo(R.id.diceDialogFragment)
    }

    @Test
    fun diffsFeature_canBeDisabledFromTheMenu() {
        seed(GameScenarios.penalties)
        launchApp()
        openGame(GameScenarios.penalties.game.gameId)
        onView(withId(R.id.btTableSeatsShowDiffs)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        clickMenu(R.id.action_disable_diffs_calcs)
        onView(withId(R.id.btTableSeatsShowDiffs)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
        clickMenu(R.id.action_enable_diffs_calcs)
        onView(withId(R.id.btTableSeatsShowDiffs)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    // endregion

    @Suppress("unused")
    private val keepImports = listOf<Any>(EAST, View.VISIBLE)
}
