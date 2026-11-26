package com.etologic.mahjongscoring2.ui

import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.viewpager2.widget.ViewPager2
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.business.model.enums.SeatsArrangement
import com.etologic.mahjongscoring2.testutil.GameScenario
import com.etologic.mahjongscoring2.testutil.GameScenarios
import com.etologic.mahjongscoring2.testutil.Goldens
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.allOf
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.shadows.ShadowLooper

/**
 * Text goldens of what every screen shows for every scenario: golden/screens/<scenario>.txt.
 *
 * For each game: the table in the three seat arrangements, every round of the list with its footer, the hand
 * actions dialog of every seat (with the "points needed" table) and, for ended games, the ranking dialog.
 * Plus the old games list and the combinations/diffs calculator lists.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ScreenTextGoldenTest : UiTestBase() {

    private val seatIds = listOf(
        "EAST" to R.id.iGameTableSeatEast,
        "SOUTH" to R.id.iGameTableSeatSouth,
        "WEST" to R.id.iGameTableSeatWest,
        "NORTH" to R.id.iGameTableSeatNorth,
    )

    private fun quickSettle() {
        repeat(3) { ShadowLooper.idleMainLooper() }
    }

    private fun recycler(id: Int): RecyclerView {
        var rv: RecyclerView? = null
        scenario!!.onActivity { rv = it.findViewById(id) }
        return rv!!
    }

    private fun showGamePage(index: Int) {
        scenario!!.onActivity { it.findViewById<ViewPager2>(R.id.viewPagerGame).setCurrentItem(index, false) }
        settle()
    }

    private fun backToOldGames() {
        scenario!!.onActivity { it.findViewById<android.view.View>(R.id.fragmentContainerView).findNavController().popBackStack(R.id.oldGamesFragment, false) }
        settle()
    }

    private fun closeTopDialog() {
        scenario!!.onActivity { it.findNavController(R.id.fragmentContainerView).popBackStack() }
        settle()
    }

    private fun dumpGame(scenario: GameScenario, withDialogs: Boolean): String = buildString {
        appendLine("# ${scenario.name}: ${scenario.description}")
        openGame(scenario.game.gameId)
        for (arrangement in SeatsArrangement.entries) {
            setSeatsArrangement(arrangement)
            settle()
            appendLine()
            appendLine("## Table, seats $arrangement")
            append(ScreenDump.allWindows())
        }
        setSeatsArrangement(SeatsArrangement.DOWN)
        settle()

        if (withDialogs && !scenario.toUiGame().isEnded) {
            for ((seatName, seatId) in seatIds) {
                onView(allOf(withId(R.id.tvTableSeatBySeatName), isDescendantOfA(withId(seatId)))).safeClick()
                appendLine()
                appendLine("## Hand actions dialog, seat $seatName")
                append(ScreenDump.topWindow())
                closeTopDialog()
            }
        }
        if (scenario.toUiGame().isEnded) {
            navigateTo(R.id.rankingDialogFragment)
            appendLine()
            appendLine("## Ranking dialog")
            append(ScreenDump.topWindow())
            closeTopDialog()
        }

        showGamePage(1)
        appendLine()
        appendLine("## List")
        append(ScreenDump.recyclerItems(recycler(R.id.rvGameList)) { quickSettle() })
        appendLine("## List page (header and footers)")
        var page = ""
        this@ScreenTextGoldenTest.scenario!!.onActivity { page = ScreenDump.view(it.findViewById<android.view.View>(R.id.rvGameList).parent as android.view.View) }
        append(page)
        backToOldGames()
    }

    private fun checkGames(games: List<GameScenario>, withDialogs: Boolean) {
        seed(*games.toTypedArray())
        launchApp()
        val failures = mutableListOf<String>()
        games.forEach { game ->
            runCatching { Goldens.assertMatches("screens/${game.name}.txt", dumpGame(game, withDialogs)) }
                .onFailure { failures += "${game.name}: ${it.message}" }
        }
        check(failures.isEmpty()) { failures.joinToString("\n\n") }
    }

    @Test
    fun handWrittenGames() = checkGames(GameScenarios.handWritten, withDialogs = true)

    @Test
    fun randomGames() = checkGames(GameScenarios.randomGames, withDialogs = true)

    /** Table after 0..15 hands: round number, wind and the corner where the dice button sits. */
    @Test
    fun tableAlongAGame() {
        seed(*GameScenarios.progression.toTypedArray())
        launchApp()
        val dump = buildString {
            GameScenarios.progression.forEach { game ->
                openGame(game.game.gameId)
                appendLine("## ${game.name}")
                var fab = ""
                this@ScreenTextGoldenTest.scenario!!.onActivity {
                    val params = it.findViewById<android.view.View>(R.id.fabGameTable).layoutParams as android.widget.RelativeLayout.LayoutParams
                    val vertical = if (params.getRule(android.widget.RelativeLayout.ALIGN_PARENT_BOTTOM) != 0) "bottom" else "top"
                    val atEnd = params.getRule(android.widget.RelativeLayout.ALIGN_PARENT_END) != 0 ||
                        params.getRule(android.widget.RelativeLayout.ALIGN_PARENT_RIGHT) != 0
                    val horizontal = if (atEnd) "end" else "start"
                    fab = "dice button: $vertical-$horizontal"
                }
                appendLine(fab)
                append(ScreenDump.allWindows())
                backToOldGames()
            }
        }
        Goldens.assertMatches("screens/table_along_a_game.txt", dump)
    }

    @Test
    fun oldGamesList() {
        seed(*GameScenarios.all.toTypedArray())
        launchApp()
        Goldens.assertMatches("screens/old_games_list.txt", ScreenDump.recyclerItems(recycler(R.id.rvOldGames)) { quickSettle() })
    }

    @Test
    fun combinationsList() {
        launchApp()
        navigateTo(R.id.combinationsFragment)
        Goldens.assertMatches("screens/combinations_list.txt", ScreenDump.recyclerItems(recycler(R.id.recyclerViewCombinations)) { quickSettle() })
    }

    @Test
    fun diffsCalculatorList() {
        launchApp()
        navigateTo(R.id.diffsCalculatorFragment)
        Goldens.assertMatches("screens/diffs_calculator_list.txt", ScreenDump.recyclerItems(recycler(R.id.recyclerViewDiffsCalculator)) { quickSettle() })
    }
}
