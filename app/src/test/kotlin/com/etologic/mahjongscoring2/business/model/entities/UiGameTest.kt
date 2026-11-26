package com.etologic.mahjongscoring2.business.model.entities

import com.etologic.mahjongscoring2.business.model.dtos.BestHand
import com.etologic.mahjongscoring2.business.model.entities.UiGame.Companion.getHuDiscardDiscarderPoints
import com.etologic.mahjongscoring2.business.model.entities.UiGame.Companion.getHuDiscardWinnerPoints
import com.etologic.mahjongscoring2.business.model.entities.UiGame.Companion.getHuSelfPickDiscarderPoints
import com.etologic.mahjongscoring2.business.model.entities.UiGame.Companion.getHuSelfPickWinnerPoints
import com.etologic.mahjongscoring2.business.model.entities.UiGame.Companion.getPenaltyOtherPlayersPoints
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.EAST
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NONE
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NORTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.SOUTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.WEST
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.Date

/** Specification of the MCR scoring and seating rules implemented by [UiGame]. */
class UiGameTest {

    private fun round(
        winner: TableWinds? = null,
        discarder: TableWinds? = null,
        points: Int = 0,
        penalties: IntArray = IntArray(4),
        roundId: Long = 0,
    ) = UiRound(
        gameId = 1,
        roundId = roundId,
        winnerInitialSeat = winner,
        discarderInitialSeat = discarder,
        handPoints = points,
        penaltyP1 = penalties[0],
        penaltyP2 = penalties[1],
        penaltyP3 = penalties[2],
        penaltyP4 = penalties[3],
    )

    private fun game(vararg rounds: UiRound, endDate: Date? = null) = UiGame(
        gameId = 1,
        nameP1 = "P1",
        nameP2 = "P2",
        nameP3 = "P3",
        nameP4 = "P4",
        startDate = Date(0),
        endDate = endDate,
        gameName = "Game",
        uiRounds = rounds.toList(),
    )

    /** A game whose ongoing round is [roundNumber] (previous rounds are draws). */
    private fun gameAtRound(roundNumber: Int) =
        game(*Array(roundNumber) { if (it < roundNumber - 1) round(NONE, NONE) else round() })

    private fun UiRound.points() = listOf(pointsP1, pointsP2, pointsP3, pointsP4)
    private fun UiRound.totals() = listOf(totalPointsP1, totalPointsP2, totalPointsP3, totalPointsP4)

    @Nested
    inner class ScoringFormulas {

        @ParameterizedTest
        @ValueSource(ints = [0, 1, 8, 13, 24, 88, 9999])
        fun `self pick winner gets (hand + 8) x 3 and each other player pays hand + 8`(hand: Int) {
            assertThat(getHuSelfPickWinnerPoints(hand)).isEqualTo((hand + 8) * 3)
            assertThat(getHuSelfPickDiscarderPoints(hand)).isEqualTo(-(hand + 8))
        }

        @ParameterizedTest
        @ValueSource(ints = [0, 1, 8, 13, 24, 88, 9999])
        fun `discard winner gets hand + 24 and discarder pays hand + 8`(hand: Int) {
            assertThat(getHuDiscardWinnerPoints(hand)).isEqualTo(hand + 24)
            assertThat(getHuDiscardDiscarderPoints(hand)).isEqualTo(-(hand + 8))
            assertThat(UiGame.POINTS_DISCARD_NEUTRAL_PLAYERS).isEqualTo(-8)
        }

        @ParameterizedTest
        @ValueSource(ints = [0, 3, 10, 30, 31, 32, 100])
        fun `divided penalty gives an integer third to each other player`(penalty: Int) {
            assertThat(getPenaltyOtherPlayersPoints(penalty)).isEqualTo(penalty / 3)
        }

        @Test
        fun `rule constants`() {
            assertThat(UiGame.NOT_SET_GAME_ID).isEqualTo(0L)
            assertThat(UiGame.NUM_MCR_PLAYERS).isEqualTo(4)
            assertThat(UiGame.MAX_MCR_ROUNDS).isEqualTo(16)
            assertThat(UiGame.MIN_MCR_POINTS).isEqualTo(8)
            assertThat(UiGame.MAX_MCR_POINTS).isEqualTo(9999)
            assertThat(UiGame.NUM_NO_WINNER_PLAYERS).isEqualTo(3)
        }
    }

