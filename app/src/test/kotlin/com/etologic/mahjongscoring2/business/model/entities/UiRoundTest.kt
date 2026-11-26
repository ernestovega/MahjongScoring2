package com.etologic.mahjongscoring2.business.model.entities

import com.etologic.mahjongscoring2.business.model.enums.TableWinds.EAST
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NONE
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.SOUTH
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class UiRoundTest {

    private fun round(roundId: Long = 1, gameId: Long = 1, points: Int = 8, p1: Int = 0) =
        UiRound(gameId, roundId, EAST, SOUTH, points, p1, 0, 0, 0)

    @Test
    fun `default round is ongoing without points or penalties`() {
        val r = UiRound()
        assertThat(r.isOngoing()).isTrue()
        assertThat(r.areTherePenalties()).isFalse()
        assertThat(r.gameId).isEqualTo(UiGame.NOT_SET_GAME_ID)
        assertThat(r.roundId).isEqualTo(UiRound.NOT_SET_ROUND_ID)
        assertThat(r.handPoints).isEqualTo(0)
    }

    @Test
    fun `a round is ongoing only while it has no winner, a draw (NONE) is finished`() {
        assertThat(UiRound(1, 1, null, null, 0, 0, 0, 0, 0).isOngoing()).isTrue()
        assertThat(UiRound(1, 1, NONE, NONE, 0, 0, 0, 0, 0).isOngoing()).isFalse()
        assertThat(round().isOngoing()).isFalse()
    }

    @Test
    fun `any non zero penalty counts as penalty`() {
        for (i in 0..3) {
            val p = IntArray(4).also { it[i] = -5 }
            assertThat(UiRound(1, 1, null, null, 0, p[0], p[1], p[2], p[3]).areTherePenalties()).isTrue()
        }
    }

    @Test
    fun `copy keeps derived values`() {
        val r = round().apply {
            roundNumber = 3; pointsP1 = 1; pointsP2 = 2; pointsP3 = 3; pointsP4 = 4
            totalPointsP1 = 5; totalPointsP2 = 6; totalPointsP3 = 7; totalPointsP4 = 8; isBestHand = true
        }
        val copy = r.getCopy()
        assertThat(copy).isNotSameInstanceAs(r)
        assertThat(UiRound.areEqual(listOf(r), listOf(copy))).isTrue()
        assertThat(r.compareContentsTo(copy)).isTrue()
    }

    @Test
    fun `identity compares game and round ids`() {
        assertThat(round().compareIdTo(round(points = 99))).isTrue()
        assertThat(round().compareIdTo(round(roundId = 2))).isFalse()
        assertThat(round().compareIdTo(round(gameId = 2))).isFalse()
    }

    @Test
    fun `content equality covers inputs and derived values`() {
        assertThat(round().compareContentsTo(round(points = 9))).isFalse()
        assertThat(round().compareContentsTo(round(p1 = -1))).isFalse()
        assertThat(round().compareContentsTo(round().apply { isBestHand = true })).isFalse()
        assertThat(round().compareContentsTo(round().apply { totalPointsP4 = 1 })).isFalse()
    }

    @Test
    fun `list equality`() {
        assertThat(UiRound.areEqual(null, null)).isTrue()
        assertThat(UiRound.areEqual(null, emptyList())).isFalse()
        assertThat(UiRound.areEqual(listOf(round()), emptyList())).isFalse()
        assertThat(UiRound.areEqual(listOf(round(), round(2)), listOf(round(), round(2)))).isTrue()
        assertThat(UiRound.areEqual(listOf(round(), round(2)), listOf(round(2), round()))).isFalse()
    }
}
