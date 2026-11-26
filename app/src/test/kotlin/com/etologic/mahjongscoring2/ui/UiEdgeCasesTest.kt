package com.etologic.mahjongscoring2.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.screens.MainActivity
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbRound
import com.etologic.mahjongscoring2.testutil.GameScenarios
import com.etologic.mahjongscoring2.testutil.Goldens
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.allOf
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowDialog
import org.robolectric.shadows.ShadowLooper
import java.io.File
import java.time.Duration

/**
 * Edge cases and less common flows on the real UI: number pad limits, every hu winner/discarder combination,
 * penalties accumulating, dialogs dismissed or cancelled, dice, sharing/exporting/importing (the intents the
 * app sends), external links, language change, back navigation, menus and activity recreation.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class UiEdgeCasesTest : UiTestBase() {

    // region Helpers

    private fun <T> onActivity(block: (MainActivity) -> T): T {
        var result: T? = null
        scenario!!.onActivity { result = block(it) }
        @Suppress("UNCHECKED_CAST")
        return result as T
    }

    private fun nextStartedActivity(): Intent? = onActivity { shadowOf(it).nextStartedActivity }

    /** Sharing/exporting runs on Dispatchers.IO: wait (real time) for the intent it starts. */
    private fun awaitStartedActivity(): Intent {
        repeat(50) {
            nextStartedActivity()?.let { return it }
            Thread.sleep(100)
            ShadowLooper.idleMainLooper()
        }
        val dialog = ShadowDialog.getLatestDialog()
        error("No activity started. Latest dialog: ${(dialog as? android.app.AlertDialog)?.let { shadowOf(it).message }}")
    }

    private fun numPadDisplay(numPadId: Int): String =
        onActivity { activity ->
            val dialogRoot = ShadowDialog.getLatestDialog().window!!.decorView
            dialogRoot.findViewById<android.view.View>(numPadId).findViewById<TextView>(R.id.etCustomNumPadDisplay).text.toString()
        }

    private fun pressNumPad(numPadId: Int, buttonId: Int) =
        onDialogView(allOf(withId(buttonId), isDescendantOfA(withId(numPadId)))).safeClick()

    private fun chooseInLatestListDialog(position: Int) {
        val dialog = ShadowDialog.getLatestDialog() as AlertDialog
        val list = dialog.listView
        list.performItemClick(list.adapter.getView(position, null, list), position, position.toLong())
        settle()
    }

    private fun latestListDialogItems(): List<String> {
        val dialog = ShadowDialog.getLatestDialog() as AlertDialog
        return (0 until dialog.listView.adapter.count).map { dialog.listView.adapter.getItem(it).toString() }
    }

    private fun latestListDialogCheckedPosition(): Int = (ShadowDialog.getLatestDialog() as AlertDialog).listView.checkedItemPosition

    private fun ongoingRound(gameId: Long): DbRound = rounds(gameId).last()

    /** DbRound.equals() only compares ids: compare every column. */
    private fun DbRound.columns() = listOf(gameId, roundId, winnerInitialSeat, discarderInitialSeat, handPoints, penaltyP1, penaltyP2, penaltyP3, penaltyP4)

    private fun roundsColumns(gameId: Long) = rounds(gameId).map { it.columns() }

    private fun menuItem(itemId: Int) = onActivity { it.binding.toolbar.menu.findItem(itemId) }

    private fun selectTab(textId: Int) {
        onView(withText(textId)).perform(androidx.test.espresso.action.ViewActions.click())
        settle()
    }

    private fun pressBack() {
        scenario!!.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        settle()
    }

    private fun chooserTarget(chooser: Intent): Intent {
        assertThat(chooser.action).isEqualTo(Intent.ACTION_CHOOSER)
        @Suppress("DEPRECATION")
        return chooser.getParcelableExtra(Intent.EXTRA_INTENT)!!
    }

    @Suppress("DEPRECATION")
    private fun sharedFiles(chooser: Intent): List<Uri> = chooserTarget(chooser).getParcelableArrayListExtra(Intent.EXTRA_STREAM)!!

    private fun readSharedFile(uri: Uri): String = context.contentResolver.openInputStream(uri)!!.bufferedReader().readText()

    // endregion

    // region Number pad and hu

    @Test
    fun numPad_digitsBackspaceAndMaxLength() {
        seed(GameScenarios.newGame)
        launchApp()
        openGame(1)
        seat(R.id.iGameTableSeatEast).safeClick()
        onDialogView(R.id.btHandActionsDialogHu).safeClick()
        val pad = R.id.cnpGameHuDialog
        pressNumPad(pad, R.id.ibCustomNumPadBackspace) // backspace on empty does nothing
        assertThat(numPadDisplay(pad)).isEmpty()
        pressNumPad(pad, R.id.btCustomNumPad1)
        pressNumPad(pad, R.id.btCustomNumPad2)
        assertThat(numPadDisplay(pad)).isEqualTo("12")
        pressNumPad(pad, R.id.ibCustomNumPadBackspace)
        assertThat(numPadDisplay(pad)).isEqualTo("1")
        pressNumPad(pad, R.id.ibCustomNumPadBackspace)
        pressNumPad(pad, R.id.btCustomNumPad0)
        pressNumPad(pad, R.id.btCustomNumPad0)
        pressNumPad(pad, R.id.btCustomNumPad0)
        pressNumPad(pad, R.id.btCustomNumPad8)
        pressNumPad(pad, R.id.btCustomNumPad9) // 5th digit: ignored, max 4 digits
        assertThat(numPadDisplay(pad)).isEqualTo("0008")
        onDialogView(R.id.btGameHuDialogOk).safeClick()
        // "0008" is read as 8 points: a valid self pick.
        with(rounds(1).first()) {
            assertThat(handPoints).isEqualTo(8)
            assertThat(winnerInitialSeat).isEqualTo(TableWinds.EAST)
            assertThat(discarderInitialSeat).isEqualTo(TableWinds.NONE)
        }
    }

    @Test
    fun hu_maximumPoints9999_isAccepted() {
        seed(GameScenarios.newGame)
        launchApp()
        openGame(1)
        seat(R.id.iGameTableSeatNorth).safeClick()
        onDialogView(R.id.btHandActionsDialogHu).safeClick()
        typePoints(R.id.cnpGameHuDialog, 9999)
        onDialogView(R.id.iDiscarderSelectorPlayer1Container).safeClick()
        onDialogView(R.id.btGameHuDialogOk).safeClick()
        with(rounds(1).first()) {
            assertThat(handPoints).isEqualTo(9999)
            assertThat(winnerInitialSeat).isEqualTo(TableWinds.NORTH)
            assertThat(discarderInitialSeat).isEqualTo(TableWinds.EAST)
        }
        seatPoints(R.id.iGameTableSeatNorth).check(matches(withText("10023")))
    }

    @Test
    fun hu_withoutPoints_isRejectedAndKeepsTheDialog() {
        seed(GameScenarios.newGame)
        launchApp()
        openGame(1)
        seat(R.id.iGameTableSeatEast).safeClick()
        onDialogView(R.id.btHandActionsDialogHu).safeClick()
        onDialogView(R.id.btGameHuDialogOk).safeClick()
        onDialogView(R.id.btGameHuDialogOk).check(matches(isDisplayed()))
        assertThat(rounds(1)).hasSize(1)
        assertThat(rounds(1).single().winnerInitialSeat).isNull()
    }

    @Test
    fun hu_discarderSelectedTwice_isASelfPick() {
        seed(GameScenarios.newGame)
        launchApp()
        openGame(1)
        seat(R.id.iGameTableSeatSouth).safeClick()
        onDialogView(R.id.btHandActionsDialogHu).safeClick()
        typePoints(R.id.cnpGameHuDialog, 10)
        onDialogView(R.id.iDiscarderSelectorPlayer3Container).safeClick()
        onDialogView(R.id.iDiscarderSelectorPlayer3Container).safeClick()
        onDialogView(R.id.btGameHuDialogOk).safeClick()
        assertThat(rounds(1).first().discarderInitialSeat).isEqualTo(TableWinds.NONE)
        assertThat(rounds(1).first().winnerInitialSeat).isEqualTo(TableWinds.SOUTH)
    }

    @Test
    fun hu_cancel_savesNothing() {
        seed(GameScenarios.newGame)
        launchApp()
        openGame(1)
        seat(R.id.iGameTableSeatSouth).safeClick()
        onDialogView(R.id.btHandActionsDialogHu).safeClick()
        typePoints(R.id.cnpGameHuDialog, 10)
        onDialogView(R.id.btGameHuDialogCancel).safeClick()
        assertThat(rounds(1).single().winnerInitialSeat).isNull()
        assertThat(currentDestination()).isEqualTo(R.id.gameFragment)
    }

    /**
     * Twelve hands, one per (winner seat, discarder button) pair, through four winds. Each time the names shown
     * in the dialog must be the ones persisted as winner and discarder (by initial seat).
     */
    @Test
    fun hu_everyWinnerAndDiscarderCombination_persistsTheNamesShown() {
        seed(GameScenarios.newGame)
        launchApp()
        openGame(1)
        val seats = listOf(R.id.iGameTableSeatEast, R.id.iGameTableSeatSouth, R.id.iGameTableSeatWest, R.id.iGameTableSeatNorth)
        val discarders = listOf(R.id.iDiscarderSelectorPlayer1Container, R.id.iDiscarderSelectorPlayer2Container, R.id.iDiscarderSelectorPlayer3Container)
        val initialNames = game(1).playersNames.toList()
        var hand = 0
        for (seatId in seats) {
            for (discarderId in discarders) {
                hand++
                seat(seatId).safeClick()
                onDialogView(R.id.btHandActionsDialogHu).safeClick()
                val (winnerName, discarderName) = onActivity {
                    val root = ShadowDialog.getLatestDialog().window!!.decorView
                    val winner = root.findViewById<android.view.View>(R.id.iGameHuDialogWinnerContainer).findViewById<TextView>(R.id.tvTableSeatMediumName).text.toString()
                    val discarder = root.findViewById<android.view.View>(discarderId).findViewById<TextView>(R.id.tvTableSeatSmallName).text.toString()
                    winner to discarder
                }
                assertThat(winnerName).isNotEqualTo(discarderName)
                typePoints(R.id.cnpGameHuDialog, 8 + hand)
                onDialogView(discarderId).safeClick()
                onDialogView(R.id.btGameHuDialogOk).safeClick()
                val saved = rounds(1)[hand - 1]
                assertThat(saved.handPoints).isEqualTo(8 + hand)
                assertThat(initialNames[saved.winnerInitialSeat!!.code]).isEqualTo(winnerName)
                assertThat(initialNames[saved.discarderInitialSeat!!.code]).isEqualTo(discarderName)
                selectTab(R.string.table)
            }
        }
        assertThat(rounds(1)).hasSize(13)
        assertThat(game(1).isEnded).isFalse()
    }

    // endregion

    // region Penalties

    @Test
    fun penalties_accumulateOnTheOngoingRound() {
        seed(GameScenarios.newGame)
        launchApp()
        openGame(1)
        // East: 5 not divided.
        seat(R.id.iGameTableSeatEast).safeClick()
        onDialogView(R.id.btHandActionsDialogPenalty).safeClick()
        typePoints(R.id.cnpPenaltyDialog, 5)
        onDialogView(R.id.cbPenaltyDialog).safeClick()
        onDialogView(R.id.btPenaltyDialogSave).safeClick()
        // West: 6 divided (+2 each other player).
        seat(R.id.iGameTableSeatWest).safeClick()
        onDialogView(R.id.btHandActionsDialogPenalty).safeClick()
        typePoints(R.id.cnpPenaltyDialog, 6)
        onDialogView(R.id.btPenaltyDialogSave).safeClick()
        // East again: 3 divided.
        seat(R.id.iGameTableSeatEast).safeClick()
        onDialogView(R.id.btHandActionsDialogPenalty).safeClick()
        typePoints(R.id.cnpPenaltyDialog, 3)
        onDialogView(R.id.btPenaltyDialogSave).safeClick()
        with(ongoingRound(1)) {
            assertThat(listOf(penaltyP1, penaltyP2, penaltyP3, penaltyP4)).containsExactly(-5 + 2 - 3, 2 + 1, -6 + 1, 2 + 1).inOrder()
        }
        seatPoints(R.id.iGameTableSeatEast).check(matches(withText("-6")))
        seatPoints(R.id.iGameTableSeatSouth).check(matches(withText("3")))
        seatPoints(R.id.iGameTableSeatWest).check(matches(withText("-5")))
        seatPoints(R.id.iGameTableSeatNorth).check(matches(withText("3")))

        // The penalties travel with the round once it is finished.
        seat(R.id.iGameTableSeatSouth).safeClick()
        onDialogView(R.id.btHandActionsDialogDraw).safeClick()
        with(rounds(1).first()) {
            assertThat(listOf(penaltyP1, penaltyP2, penaltyP3, penaltyP4)).containsExactly(-6, 3, -5, 3).inOrder()
        }
        assertThat(ongoingRound(1).let { listOf(it.penaltyP1, it.penaltyP2, it.penaltyP3, it.penaltyP4) }).containsExactly(0, 0, 0, 0)
    }

    @Test
    fun penalty_zeroOrEmpty_isRejected_andCancelSavesNothing() {
        seed(GameScenarios.newGame)
        launchApp()
        openGame(1)
        seat(R.id.iGameTableSeatEast).safeClick()
        onDialogView(R.id.btHandActionsDialogPenalty).safeClick()
        onDialogView(R.id.btPenaltyDialogSave).safeClick() // empty
        typePoints(R.id.cnpPenaltyDialog, 0)
        onDialogView(R.id.btPenaltyDialogSave).safeClick() // zero
        onDialogView(R.id.btPenaltyDialogSave).check(matches(isDisplayed()))
        typePoints(R.id.cnpPenaltyDialog, 9)
        onDialogView(R.id.btPenaltyDialogCancel).safeClick()
        assertThat(ongoingRound(1).penaltyP1).isEqualTo(0)
    }

    @Test
    fun cancelPenalties_isOnlyOfferedWhenThereArePenalties_andCanBeAborted() {
        seed(GameScenarios.newGame, GameScenarios.penalties)
        launchApp()
        openGame(1)
        seat(R.id.iGameTableSeatEast).safeClick()
        onDialogView(R.id.btHandActionsDialogPenaltiesCancel).check(matches(withEffectiveVisibility(Visibility.GONE)))
        scenario!!.onActivity { it.findNavController().popBackStack() }
        settle()

        openGame(GameScenarios.penalties.game.gameId)
        val before = ongoingRound(GameScenarios.penalties.game.gameId).columns()
        seat(R.id.iGameTableSeatEast).safeClick()
        onDialogView(R.id.btHandActionsDialogPenaltiesCancel).check(matches(isDisplayed())).safeClick()
        val confirm = ShadowDialog.getLatestDialog() as android.app.AlertDialog
        confirm.getButton(android.content.DialogInterface.BUTTON_NEGATIVE).performClick()
        settle()
        assertThat(ongoingRound(GameScenarios.penalties.game.gameId).columns()).isEqualTo(before)
    }

    // endregion

    // region Hand actions dialog

    @Test
    fun handActions_showsPointsNeededOnlyWhenDiffsAreEnabled() {
        seed(GameScenarios.penalties)
        launchApp()
        openGame(GameScenarios.penalties.game.gameId)
        seat(R.id.iGameTableSeatSouth).safeClick()
        onDialogView(R.id.iHandActionsDialogDiffs).check(matches(isDisplayed()))
        scenario!!.onActivity { it.findNavController().popBackStack() }
        settle()
        clickMenu(R.id.action_disable_diffs_calcs)
        seat(R.id.iGameTableSeatSouth).safeClick()
        onDialogView(R.id.iHandActionsDialogDiffs).check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun handActions_tapOutsideTheButtons_closesWithoutChanges() {
        seed(GameScenarios.penalties)
        launchApp()
        openGame(GameScenarios.penalties.game.gameId)
        val before = roundsColumns(GameScenarios.penalties.game.gameId)
        seat(R.id.iGameTableSeatSouth).safeClick()
        assertThat(currentDestination()).isEqualTo(R.id.handActionsDialogFragment)
        // Tapping the dialog background (the page root, outside every button) closes it.
        scenario!!.onActivity {
            var view: android.view.View = ShadowDialog.getLatestDialog().window!!.decorView.findViewById(R.id.btHandActionsDialogHu)
            while ((view.parent as android.view.View).parent !is RecyclerView) view = view.parent as android.view.View
            shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(1))
            view.performClick()
        }
        settle()
        assertThat(currentDestination()).isEqualTo(R.id.gameFragment)
        assertThat(roundsColumns(GameScenarios.penalties.game.gameId)).isEqualTo(before)
    }

    // endregion

    // region Dice

    @Test
    fun dice_secondThrowerFollowsTheSumOfTheFirstTwoDice() {
        seed(GameScenarios.penalties)
        launchApp()
        openGame(GameScenarios.penalties.game.gameId)
        onView(withId(R.id.fabGameTable)).safeClick()
        assertThat(currentDestination()).isEqualTo(R.id.diceDialogFragment)
        val game = game(GameScenarios.penalties.game.gameId)
        onDialogView(R.id.tvDiceDialogFirst).check(matches(withText(game.getCurrentEastSeatPlayerName())))
        repeat(8) {
            onDialogView(R.id.ivDice1).perform(clickWithoutAdvancingTime())
            Thread.sleep(600) // the app rolls on a java.util.Timer (real time)
            repeat(5) { ShadowLooper.idleMainLooper() }
            val (sum, second) = onActivity {
                val root = ShadowDialog.getLatestDialog().window!!.decorView
                val d1 = root.findViewById<ImageView>(R.id.ivDice1).tag as Int
                val d2 = root.findViewById<ImageView>(R.id.ivDice2).tag as Int
                (d1 + d2) to root.findViewById<TextView>(R.id.tvDiceDialogSecond).text.toString()
            }
            assertThat(sum).isIn(2..12)
            val expected = when (sum) {
                5, 9 -> game.getCurrentEastSeatPlayerName()
                2, 6, 10 -> game.getCurrentSouthSeatPlayerName()
                3, 7, 11 -> game.getCurrentWestSeatPlayerName()
                else -> game.getCurrentNorthSeatPlayerName()
            }
            assertThat(second).isEqualTo(expected)
            onDialogView(R.id.ivDice3).check(matches(isDisplayed()))
            shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(2))
        }
    }

    private fun clickWithoutAdvancingTime() = object : androidx.test.espresso.ViewAction {
        override fun getConstraints() = androidx.test.espresso.matcher.ViewMatchers.isEnabled()
        override fun getDescription() = "performClick() without advancing the clock"
        override fun perform(uiController: androidx.test.espresso.UiController, view: android.view.View) {
            shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(1))
            view.performClick()
        }
    }

    // endregion

    // region Sharing, export and import

    @Test
    fun shareFromOldGames_asText_sendsTheTextExport() {
        seed(GameScenarios.fullGame)
        launchApp()
        onView(withId(R.id.btOldGameItemShare)).safeClick()
        assertThat(latestListDialogItems()).containsExactly("Plain text (only final results)", "CSV table (all game data)", "Importable (all game data)").inOrder()
        chooseInLatestListDialog(0)
        val target = chooserTarget(awaitStartedActivity())
        assertThat(target.action).isEqualTo(Intent.ACTION_SEND)
        assertThat(target.type).isEqualTo("text/plain")
        assertThat(target.getStringExtra(Intent.EXTRA_TEXT)).isEqualTo(Goldens.read("scenarios/${GameScenarios.fullGame.name}/export.txt"))
    }

    @Test
    fun shareFromOldGames_asCsv_sendsTheCsvFile() {
        seed(GameScenarios.fullGame)
        launchApp()
        onView(withId(R.id.btOldGameItemShare)).safeClick()
        chooseInLatestListDialog(1)
        val chooser = awaitStartedActivity()
        assertThat(chooserTarget(chooser).action).isEqualTo(Intent.ACTION_SEND_MULTIPLE)
        val files = sharedFiles(chooser)
        assertThat(files).hasSize(1)
        assertThat(readSharedFile(files.single())).isEqualTo(Goldens.read("scenarios/${GameScenarios.fullGame.name}/export.csv"))
    }

    @Test
    fun shareFromOldGames_asImportable_sendsAJsonThatImportsBack() {
        seed(GameScenarios.fullGame)
        launchApp()
        onView(withId(R.id.btOldGameItemShare)).safeClick()
        chooseInLatestListDialog(2)
        val file = sharedFiles(awaitStartedActivity()).single()
        assertThat(file.lastPathSegment).endsWith(".json")
        assertThat(readSharedFile(file)).contains("Torneo Primavera - Mesa 3")
    }

    @Test
    fun shareDialog_close_sendsNothing() {
        seed(GameScenarios.fullGame)
        launchApp()
        onView(withId(R.id.btOldGameItemShare)).safeClick()
        (ShadowDialog.getLatestDialog() as AlertDialog).getButton(AlertDialog.BUTTON_NEGATIVE).performClick()
        settle()
        assertThat(nextStartedActivity()).isNull()
    }

    @Test
    fun shareFromTheGameMenu_andFromTheRanking() {
        seed(GameScenarios.fullGame)
        launchApp()
        openGame(GameScenarios.fullGame.game.gameId)
        clickMenu(R.id.action_share_game)
        chooseInLatestListDialog(0)
        val expectedText = Goldens.read("scenarios/${GameScenarios.fullGame.name}/export.txt")
        assertThat(chooserTarget(awaitStartedActivity()).getStringExtra(Intent.EXTRA_TEXT)).isEqualTo(expectedText)

        onView(withId(R.id.fabGameTable)).safeClick()
        onDialogView(R.id.btRankingDialogShare).safeClick()
        chooseInLatestListDialog(0)
        assertThat(chooserTarget(awaitStartedActivity()).getStringExtra(Intent.EXTRA_TEXT)).isEqualTo(expectedText)
        onDialogView(R.id.btRankingDialogClose).safeClick()
        assertThat(currentDestination()).isEqualTo(R.id.gameFragment)
    }

    @Test
    fun exportAllGames_thenImportThemIntoAnEmptyApp() {
        seed(*GameScenarios.handWritten.toTypedArray())
        launchApp()
        clickMenu(R.id.action_export_games)
        val files = sharedFiles(awaitStartedActivity())
        assertThat(files).hasSize(1)
        val exported = readSharedFile(files.single())

        // Wipe everything and import the file through the system file picker.
        runBlocking { database.clearAllTables() }
        settle()
        onView(withId(R.id.emptyLayoutOldGames)).check(matches(isDisplayed()))
        val importFile = File(context.cacheDir, "import.json").apply { writeText(exported) }
        clickMenu(R.id.action_import_games)
        val pick = onActivity { shadowOf(it).nextStartedActivityForResult }
        assertThat(pick.intent.action).isEqualTo(Intent.ACTION_OPEN_DOCUMENT)
        assertThat(pick.intent.type).isEqualTo("*/*")
        scenario!!.onActivity { shadowOf(it).receiveResult(pick.intent, Activity.RESULT_OK, Intent().setData(Uri.fromFile(importFile))) }
        settle()
        val imported = allGames()
        assertThat(imported.map { it.gameName }).containsExactlyElementsIn(GameScenarios.handWritten.map { it.game.gameName })
        imported.forEach { dbGame ->
            val original = GameScenarios.handWritten.single { it.game.gameName == dbGame.gameName && it.game.startDate == dbGame.startDate }
            assertThat(game(dbGame.gameId).uiRounds.map { it.totalPointsP1 to it.totalPointsP4 })
                .isEqualTo(original.toUiGame().uiRounds.map { it.totalPointsP1 to it.totalPointsP4 })
        }
    }

    @Test
    fun import_cancelledPicker_changesNothing() {
        seed(GameScenarios.penalties)
        launchApp()
        clickMenu(R.id.action_import_games)
        val pick = onActivity { shadowOf(it).nextStartedActivityForResult }
        scenario!!.onActivity { shadowOf(it).receiveResult(pick.intent, Activity.RESULT_CANCELED, null) }
        settle()
        assertThat(allGames()).hasSize(1)
    }

    @Test
    fun import_invalidFile_showsAnErrorAndImportsNothing() {
        seed(GameScenarios.penalties)
        launchApp()
        val badFile = File(context.cacheDir, "bad.json").apply { writeText("this is not a game") }
        clickMenu(R.id.action_import_games)
        val pick = onActivity { shadowOf(it).nextStartedActivityForResult }
        scenario!!.onActivity { shadowOf(it).receiveResult(pick.intent, Activity.RESULT_OK, Intent().setData(Uri.fromFile(badFile))) }
        settle()
        assertThat(allGames()).hasSize(1)
        val error = ShadowDialog.getLatestDialog() as android.app.AlertDialog
        assertThat(error.isShowing).isTrue()
        assertThat(shadowOf(error).title.toString()).isEqualTo(context.getString(R.string.error))
    }

    // endregion

    // region Old games screen

    @Test
    fun deleteGame_answeringNo_keepsIt() {
        seed(GameScenarios.penalties)
        launchApp()
        onView(withId(R.id.btOldGameItemDelete)).safeClick()
        (ShadowDialog.getLatestDialog() as android.app.AlertDialog).getButton(android.content.DialogInterface.BUTTON_NEGATIVE).performClick()
        settle()
        assertThat(allGames()).hasSize(1)
        assertThat(rounds(GameScenarios.penalties.game.gameId)).isNotEmpty()
    }

    @Test
    fun deleteGame_removesItsRoundsToo() {
        seed(GameScenarios.penalties, GameScenarios.fullGame)
        launchApp()
        onView(allOf(withId(R.id.btOldGameItemDelete), isDescendantOfA(allOf(withId(R.id.cvOldGameItem), androidx.test.espresso.matcher.ViewMatchers.hasDescendant(withText("penalties")))))).safeClick()
        confirmLatestAlertDialog()
        assertThat(allGames().map { it.gameName }).containsExactly(GameScenarios.fullGame.game.gameName)
        assertThat(rounds(GameScenarios.penalties.game.gameId)).isEmpty()
        assertThat(rounds(GameScenarios.fullGame.game.gameId)).hasSize(GameScenarios.fullGame.rounds.size)
    }

    @Test
    fun back_onOldGames_asksToPressAgain_thenCloses() {
        launchApp()
        pressBack()
        onView(withText(R.string.press_again_to_exit)).check(matches(isDisplayed()))
        assertThat(onActivity { it.isFinishing }).isFalse()
        pressBack()
        assertThat(onActivity { it.isFinishing }).isTrue()
    }

    @Test
    fun back_onTheGame_goesFromListToTable_thenToOldGames() {
        seed(GameScenarios.penalties)
        launchApp()
        openGame(GameScenarios.penalties.game.gameId)
        selectTab(R.string.list)
        assertThat(gamePagerItem()).isEqualTo(1)
        pressBack()
        assertThat(gamePagerItem()).isEqualTo(0)
        assertThat(currentDestination()).isEqualTo(R.id.gameFragment)
        pressBack()
        assertThat(currentDestination()).isEqualTo(R.id.oldGamesFragment)
    }

    @Test
    fun changeLanguage_savesItAndRestartsTheApp() {
        launchApp()
        clickMenu(R.id.action_change_language)
        assertThat(latestListDialogItems()).hasSize(6)
        assertThat(latestListDialogCheckedPosition()).isEqualTo(2) // English
        chooseInLatestListDialog(4) // Spanish
        assertThat(runBlocking { languageRepository.get().first() }).isEqualTo("es")
        val restart = awaitStartedActivity()
        assertThat(restart.component!!.className).isEqualTo(MainActivity::class.java.name)
        assertThat(restart.flags and Intent.FLAG_ACTIVITY_CLEAR_TASK).isNotEqualTo(0)
        assertThat(onActivity { it.isFinishing }).isTrue()
    }

    @Test
    fun changeLanguage_eachOptionMapsToItsLanguage() {
        val expected = listOf("zh", "nl", "en", "fr", "es", "it")
        launchApp()
        expected.forEachIndexed { index, language ->
            clickMenu(R.id.action_change_language)
            chooseInLatestListDialog(index)
            assertThat(runBlocking { languageRepository.get().first() }).isEqualTo(language)
            scenario!!.close()
            launchApp()
        }
    }

    @Test
    fun oldGamesCard_reflectsChangesMadeInsideTheGame() {
        seed(GameScenarios.penalties, GameScenarios.fullGame)
        launchApp()
        val id = GameScenarios.penalties.game.gameId
        openGame(id)
        clickMenu(R.id.action_edit_names)
        onDialogView(R.id.tietNamesDialogGameName).perform(replaceText("Renamed table"))
        onDialogView(R.id.tietNamesDialogEast).perform(replaceText("Zoe"))
        onDialogView(R.id.btNamesDialogSave).safeClick()
        seat(R.id.iGameTableSeatEast).safeClick()
        onDialogView(R.id.btHandActionsDialogDraw).safeClick()
        pressBack()
        pressBack()
        assertThat(currentDestination()).isEqualTo(R.id.oldGamesFragment)
        val card = onActivity { activity ->
            val list = activity.findViewById<RecyclerView>(R.id.rvOldGames)
            val cards = (0 until list.childCount).map { ScreenDump.view(list.getChildAt(it)) }
            cards.singleOrNull { "Torneo Primavera" !in it } ?: error(cards.joinToString("\n----\n"))
        }
        assertThat(card).contains("tvOldGameItemGameName: Renamed table")
        assertThat(card).contains("Zoe")
        assertThat(card).contains("tvOldGameItemRoundsNumber: ${rounds(id).size - 1}")
    }

    // endregion

    // region Drawer

    @Test
    fun drawer_externalLinks() {
        launchApp()
        fun select(itemId: Int): Intent {
            openDrawer()
            scenario!!.onActivity { it.binding.navigationView.menu.performIdentifierAction(itemId, 0) }
            settle()
            return awaitStartedActivity()
        }
        with(select(R.id.nav_greenbook_en)) {
            assertThat(action).isEqualTo(Intent.ACTION_VIEW)
            assertThat(data.toString()).isEqualTo("http://mahjong-europe.org/portal/images/docs/mcr_EN.pdf")
        }
        assertThat(select(R.id.nav_greenbook_es).data.toString())
            .isEqualTo("http://mahjong-europe.org/portal/images/docs/GreenBookTranslatedintoSpanishbyIvanMaestreRos.pdf")
        with(select(R.id.nav_mm_web)) {
            assertThat(data.toString()).isEqualTo("https://www.mahjongmadrid.com")
            assertThat(flags and Intent.FLAG_ACTIVITY_NO_HISTORY).isNotEqualTo(0)
        }
        assertThat(select(R.id.nav_ema_web).data.toString()).isEqualTo("http://mahjong-europe.org/")
        with(chooserTarget(select(R.id.nav_contact_mahjong_madrid))) {
            assertThat(action).isEqualTo(Intent.ACTION_SENDTO)
            assertThat(data.toString()).isEqualTo("mailto:")
            assertThat(getStringArrayExtra(Intent.EXTRA_EMAIL)).asList().containsExactly("mahjongmadrid@gmail.com")
            assertThat(getStringExtra(Intent.EXTRA_SUBJECT)).startsWith("Mahjong Scoring ")
        }
        with(chooserTarget(select(R.id.nav_contact_app_support))) {
            assertThat(getStringArrayExtra(Intent.EXTRA_EMAIL)!!.single()).endsWith("@gmail.com")
        }
    }

    @Test
    fun drawer_navigation_andGameEntryNeedsAnActiveGame() {
        seed(GameScenarios.penalties)
        launchApp()
        assertThat(onActivity { it.binding.navigationView.menu.findItem(R.id.gameFragment).isEnabled }).isFalse()
        openDrawer()
        scenario!!.onActivity { it.binding.navigationView.menu.performIdentifierAction(R.id.combinationsFragment, 0) }
        settle()
        assertThat(currentDestination()).isEqualTo(R.id.combinationsFragment)
        openDrawer()
        scenario!!.onActivity { it.binding.navigationView.menu.performIdentifierAction(R.id.oldGamesFragment, 0) }
        settle()
        assertThat(currentDestination()).isEqualTo(R.id.oldGamesFragment)
        openGame(GameScenarios.penalties.game.gameId)
        assertThat(onActivity { it.binding.navigationView.menu.findItem(R.id.gameFragment).isEnabled }).isTrue()
        openDrawer()
        scenario!!.onActivity { it.binding.navigationView.menu.performIdentifierAction(R.id.combinationsFragment, 0) }
        settle()
        assertThat(currentDestination()).isEqualTo(R.id.combinationsFragment)
        openDrawer()
        scenario!!.onActivity { it.binding.navigationView.menu.performIdentifierAction(R.id.gameFragment, 0) }
        settle()
        assertThat(currentDestination()).isEqualTo(R.id.gameFragment)
    }

    // endregion

    // region Game menu

    @Test
    fun gameMenu_endAndResumeVisibility() {
        seed(GameScenarios.newGame, GameScenarios.selfPicks, GameScenarios.allDiscardPairs, GameScenarios.fullGame, GameScenarios.sixteenDraws)
        launchApp()
        fun visible() = Triple(menuItem(R.id.action_end_game).isVisible, menuItem(R.id.action_resume_game).isVisible, menuItem(R.id.action_share_game).isVisible)

        openGame(GameScenarios.newGame.game.gameId) // only the ongoing round
        assertThat(visible()).isEqualTo(Triple(false, false, true))
        pressBack()
        openGame(GameScenarios.selfPicks.game.gameId) // ongoing with hands
        assertThat(visible()).isEqualTo(Triple(true, false, true))
        pressBack()
        openGame(GameScenarios.allDiscardPairs.game.gameId) // ended before 16 hands
        assertThat(visible()).isEqualTo(Triple(false, true, true))
        pressBack()
        openGame(GameScenarios.fullGame.game.gameId) // ended after 16 hands: cannot resume
        assertThat(visible()).isEqualTo(Triple(false, false, true))
        pressBack()
        openGame(GameScenarios.sixteenDraws.game.gameId) // ended after 16 hands: cannot resume
        assertThat(visible()).isEqualTo(Triple(false, false, true))
    }

    @Test
    fun gameMenu_resumeFromTheMenu_reopensTheGame() {
        seed(GameScenarios.allDiscardPairs)
        launchApp()
        openGame(GameScenarios.allDiscardPairs.game.gameId)
        clickMenu(R.id.action_resume_game)
        assertThat(game(GameScenarios.allDiscardPairs.game.gameId).isEnded).isFalse()
        assertThat(menuItem(R.id.action_end_game).isVisible).isTrue()
        onView(withId(R.id.fabGameTable)).safeClick()
        assertThat(currentDestination()).isEqualTo(R.id.diceDialogFragment) // dice again, not the ranking
    }

    @Test
    fun gameMenu_endGameTwice_rankingIsShownOnce() {
        seed(GameScenarios.selfPicks)
        launchApp()
        openGame(GameScenarios.selfPicks.game.gameId)
        clickMenu(R.id.action_end_game)
        onView(withId(R.id.fabGameTable)).safeClick()
        onView(withId(R.id.fabGameTable)).perform(clickWithoutAdvancingTime())
        settle()
        assertThat(currentDestination()).isEqualTo(R.id.rankingDialogFragment)
        scenario!!.onActivity { it.findNavController().popBackStack() }
        settle()
        assertThat(currentDestination()).isEqualTo(R.id.gameFragment)
    }

    @Test
    fun seatsOrientation_fromTheListPage_showsTheTable() {
        seed(GameScenarios.penalties)
        launchApp()
        openGame(GameScenarios.penalties.game.gameId)
        selectTab(R.string.list)
        assertThat(gamePagerItem()).isEqualTo(1)
        clickMenu(R.id.action_seats_orientation)
        assertThat(gamePagerItem()).isEqualTo(0)
    }

    @Test
    fun editNames_blankFieldsFallBackToDefaults_andCancelKeepsNames() {
        seed(GameScenarios.penalties)
        launchApp()
        val id = GameScenarios.penalties.game.gameId
        openGame(id)
        val original = game(id).playersNames.toList()
        clickMenu(R.id.action_edit_names)
        onDialogView(R.id.tietNamesDialogEast).perform(replaceText("Changed"))
        onDialogView(R.id.btNamesDialogCancel).safeClick()
        assertThat(game(id).playersNames.toList()).isEqualTo(original)

        clickMenu(R.id.action_edit_names)
        onDialogView(R.id.tietNamesDialogGameName).perform(replaceText("   "))
        onDialogView(R.id.tietNamesDialogEast).perform(replaceText(""))
        onDialogView(R.id.tietNamesDialogSouth).perform(replaceText("  "))
        onDialogView(R.id.tietNamesDialogWest).perform(replaceText("  Ana   María  ")) // trimmed, inner spaces kept
        onDialogView(R.id.btNamesDialogSave).safeClick()
        with(game(id)) {
            assertThat(playersNames.toList()).containsExactly(
                context.getString(R.string.player_one), context.getString(R.string.player_two), "Ana   María", original[3],
            ).inOrder()
            assertThat(gameName).matches("""\d{2}/\d{2}/\d{4} \d{2}:\d{2}""") // blank name: the start date
        }
        onView(withId(R.id.tvGameTableGameName)).check(matches(withText(game(id).gameName)))
    }

    // endregion

    // region Combinations and diffs calculator

    @Test
    fun combinations_searchWithoutResults_showsTheEmptyState_andClearingRestoresAll() {
        launchApp()
        navigateTo(R.id.combinationsFragment)
        val list = onActivity { it.findViewById<RecyclerView>(R.id.recyclerViewCombinations) }
        assertThat(list.adapter!!.itemCount).isEqualTo(81)
        searchCombination("zzzz")
        onView(withId(R.id.emptyLayoutCombinations)).check(matches(isDisplayed()))
        assertThat(list.adapter!!.itemCount).isEqualTo(0)
        searchCombination("")
        onView(withId(R.id.emptyLayoutCombinations)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        assertThat(list.adapter!!.itemCount).isEqualTo(81)
        searchCombination("88")
        assertThat(list.adapter!!.itemCount).isAtLeast(1)
    }

    private fun searchCombination(text: String) {
        scenario!!.onActivity { activity ->
            val item = activity.binding.toolbar.menu.findItem(R.id.action_search_combination)
            item.expandActionView()
            (item.actionView as androidx.appcompat.widget.SearchView).setQuery(text, false)
        }
        settle()
    }

    @Test
    fun combinations_toggleImageOrDescription() {
        launchApp()
        navigateTo(R.id.combinationsFragment)
        fun firstItem(): String = onActivity { activity ->
            val list = activity.findViewById<RecyclerView>(R.id.recyclerViewCombinations)
            list.getChildAt(0).let { item ->
                fun describe(view: android.view.View): String =
                    "${view.javaClass.simpleName}:${view.visibility}" +
                        ((view as? android.view.ViewGroup)?.let { g -> (0 until g.childCount).joinToString(",", "[", "]") { describe(g.getChildAt(it)) } } ?: "")
                ScreenDump.view(item) + describe(item)
            }
        }
        val before = firstItem()
        clickMenu(R.id.action_toggle_combination_explanation)
        val after = firstItem()
        assertThat(after).isNotEqualTo(before)
        clickMenu(R.id.action_toggle_combination_explanation)
        assertThat(firstItem()).isEqualTo(before)
    }

    @Test
    fun diffsCalculator_loadsMoreRowsWhileScrolling_untilTheMaximum() {
        launchApp()
        navigateTo(R.id.diffsCalculatorFragment)
        val list = onActivity { it.findViewById<RecyclerView>(R.id.recyclerViewDiffsCalculator) }
        val initial = list.adapter!!.itemCount
        repeat(60) {
            scenario!!.onActivity { list.scrollBy(0, 5000) }
            settle(1)
        }
        val loaded = list.adapter!!.itemCount
        assertThat(loaded).isGreaterThan(initial)
        assertThat(loaded).isAtMost(com.etologic.mahjongscoring2.app.screens.diffs_calculator.DiffsCalculatorViewModel.MAX_ITEMS)
    }

    // endregion

    // region Games without rounds

    /** Regression: a game without rounds used to keep the whole Old games list from loading and updating. */
    @Test
    fun oldGames_withAGameWithoutRounds_listsTheOtherGamesAndKeepsUpdating() {
        seed(GameScenarios.penalties)
        runBlocking { database.gamesDao.insertOne(GameScenarios.newGame.game.copy(gameId = 50, gameName = "no rounds")) }
        launchApp()
        assertThat(oldGamesNames()).containsExactly("penalties")
        seed(GameScenarios.fullGame)
        settle()
        assertThat(oldGamesNames()).containsExactly("penalties", GameScenarios.fullGame.game.gameName)
    }

    private fun oldGamesNames(): List<String> = onActivity { activity ->
        val list = activity.findViewById<RecyclerView>(R.id.rvOldGames)
        (0 until list.childCount).map {
            list.getChildAt(it).findViewById<android.widget.TextView>(R.id.tvOldGameItemGameName).text.toString()
        }
    }

    // endregion

    // region Activity recreation (rotation, process death with saved state)

    @Test
    fun recreate_keepsTheOpenGame() {
        seed(GameScenarios.penalties, GameScenarios.fullGame)
        launchApp()
        openGame(GameScenarios.penalties.game.gameId)
        scenario!!.recreate()
        settle()
        assertThat(currentDestination()).isEqualTo(R.id.gameFragment)
        onView(withId(R.id.tvGameTableGameName)).check(matches(withText("penalties")))
    }

    /** Recreating the activity with the hand actions dialog open keeps the dialog open for the same seat. */
    @Test
    fun recreate_withTheHandActionsDialogOpen_keepsItOpen() {
        seed(GameScenarios.penalties)
        launchApp()
        openGame(GameScenarios.penalties.game.gameId)
        val before = roundsColumns(GameScenarios.penalties.game.gameId)
        seat(R.id.iGameTableSeatWest).safeClick()
        assertThat(currentDestination()).isEqualTo(R.id.handActionsDialogFragment)
        scenario!!.recreate()
        settle()
        assertThat(currentDestination()).isEqualTo(R.id.handActionsDialogFragment)
        assertThat(roundsColumns(GameScenarios.penalties.game.gameId)).isEqualTo(before)
        // The restored dialog still works for the selected seat.
        onDialogView(R.id.btHandActionsDialogDraw).safeClick()
        assertThat(currentDestination()).isEqualTo(R.id.gameFragment)
        assertThat(rounds(GameScenarios.penalties.game.gameId)).hasSize(before.size + 1)
    }

    /** Cancelling the dialog after a recreation still clears the selection and leaves the table usable. */
    @Test
    fun recreate_withTheHandActionsDialogOpen_thenBack_closesIt() {
        seed(GameScenarios.penalties)
        launchApp()
        openGame(GameScenarios.penalties.game.gameId)
        seat(R.id.iGameTableSeatWest).safeClick()
        scenario!!.recreate()
        settle()
        pressBack()
        assertThat(currentDestination()).isEqualTo(R.id.gameFragment)
        seat(R.id.iGameTableSeatEast).safeClick()
        assertThat(currentDestination()).isEqualTo(R.id.handActionsDialogFragment)
    }

    // endregion

    private fun MainActivity.findNavController() = androidx.navigation.Navigation.findNavController(this, R.id.fragmentContainerView)

    @Suppress("unused")
    private val unusedReference = UiGame.MAX_MCR_ROUNDS
}