    @Nested
    inner class RoundPoints {

        @Test
        fun `self pick round is zero sum`() {
            TableWinds.asArray.forEach { winner ->
                val g = game(round(winner, NONE, 10))
                val expected = TableWinds.asArray.map { if (it == winner) 54 else -18 }
                assertThat(g.uiRounds[0].points()).isEqualTo(expected)
                assertThat(g.uiRounds[0].points().sum()).isEqualTo(0)
            }
        }

        @Test
        fun `self pick is also detected when discarder is null`() {
            val g = game(round(SOUTH, null, 8))
            assertThat(g.uiRounds[0].points()).containsExactly(-16, 48, -16, -16).inOrder()
        }

        @Test
        fun `discard round for every winner and discarder pair is zero sum`() {
            TableWinds.asArray.forEach { winner ->
                TableWinds.asArray.filter { it != winner }.forEach { discarder ->
                    val g = game(round(winner, discarder, 20))
                    val expected = TableWinds.asArray.map {
                        when (it) {
                            winner -> 44
                            discarder -> -28
                            else -> -8
                        }
                    }
                    assertThat(g.uiRounds[0].points()).isEqualTo(expected)
                    assertThat(g.uiRounds[0].points().sum()).isEqualTo(0)
                }
            }
        }

        @Test
        fun `draw and ongoing rounds only count penalties`() {
            val penalties = intArrayOf(-30, 10, 10, 10)
            assertThat(game(round(NONE, NONE, 0, penalties)).uiRounds[0].points()).containsExactly(-30, 10, 10, 10).inOrder()
            assertThat(game(round(null, null, 0, penalties)).uiRounds[0].points()).containsExactly(-30, 10, 10, 10).inOrder()
        }

        @Test
        fun `a draw keeps hand points but they do not score`() {
            assertThat(game(round(NONE, NONE, 40)).uiRounds[0].points()).containsExactly(0, 0, 0, 0)
        }

        @Test
        fun `penalties are added to hand points`() {
            val g = game(round(EAST, WEST, 8, intArrayOf(0, -10, 0, 0)))
            assertThat(g.uiRounds[0].points()).containsExactly(32, -18, -16, -8).inOrder()
        }

        @Test
        fun `totals accumulate round by round including the ongoing round penalties`() {
            val g = game(
                round(EAST, WEST, 8),
                round(SOUTH, NONE, 8),
                round(NONE, NONE),
                round(null, null, 0, intArrayOf(0, 0, 0, -12)),
            )
            assertThat(g.uiRounds.map { it.totals() }).containsExactly(
                listOf(32, -8, -16, -8),
                listOf(16, 40, -32, -24),
                listOf(16, 40, -32, -24),
                listOf(16, 40, -32, -36),
            ).inOrder()
        }

        @Test
        fun `round numbers are the position in the list`() {
            val g = game(round(NONE, NONE), round(NONE, NONE), round())
            assertThat(g.uiRounds.map { it.roundNumber }).containsExactly(1, 2, 3).inOrder()
        }
    }

    @Nested
    inner class BestHandRules {

        @Test
        fun `no best hand when nobody won`() {
            val g = game(round(NONE, NONE), round())
            assertThat(g.getBestHand()).isEqualTo(BestHand())
            assertThat(g.uiRounds.none { it.isBestHand }).isTrue()
        }

        @Test
        fun `highest hand wins and first one wins ties`() {
            val g = game(round(SOUTH, EAST, 24), round(WEST, NONE, 30), round(NORTH, EAST, 30), round())
            assertThat(g.getBestHand()).isEqualTo(BestHand(roundNumber = 2, handValue = 30, playerInitialPosition = WEST, playerName = "P3"))
            assertThat(g.uiRounds.map { it.isBestHand }).containsExactly(false, true, false, false).inOrder()
        }

        @Test
        fun `a draw that keeps hand points is taken as best hand (current behaviour)`() {
            // A draw carries the hand points it had; the current implementation does consider them.
            val g = game(round(NONE, NONE, 50), round(EAST, SOUTH, 10))
            assertThat(g.getBestHand().roundNumber).isEqualTo(1)
            assertThat(g.getBestHand().playerInitialPosition).isEqualTo(NONE)
            assertThat(g.getBestHand().playerName).isEmpty()
        }

        @Test
        fun `player names by initial position`() {
            val g = game(round())
            assertThat(TableWinds.entries.map { g.getPlayerNameByInitialPosition(it) })
                .containsExactly("", "P1", "P2", "P3", "P4").inOrder()
        }
    }

