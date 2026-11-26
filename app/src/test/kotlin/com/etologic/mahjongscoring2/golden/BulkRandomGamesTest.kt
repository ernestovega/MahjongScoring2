package com.etologic.mahjongscoring2.golden

import com.etologic.mahjongscoring2.app.screens.game.dialogs.RankingTableHelper
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.use_cases.DeleteGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.ExportGameToJsonUseCase
import com.etologic.mahjongscoring2.business.use_cases.GetOneGameFlowUseCase
import com.etologic.mahjongscoring2.business.use_cases.ImportGamesFromJsonUseCase
import com.etologic.mahjongscoring2.testutil.FakeGamesRepository
import com.etologic.mahjongscoring2.testutil.FakeRoundsRepository
import com.etologic.mahjongscoring2.testutil.GameReport
import com.etologic.mahjongscoring2.testutil.GameScenarios
import com.etologic.mahjongscoring2.testutil.Goldens
import com.etologic.mahjongscoring2.testutil.clone
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

/**
 * Hundreds of seeded random games (GameScenarios.randomGame), far more than the hand written scenarios:
 *
 *  - golden/bulk/random_games_*.txt: one compact line per round (winner, discarder, hand, penalties, round
 *    points and running totals) plus the final ranking and best hand. A KMP port must reproduce them exactly.
 *  - Invariants checked on 3000 games: every hand is zero sum, totals are the running sums, the ranking is
 *    sorted, and a JSON export imports back to the very same game.
 */
class BulkRandomGamesTest {

    private val goldenGames = (1..400).map { GameScenarios.randomGame(1000 + it, 10_000L + it) }

    private fun line(game: UiGame): String = buildString {
        appendLine("game ${game.gameId} ended=${game.isEnded} players=${game.playersNames.joinToString("|")}")
        game.uiRounds.forEach { r ->
            append("  r${r.roundNumber} ")
            append("w=${r.winnerInitialSeat?.name ?: "-"} d=${r.discarderInitialSeat?.name ?: "-"} h=${r.handPoints} ")
            append("pen=${r.penaltyP1},${r.penaltyP2},${r.penaltyP3},${r.penaltyP4} ")
            append("pts=${r.pointsP1},${r.pointsP2},${r.pointsP3},${r.pointsP4} ")
            appendLine("tot=${r.totalPointsP1},${r.totalPointsP2},${r.totalPointsP3},${r.totalPointsP4}")
        }
        val ranking = RankingTableHelper.generateRankingTable(game)
        appendLine("  ranking=${ranking?.sortedPlayersRankings?.joinToString { "${it.name}:${it.points}:${it.score}" }}")
        val best = game.getBestHand()
        appendLine("  best=${best.playerInitialPosition.name}:${best.handValue}")
    }

    @Test
    fun `bulk golden`() {
        goldenGames.chunked(100).forEachIndexed { chunk, games ->
            val from = chunk * 100 + 1
            Goldens.assertMatches(
                "bulk/random_games_%03d-%03d.txt".format(from, from + games.size - 1),
                games.joinToString("") { line(it.toUiGame()) },
            )
        }
    }

    @Test
    fun `invariants on thousands of games`() {
        (1..3000).map { GameScenarios.randomGame(5000 + it, 50_000L + it) }.forEach { scenario ->
            val game = scenario.toUiGame()
            var running = IntArray(4)
            game.uiRounds.forEach { r ->
                val handPoints = listOf(r.pointsP1, r.pointsP2, r.pointsP3, r.pointsP4)
                val penalties = listOf(r.penaltyP1, r.penaltyP2, r.penaltyP3, r.penaltyP4)
                // Round points = hand result + penalties, and the hand result itself is zero sum.
                val handOnly = handPoints.zip(penalties) { p, pen -> p - pen }
                assertThat(handOnly.sum()).isEqualTo(0)
                if (r.winnerInitialSeat == null || r.winnerInitialSeat == TableWinds.NONE) {
                    assertThat(handOnly).containsExactly(0, 0, 0, 0)
                } else {
                    assertThat(handOnly[r.winnerInitialSeat!!.code]).isGreaterThan(0)
                }
                running = IntArray(4) { running[it] + handPoints[it] }
                assertThat(listOf(r.totalPointsP1, r.totalPointsP2, r.totalPointsP3, r.totalPointsP4)).isEqualTo(running.toList())
            }
            assertThat(game.uiRounds.size).isAtMost(UiGame.MAX_MCR_ROUNDS)
            RankingTableHelper.generateRankingTable(game)?.let { ranking ->
                val scores = ranking.sortedPlayersRankings.map { it.score }
                assertThat(scores).isInOrder(Comparator.reverseOrder<Int>())
            }
        }
    }

    @Test
    fun `json export imports back identically`() {
        (1..300).map { GameScenarios.randomGame(9000 + it, 90_000L + it) }.forEach { scenario ->
            val source = FakeGamesRepository() to FakeRoundsRepository()
            runBlocking {
                source.first.insertOne(scenario.game).getOrThrow()
                scenario.rounds.forEach { source.second.insertOne(it.clone()).getOrThrow() }
            }
            val getOne = GetOneGameFlowUseCase(source.first, source.second)
            val original = runBlocking { getOne(scenario.game.gameId).first() }
            val json = ExportGameToJsonUseCase(getOne).jsonFrom(original)

            val games = FakeGamesRepository()
            val rounds = FakeRoundsRepository()
            val ids = runBlocking { ImportGamesFromJsonUseCase(games, rounds, DeleteGameUseCase(games, rounds)).createGamesInDbFrom(json) }
            val imported = runBlocking { GetOneGameFlowUseCase(games, rounds)(ids.single()).first() }
            assertThat(GameReport.render(imported)).isEqualTo(GameReport.render(original))
        }
    }
}
