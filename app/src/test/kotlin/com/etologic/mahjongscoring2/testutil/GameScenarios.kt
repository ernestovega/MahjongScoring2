package com.etologic.mahjongscoring2.testutil

import com.etologic.mahjongscoring2.business.model.entities.GameId
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.EAST
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NONE
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NORTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.SOUTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.WEST
import com.etologic.mahjongscoring2.business.use_cases.mappers.toUiGame
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbGame
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbRound
import java.util.Date
import java.util.Random

/**
 * Deterministic game scenarios used by the golden tests, the use case tests and the screenshot tests.
 *
 * Only *inputs* are described here (who won, who discarded, hand points and penalties). Every derived
 * value (round points, totals, best hand, ranking, diffs, exports) is computed by the production code and
 * pinned in golden files. Each scenario is also written as `golden/scenarios/<name>.input.json` using the
 * app's own portable import/export format, so another implementation can load exactly the same inputs.
 */
data class GameScenario(
    val name: String,
    val description: String,
    val game: DbGame,
    val rounds: List<DbRound>,
) {
    fun toUiGame(): UiGame = game.toUiGame(rounds)
}

class ScenarioBuilder(private val gameId: GameId) {
    private val rounds = mutableListOf<DbRound>()
    private var nextRoundId = gameId * 100 + 1

    private fun add(winner: TableWinds?, discarder: TableWinds?, points: Int, penalties: IntArray) {
        rounds += DbRound(
            gameId = gameId,
            roundId = nextRoundId++,
            winnerInitialSeat = winner,
            discarderInitialSeat = discarder,
            handPoints = points,
            penaltyP1 = penalties[0],
            penaltyP2 = penalties[1],
            penaltyP3 = penalties[2],
            penaltyP4 = penalties[3],
        )
    }

    fun discard(winner: TableWinds, discarder: TableWinds, points: Int, vararg penalties: Int) =
        add(winner, discarder, points, penalties.orZeros())

    fun selfPick(winner: TableWinds, points: Int, vararg penalties: Int) =
        add(winner, NONE, points, penalties.orZeros())

    fun draw(vararg penalties: Int) = add(NONE, NONE, 0, penalties.orZeros())

    fun ongoing(vararg penalties: Int) = add(null, null, 0, penalties.orZeros())

    fun build(): List<DbRound> = rounds.toList()

    private fun IntArray.orZeros(): IntArray = if (isEmpty()) IntArray(4) else also { require(size == 4) }
}

object GameScenarios {

    /** 2025-01-15T10:00:00Z. All scenario dates are fixed so outputs never depend on "now". */
    const val BASE_START_MILLIS = 1_736_935_200_000L
    private const val HOUR = 3_600_000L

    private fun game(
        id: GameId,
        name: String,
        players: List<String> = listOf("Ana", "Bruno", "Carla", "David"),
        ended: Boolean,
        startOffsetHours: Long = id,
    ): DbGame = DbGame(
        gameId = id,
        nameP1 = players[0],
        nameP2 = players[1],
        nameP3 = players[2],
        nameP4 = players[3],
        startDate = Date(BASE_START_MILLIS + startOffsetHours * HOUR),
        endDate = if (ended) Date(BASE_START_MILLIS + startOffsetHours * HOUR + 2 * HOUR) else null,
        gameName = name,
    )

    private fun scenario(
        id: GameId,
        name: String,
        description: String,
        gameName: String = name,
        players: List<String> = listOf("Ana", "Bruno", "Carla", "David"),
        ended: Boolean = false,
        rounds: ScenarioBuilder.() -> Unit,
    ) = GameScenario(
        name = name,
        description = description,
        game = game(id, gameName, players, ended),
        rounds = ScenarioBuilder(id).apply(rounds).build(),
    )

    val newGame = scenario(1, "new_game", "Freshly created game: one ongoing round, nothing scored.") {
        ongoing()
    }