    @Nested
    inner class Seating {

        @ParameterizedTest(name = "round {0}")
        @MethodSource("com.etologic.mahjongscoring2.business.model.entities.UiGameTest#seatingTable")
        fun `players move seats every four rounds`(
            roundNumber: Int,
            namesByCurrentSeat: List<String>,
            windsByInitialSeat: List<TableWinds>,
            dealerName: String,
        ) {
            val g = gameAtRound(roundNumber)
            assertThat(g.ongoingRound.roundNumber).isEqualTo(roundNumber)
            assertThat(g.getPlayersNamesByCurrentSeat().toList()).isEqualTo(namesByCurrentSeat)
            assertThat(g.getSeatsCurrentWind(roundNumber).toList()).isEqualTo(windsByInitialSeat)
            assertThat(g.getCurrentEastSeatPlayerName()).isEqualTo(dealerName)
            // The four "current wind" names are a rotation of the seat names.
            assertThat(
                listOf(
                    g.getCurrentEastSeatPlayerName(), g.getCurrentSouthSeatPlayerName(),
                    g.getCurrentWestSeatPlayerName(), g.getCurrentNorthSeatPlayerName(),
                )
            ).containsExactlyElementsIn(namesByCurrentSeat)
            // Seat -> initial seat mapping is consistent with the names.
            TableWinds.asArray.forEach { seat ->
                assertThat(g.getPlayerNameByInitialPosition(g.getPlayerInitialSeatByCurrentSeat(seat)))
                    .isEqualTo(namesByCurrentSeat[seat.code])
            }
        }

        @Test
        fun `out of range round numbers`() {
            val g = gameAtRound(17)
            assertThat(g.getSeatsCurrentWind(17)).isEmpty()
            assertThat(g.getSeatsCurrentWind(0)).isEmpty()
            assertThat(g.getCurrentEastSeatPlayerName()).isNull()
            assertThat(g.getCurrentSouthSeatPlayerName()).isNull()
            assertThat(g.getCurrentWestSeatPlayerName()).isNull()
            assertThat(g.getCurrentNorthSeatPlayerName()).isNull()
            assertThat(g.getPlayersNamesByCurrentSeat().toList()).containsExactly("P1", "P2", "P3", "P4").inOrder()
            assertThat(TableWinds.asArray.map { g.getPlayerInitialSeatByCurrentSeat(it) })
                .containsExactly(NORTH, WEST, EAST, SOUTH).inOrder()
        }

        @Test
        fun `totals and penalties by current seat follow the seat rotation`() {
            val rounds = Array(9) { if (it < 8) round(NONE, NONE) else round(penalties = intArrayOf(-1, -2, -3, -4)) }
            rounds[0] = round(EAST, NONE, 0, IntArray(4)) // P1 +24, others -8
            val g = game(*rounds)
            // Round 9: seats are [P3, P4, P2, P1]
            assertThat(g.getPlayersTotalPointsByCurrentSeat().toList()).containsExactly(-11, -12, -10, 23).inOrder()
            assertThat(g.getPlayersPenaltiesByCurrentSeat().toList()).containsExactly(-3, -4, -2, -1).inOrder()
            val diffs = g.getTableDiffs().seatsDiffs
            assertThat(diffs.map { it.seat }).containsExactly(EAST, SOUTH, WEST, NORTH).inOrder()
            assertThat(diffs.map { it.points }).containsExactly(-11, -12, -10, 23).inOrder()
        }
    }

    @Nested
    inner class GameState {

        @Test
        fun `ongoing round is the last one and finished rounds exclude it`() {
            val g = game(round(EAST, SOUTH, 8, roundId = 1), round(roundId = 2))
            assertThat(g.ongoingRound.roundId).isEqualTo(2)
            assertThat(g.finishedRounds.map { it.roundId }).containsExactly(1L)
        }

        @Test
        fun `ended state depends only on end date`() {
            assertThat(game(round()).isEnded).isFalse()
            assertThat(game(round(), endDate = Date(1)).isEnded).isTrue()
        }

        @Test
        fun `players names array is in initial seat order`() {
            assertThat(game(round()).playersNames.toList()).containsExactly("P1", "P2", "P3", "P4").inOrder()
        }

        @Test
        fun `default constructor builds an empty unsaved game with one round`() {
            val g = UiGame()
            assertThat(g.gameId).isEqualTo(UiGame.NOT_SET_GAME_ID)
            assertThat(g.uiRounds).hasSize(1)
            assertThat(g.isEnded).isFalse()
            assertThat(g.gameName).isEmpty()
        }

        @Test
        fun `a game without rounds cannot be built`() {
            org.junit.jupiter.api.assertThrows<NoSuchElementException> { game() }
        }
    }

    companion object {
        @JvmStatic
        fun seatingTable(): List<Arguments> {
            val e = listOf("P1", "P2", "P3", "P4")
            val s = listOf("P2", "P1", "P4", "P3")
            val w = listOf("P3", "P4", "P2", "P1")
            val n = listOf("P4", "P3", "P1", "P2")
            val winds = listOf(
                listOf(EAST, SOUTH, WEST, NORTH),
                listOf(NORTH, EAST, SOUTH, WEST),
                listOf(WEST, NORTH, EAST, SOUTH),
                listOf(SOUTH, WEST, NORTH, EAST),
            )
            return (1..16).map { roundNumber ->
                val names = listOf(e, s, w, n)[(roundNumber - 1) / 4]
                val handInWind = (roundNumber - 1) % 4
                Arguments.of(roundNumber, names, winds[handInWind], names[handInWind])
            }
        }
    }
}
