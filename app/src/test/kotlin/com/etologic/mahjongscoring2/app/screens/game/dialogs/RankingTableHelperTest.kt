package com.etologic.mahjongscoring2.app.screens.game.dialogs

import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.model.entities.UiRound
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.Date

/** Table points (4/2/1/0) and how ties split them, as used by the ranking dialog and text export. */
class RankingTableHelperTest {

    private fun gameWithTotals(p1: Int, p2: Int, p3: Int, p4: Int, vararg extraRounds: UiRound) = UiGame(
        gameId = 1, nameP1 = "A", nameP2 = "B", nameP3 = "C", nameP4 = "D",
        startDate = Date(0), endDate = null, gameName = "g",
        uiRounds = extraRounds.toList() + UiRound(1, 99, null, null, 0, p1, p2, p3, p4),
    )

    @ParameterizedTest(name = "{0}")
    @MethodSource("cases")
    fun `table points`(case: String, scores: List<Int>, expectedNames: List<String>, expectedPoints: List<String>) {
        val data = RankingTableHelper.generateRankingTable(gameWithTotals(scores[0], scores[1], scores[2], scores[3]))!!
        assertThat(data.sortedPlayersRankings.map { it.name }).isEqualTo(expectedNames)
        assertThat(data.sortedPlayersRankings.map { it.points }).isEqualTo(expectedPoints)
        assertThat(data.sortedPlayersRankings.map { it.score }).isEqualTo(scores.sortedDescending())
    }

    @Test
    fun `null game gives null`() {
        assertThat(RankingTableHelper.generateRankingTable(null)).isNull()
    }

    @Test
    fun `best hand and rounds`() {
        val noWinner = RankingTableHelper.generateRankingTable(gameWithTotals(0, 0, 0, 0))!!
        assertThat(noWinner.bestHandPlayerPoints).isEqualTo("-")
        assertThat(noWinner.bestHandPlayerName).isEqualTo("-")
        assertThat(noWinner.numRounds).isEqualTo(1)
        assertThat(noWinner.sNumRounds).isEqualTo("1")

        val withWinner = RankingTableHelper.generateRankingTable(
            gameWithTotals(0, 0, 0, 0, UiRound(1, 1, TableWinds.WEST, TableWinds.EAST, 30, 0, 0, 0, 0))
        )!!
        assertThat(withWinner.bestHandPlayerPoints).isEqualTo("30")
        assertThat(withWinner.bestHandPlayerName).isEqualTo("C")
        assertThat(withWinner.numRounds).isEqualTo(2)
    }

    companion object {
        @JvmStatic
        fun cases(): List<Arguments> = listOf(
            Arguments.of("no ties", listOf(10, 30, -20, -20 + 1), listOf("B", "A", "D", "C"), listOf("4", "2", "1", "0")),
            Arguments.of("all tied", listOf(0, 0, 0, 0), listOf("A", "B", "C", "D"), listOf("1.75", "1.75", "1.75", "1.75")),
            Arguments.of("first three tied", listOf(5, 5, -15, 5), listOf("A", "B", "D", "C"), listOf("2.33", "2.33", "2.33", "0")),
            Arguments.of("last three tied", listOf(-5, 15, -5, -5), listOf("B", "A", "C", "D"), listOf("4", "1", "1", "1")),
            Arguments.of("first two tied", listOf(10, 10, 0, -20), listOf("A", "B", "C", "D"), listOf("3", "3", "1", "0")),
            Arguments.of("second two tied", listOf(20, 0, 0, -20), listOf("A", "B", "C", "D"), listOf("4", "1.5", "1.5", "0")),
            Arguments.of("last two tied", listOf(20, 10, -15, -15), listOf("A", "B", "C", "D"), listOf("4", "2", "0.5", "0.5")),
            Arguments.of("two pairs", listOf(10, -10, 10, -10), listOf("A", "C", "B", "D"), listOf("3", "3", "0.5", "0.5")),
        )
    }
}