    val selfPicks = scenario(2, "self_picks", "Each initial seat wins once by self pick, then an ongoing round.") {
        selfPick(EAST, 8)
        selfPick(SOUTH, 16)
        selfPick(WEST, 24)
        selfPick(NORTH, 88)
        ongoing()
    }

    val allDiscardPairs = scenario(
        3, "all_discard_pairs",
        "The 12 possible winner/discarder combinations with growing hand values. Ends the game at 12 rounds.",
        ended = true,
    ) {
        var points = 8
        for (winner in TableWinds.asArray) for (discarder in TableWinds.asArray) {
            if (winner != discarder) {
                discard(winner, discarder, points)
                points += 3
            }
        }
    }

    val penalties = scenario(4, "penalties", "Divided and non divided penalties mixed with hands, draws and an ongoing round.") {
        discard(EAST, SOUTH, 10, -30, 10, 10, 10)
        selfPick(WEST, 12, 0, -20, 0, 0)
        draw(5, 5, -15, 5)
        discard(NORTH, EAST, 32, -60, 0, 0, 0)
        ongoing(0, 0, 0, -10)
    }

    val sixteenDraws = scenario(5, "sixteen_draws", "A complete game where every hand is a draw.", ended = true) {
        repeat(16) { draw() }
    }

    val fullGame = scenario(
        6, "full_game",
        "A realistic complete 16 round tournament game with every kind of round.",
        gameName = "Torneo Primavera - Mesa 3",
        players = listOf("Ernesto", "Lucía", "Wang Wei", "Ana María"),
        ended = true,
    ) {
        discard(EAST, NORTH, 12)
        selfPick(SOUTH, 9)
        draw()
        discard(WEST, SOUTH, 24)
        discard(NORTH, WEST, 8, 0, 0, 0, -10)
        selfPick(EAST, 32)
        discard(SOUTH, EAST, 16)
        draw(-30, 10, 10, 10)
        discard(WEST, NORTH, 64)
        selfPick(NORTH, 12)
        discard(EAST, WEST, 8)
        discard(SOUTH, NORTH, 40)
        selfPick(WEST, 8)
        discard(NORTH, SOUTH, 13)
        draw()
        discard(EAST, SOUTH, 88)
    }

    // Ranking ties: names are chosen so that the tie-breaking order (stable sort) is visible.
    val tieAll4 = scenario(7, "tie_all_four", "All four players end with the same score.") {
        ongoing()
    }

    val tieFirst3 = scenario(8, "tie_first_three", "Three players tie for first place (penalties only).") {
        ongoing(10, 10, 10, -30)
    }

    val tieLast3 = scenario(9, "tie_last_three", "Three players tie for last place.") {
        selfPick(SOUTH, 8)
    }

    val tieFirst2 = scenario(10, "tie_first_two", "Two players tie for first place, the rest are different.") {
        discard(EAST, WEST, 8)
        discard(SOUTH, NORTH, 8)
        ongoing(0, 0, -2, 2)
    }

    val tieSecond2 = scenario(11, "tie_second_two", "Second and third places are tied.") {
        discard(EAST, NORTH, 20)
        ongoing()
    }

    val tieLast2 = scenario(12, "tie_last_two", "Third and fourth places are tied.") {
        discard(EAST, NORTH, 20)
        discard(SOUTH, WEST, 8)
        ongoing(0, 0, -6, 6)
    }

    val tiePairs = scenario(13, "tie_two_pairs", "First two tied and last two tied.") {
        discard(EAST, WEST, 8)
        discard(SOUTH, NORTH, 8)
        ongoing()
    }

    val specialNames = scenario(
        14, "special_names",
        "Names with quotes, commas, accents, emoji and file-name-unsafe characters.",
        gameName = "Final: \"Copa\" <Madrid>/2025?",
        players = listOf("  \"Pepe\"  ", "José, Luis", "王伟", "Zoë 🀄"),
    ) {
        discard(SOUTH, WEST, 25)
        selfPick(NORTH, 10, 0, 0, -15, 0)
        ongoing()
    }

    val emptyGameName = scenario(
        15, "empty_game_name", "Game without a name: exports fall back to the start date.", gameName = "",
    ) {
        selfPick(WEST, 30)
        ongoing()
    }

    val bestHandTie = scenario(16, "best_hand_tie", "Two hands share the highest value: the first one is the best hand.") {
        discard(SOUTH, EAST, 48)
        discard(WEST, EAST, 48)
        discard(NORTH, EAST, 20)
        ongoing()
    }

    val penaltiesOnly = scenario(17, "penalties_only", "No hand won yet, only penalties on the ongoing round.") {
        ongoing(-40, 0, 0, 0)
    }

    val bigScores = scenario(18, "big_scores", "Very large hands to check sign handling and table diffs with big numbers.") {
        selfPick(NORTH, 888)
        discard(EAST, NORTH, 500)
        discard(WEST, SOUTH, 9999)
        ongoing()
    }

    /** Pseudo-random but fully deterministic games, covering lengths from 1 to 16 rounds. */
    val randomGames: List<GameScenario> = (1..20).map { index -> randomGame(index, 100L + index) }

    /**
     * Seeded random game number [index] (same seed, same game, on every platform that ports this generator).
     * Lengths cycle from 1 to 16 rounds; a quarter of the shorter games are ended.
     */
    fun randomGame(index: Int, id: GameId): GameScenario {
        val random = Random(index * 7919L)
        val roundsCount = 1 + (index - 1) % 16
        val ended = roundsCount == 16 || random.nextInt(4) == 0
        return scenario(id, "random_%02d".format(index), "Seeded random game #$index with $roundsCount rounds.", ended = ended) {
            repeat(roundsCount) { roundIndex ->
                val isLast = roundIndex == roundsCount - 1
                val penalties = IntArray(4)
                if (random.nextInt(5) == 0) {
                    val penalized = random.nextInt(4)
                    val amount = 10 * (1 + random.nextInt(6))
                    if (random.nextBoolean()) {
                        val share = amount / 3
                        for (i in 0..3) penalties[i] = if (i == penalized) -amount else share
                    } else {
                        penalties[penalized] = -amount
                    }
                }
                if (isLast && !ended) {
                    ongoing(*penalties)
                } else {
                    val winner = TableWinds.asArray[random.nextInt(4)]
                    val points = 8 + random.nextInt(60)
                    when (random.nextInt(6)) {
                        0 -> draw(*penalties)
                        1, 2 -> selfPick(winner, points, *penalties)
                        else -> {
                            val discarder = TableWinds.asArray.filter { it != winner }[random.nextInt(3)]
                            discard(winner, discarder, points, *penalties)
                        }
                    }
                }
            }
        }
    }

    /**
     * The same game seen after 0..15 finished hands (ongoing). Used for the table screen: the dice button moves
     * corner every hand, and the prevalent wind changes every four hands. Not part of [all].
     */
    val progression: List<GameScenario> = (0..15).map { finished ->
        val winners = listOf(TableWinds.EAST, TableWinds.SOUTH, TableWinds.WEST, TableWinds.NORTH)
        scenario(
            id = 300L + finished,
            name = "progression_%02d".format(finished),
            description = "Ongoing game after $finished finished hands.",
        ) {
            repeat(finished) { hand ->
                val winner = winners[(hand * 3 + 1) % 4]
                if (hand % 3 == 2) draw() else if (hand % 2 == 0) selfPick(winner, 8 + hand) else discard(winner, winners[(hand * 3 + 2) % 4], 10 + hand * 2)
            }
            ongoing()
        }
    }

    val handWritten: List<GameScenario> = listOf(
        newGame, selfPicks, allDiscardPairs, penalties, sixteenDraws, fullGame,
        tieAll4, tieFirst3, tieLast3, tieFirst2, tieSecond2, tieLast2, tiePairs,
        specialNames, emptyGameName, bestHandTie, penaltiesOnly, bigScores,
    )

    val all: List<GameScenario> = handWritten + randomGames
}
