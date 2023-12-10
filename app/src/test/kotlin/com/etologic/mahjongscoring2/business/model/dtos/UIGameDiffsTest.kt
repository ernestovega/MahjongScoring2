package com.etologic.mahjongscoring2.business.model.dtos

import com.etologic.mahjongscoring2.app.extensions.fourth
import com.etologic.mahjongscoring2.app.extensions.second
import com.etologic.mahjongscoring2.app.extensions.third
import com.etologic.mahjongscoring2.business.model.entities.UIGame
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import kotlin.math.abs

class UIGameDiffsTest {

    @Test
    fun `1st and 2nd are equal`() {
        val tableDiffs = TableDiffs(
            eastSeatPoints = 64,
            southSeatPoints = 64,
            westSeatPoints = 0,
            northSeatPoints = -128,
        )
        assertThat(tableDiffs.seatsDiffs.first().pointsToBeFirst?.bySelfPick)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.first().pointsToBeFirst?.byDirectHu)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.first().pointsToBeFirst?.byIndirectHu)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.second().pointsToBeFirst?.bySelfPick)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.second().pointsToBeFirst?.byDirectHu)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.second().pointsToBeFirst?.byIndirectHu)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
    }

    @Test
    fun `if 2nd and 3rd are equal`() {
        val tableDiffs = TableDiffs(
            eastSeatPoints = 128,
            southSeatPoints = 32,
            westSeatPoints = 32,
            northSeatPoints = -192,
        )
        assertThat(tableDiffs.seatsDiffs.second().pointsToBeSecond?.bySelfPick)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.second().pointsToBeSecond?.byDirectHu)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.second().pointsToBeSecond?.byIndirectHu)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.third().pointsToBeSecond?.bySelfPick)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.third().pointsToBeSecond?.byDirectHu)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.third().pointsToBeSecond?.byIndirectHu)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
    }

    @Test
    fun `if 3rd and 4th are equal`() {
        val tableDiffs = TableDiffs(
            eastSeatPoints = 96,
            southSeatPoints = 32,
            westSeatPoints = -64,
            northSeatPoints = -64,
        )
        assertThat(tableDiffs.seatsDiffs.third().pointsToBeThird?.bySelfPick)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.third().pointsToBeThird?.byDirectHu)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.third().pointsToBeThird?.byIndirectHu)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.fourth().pointsToBeThird?.bySelfPick)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.fourth().pointsToBeThird?.byDirectHu)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.fourth().pointsToBeThird?.byIndirectHu)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
    }

    @Test
    fun `if 1st and 2nd are equal and 3rd and 4th are a different equal`() {
        val tableDiffs = TableDiffs(
            eastSeatPoints = 64,
            southSeatPoints = 64,
            westSeatPoints = -64,
            northSeatPoints = -64,
        )
        val diff3rdTo1st =
            abs(tableDiffs.seatsDiffs.first().points - tableDiffs.seatsDiffs.third().points)
        val diff4thTo1st =
            abs(tableDiffs.seatsDiffs.first().points - tableDiffs.seatsDiffs.fourth().points)
        assertThat(tableDiffs.seatsDiffs.first().pointsToBeFirst?.bySelfPick)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.first().pointsToBeFirst?.byDirectHu)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.first().pointsToBeFirst?.byIndirectHu)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.second().pointsToBeFirst?.bySelfPick)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.second().pointsToBeFirst?.byDirectHu)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.second().pointsToBeFirst?.byIndirectHu)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.third().pointsToBeFirst?.bySelfPick).isEqualTo(diff3rdTo1st.getNeededPointsBySelfPick())
        assertThat(tableDiffs.seatsDiffs.third().pointsToBeFirst?.byDirectHu).isEqualTo(diff3rdTo1st.getNeededPointsByDirectHu())
        assertThat(tableDiffs.seatsDiffs.third().pointsToBeFirst?.byIndirectHu).isEqualTo(diff3rdTo1st.getNeededPointsByIndirectHu())
        assertThat(tableDiffs.seatsDiffs.third().pointsToBeThird?.bySelfPick)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.third().pointsToBeThird?.byDirectHu)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.third().pointsToBeThird?.byIndirectHu)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.fourth().pointsToBeFirst?.bySelfPick).isEqualTo(diff4thTo1st.getNeededPointsBySelfPick())
        assertThat(tableDiffs.seatsDiffs.fourth().pointsToBeFirst?.byDirectHu).isEqualTo(diff4thTo1st.getNeededPointsByDirectHu())
        assertThat(tableDiffs.seatsDiffs.fourth().pointsToBeFirst?.byIndirectHu).isEqualTo(diff4thTo1st.getNeededPointsByIndirectHu())
        assertThat(tableDiffs.seatsDiffs.fourth().pointsToBeThird?.bySelfPick)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.fourth().pointsToBeThird?.byDirectHu)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.fourth().pointsToBeThird?.byIndirectHu)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
    }

    @Test
    fun `if 1st, 2nd and 3rd are equal`() {
        val tableDiffs = TableDiffs(
            eastSeatPoints = 64,
            southSeatPoints = 64,
            westSeatPoints = 64,
            northSeatPoints = -192,
        )
        assertThat(tableDiffs.seatsDiffs.first().pointsToBeFirst?.bySelfPick)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.first().pointsToBeFirst?.byDirectHu)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.first().pointsToBeFirst?.byIndirectHu)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.first().pointsToBeSecond?.bySelfPick).isEqualTo(null)
        assertThat(tableDiffs.seatsDiffs.first().pointsToBeSecond?.byDirectHu).isEqualTo(null)
        assertThat(tableDiffs.seatsDiffs.first().pointsToBeSecond?.byIndirectHu).isEqualTo(null)
        assertThat(tableDiffs.seatsDiffs.second().pointsToBeFirst?.bySelfPick)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.second().pointsToBeFirst?.byDirectHu)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.second().pointsToBeFirst?.byIndirectHu)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.second().pointsToBeSecond?.bySelfPick).isEqualTo(null)
        assertThat(tableDiffs.seatsDiffs.second().pointsToBeSecond?.byDirectHu).isEqualTo(null)
        assertThat(tableDiffs.seatsDiffs.second().pointsToBeSecond?.byIndirectHu).isEqualTo(null)
        assertThat(tableDiffs.seatsDiffs.third().pointsToBeFirst?.bySelfPick)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.third().pointsToBeFirst?.byDirectHu)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.third().pointsToBeFirst?.byIndirectHu)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.third().pointsToBeSecond?.bySelfPick).isEqualTo(null)
        assertThat(tableDiffs.seatsDiffs.third().pointsToBeSecond?.byDirectHu).isEqualTo(null)
        assertThat(tableDiffs.seatsDiffs.third().pointsToBeSecond?.byIndirectHu).isEqualTo(null)
        assertThat(tableDiffs.seatsDiffs.fourth().pointsToBeFirst?.bySelfPick).isEqualTo(57)
        assertThat(tableDiffs.seatsDiffs.fourth().pointsToBeFirst?.byDirectHu).isEqualTo(113)
        assertThat(tableDiffs.seatsDiffs.fourth().pointsToBeFirst?.byIndirectHu).isEqualTo(225)
        assertThat(tableDiffs.seatsDiffs.fourth().pointsToBeSecond?.bySelfPick).isEqualTo(null)
        assertThat(tableDiffs.seatsDiffs.fourth().pointsToBeSecond?.byDirectHu).isEqualTo(null)
        assertThat(tableDiffs.seatsDiffs.fourth().pointsToBeSecond?.byIndirectHu).isEqualTo(null)
        assertThat(tableDiffs.seatsDiffs.fourth().pointsToBeThird?.bySelfPick).isEqualTo(null)
        assertThat(tableDiffs.seatsDiffs.fourth().pointsToBeThird?.byDirectHu).isEqualTo(null)
        assertThat(tableDiffs.seatsDiffs.fourth().pointsToBeThird?.byIndirectHu).isEqualTo(null)
    }

    @Test
    fun `if 2nd, 3rd and 4th are equal`() {
        val tableDiffs = TableDiffs(
            eastSeatPoints = 192,
            southSeatPoints = -64,
            westSeatPoints = -64,
            northSeatPoints = -64,
        )
        assertThat(tableDiffs.seatsDiffs.second().pointsToBeSecond?.bySelfPick)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.second().pointsToBeSecond?.byDirectHu)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.second().pointsToBeSecond?.byIndirectHu)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.second().pointsToBeThird?.bySelfPick).isEqualTo(null)
        assertThat(tableDiffs.seatsDiffs.second().pointsToBeThird?.byDirectHu).isEqualTo(null)
        assertThat(tableDiffs.seatsDiffs.second().pointsToBeThird?.byIndirectHu).isEqualTo(null)
        assertThat(tableDiffs.seatsDiffs.third().pointsToBeSecond?.bySelfPick)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.third().pointsToBeSecond?.byDirectHu)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.third().pointsToBeSecond?.byIndirectHu)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.third().pointsToBeThird?.bySelfPick).isEqualTo(null)
        assertThat(tableDiffs.seatsDiffs.third().pointsToBeThird?.byDirectHu).isEqualTo(null)
        assertThat(tableDiffs.seatsDiffs.third().pointsToBeThird?.byIndirectHu).isEqualTo(null)
        assertThat(tableDiffs.seatsDiffs.fourth().pointsToBeSecond?.bySelfPick)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.fourth().pointsToBeSecond?.byDirectHu)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.fourth().pointsToBeSecond?.byIndirectHu)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.fourth().pointsToBeThird?.bySelfPick).isEqualTo(null)
        assertThat(tableDiffs.seatsDiffs.fourth().pointsToBeThird?.byDirectHu).isEqualTo(null)
        assertThat(tableDiffs.seatsDiffs.fourth().pointsToBeThird?.byIndirectHu).isEqualTo(null)
    }

    @Test
    fun `if 1st, 2nd, 3rd and 4th are equal`() {
        val tableDiffs = TableDiffs(
            eastSeatPoints = 0,
            southSeatPoints = 0,
            westSeatPoints = 0,
            northSeatPoints = 0,
        )
        assertThat(tableDiffs.seatsDiffs.second().pointsToBeFirst?.bySelfPick)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.second().pointsToBeFirst?.byDirectHu)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.second().pointsToBeFirst?.byIndirectHu)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.second().pointsToBeSecond?.bySelfPick).isEqualTo(null)
        assertThat(tableDiffs.seatsDiffs.second().pointsToBeSecond?.byDirectHu).isEqualTo(null)
        assertThat(tableDiffs.seatsDiffs.second().pointsToBeSecond?.byIndirectHu).isEqualTo(null)
        assertThat(tableDiffs.seatsDiffs.second().pointsToBeThird?.bySelfPick).isEqualTo(null)
        assertThat(tableDiffs.seatsDiffs.second().pointsToBeThird?.byDirectHu).isEqualTo(null)
        assertThat(tableDiffs.seatsDiffs.second().pointsToBeThird?.byIndirectHu).isEqualTo(null)
        assertThat(tableDiffs.seatsDiffs.third().pointsToBeFirst?.bySelfPick)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.third().pointsToBeFirst?.byDirectHu)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.third().pointsToBeFirst?.byIndirectHu)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.third().pointsToBeSecond?.bySelfPick).isEqualTo(null)
        assertThat(tableDiffs.seatsDiffs.third().pointsToBeSecond?.byDirectHu).isEqualTo(null)
        assertThat(tableDiffs.seatsDiffs.third().pointsToBeSecond?.byIndirectHu).isEqualTo(null)
        assertThat(tableDiffs.seatsDiffs.third().pointsToBeThird?.bySelfPick).isEqualTo(null)
        assertThat(tableDiffs.seatsDiffs.third().pointsToBeThird?.byDirectHu).isEqualTo(null)
        assertThat(tableDiffs.seatsDiffs.third().pointsToBeThird?.byIndirectHu).isEqualTo(null)
        assertThat(tableDiffs.seatsDiffs.fourth().pointsToBeFirst?.bySelfPick)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.fourth().pointsToBeFirst?.byDirectHu)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.fourth().pointsToBeFirst?.byIndirectHu)
            .isEqualTo(UIGame.MIN_MCR_POINTS)
        assertThat(tableDiffs.seatsDiffs.fourth().pointsToBeSecond?.bySelfPick).isEqualTo(null)
        assertThat(tableDiffs.seatsDiffs.fourth().pointsToBeSecond?.byDirectHu).isEqualTo(null)
        assertThat(tableDiffs.seatsDiffs.fourth().pointsToBeSecond?.byIndirectHu).isEqualTo(null)
        assertThat(tableDiffs.seatsDiffs.fourth().pointsToBeThird?.bySelfPick).isEqualTo(null)
        assertThat(tableDiffs.seatsDiffs.fourth().pointsToBeThird?.byDirectHu).isEqualTo(null)
        assertThat(tableDiffs.seatsDiffs.fourth().pointsToBeThird?.byIndirectHu).isEqualTo(null)
    }

    @TestFactory
    fun `needed points to win`() = listOf(
        dynamicTestToWinBySelfPick(3, 8),
        dynamicTestToWinBySelfPick(39, 8),
        dynamicTestToWinBySelfPick(52, 8),
        dynamicTestToWinBySelfPick(64, 9),
        dynamicTestToWinBySelfPick(68, 10),
        dynamicTestToWinBySelfPick(87, 14),
        dynamicTestToWinBySelfPick(115, 21),
        dynamicTestToWinBySelfPick(116, 22),
        dynamicTestToWinBySelfPick(117, 22),
        dynamicTestToWinBySelfPick(118, 22),
        dynamicTestToWinBySelfPick(119, 22),
        dynamicTestToWinBySelfPick(120, 23),
        dynamicTestToWinBySelfPick(164, 34),
        dynamicTestToWinBySelfPick(213, 46),
        dynamicTestToWinBySelfPick(227, 49),
        dynamicTestToWinBySelfPick(263, 58),
        dynamicTestToWinBySelfPick(299, 67),
        dynamicTestToWinBySelfPick(332, 76),
        dynamicTestToWinBySelfPick(377, 87),
        dynamicTestToWinBySelfPick(409, 95),
        dynamicTestToWinBySelfPick(428, 100),

        dynamicTestToWinByDirectHu(3, 8),
        dynamicTestToWinByDirectHu(39, 8),
        dynamicTestToWinByDirectHu(52, 11),
        dynamicTestToWinByDirectHu(64, 17),
        dynamicTestToWinByDirectHu(68, 19),
        dynamicTestToWinByDirectHu(87, 28),
        dynamicTestToWinByDirectHu(115, 42),
        dynamicTestToWinByDirectHu(116, 43),
        dynamicTestToWinByDirectHu(117, 43),
        dynamicTestToWinByDirectHu(118, 44),
        dynamicTestToWinByDirectHu(119, 44),
        dynamicTestToWinByDirectHu(120, 45),
        dynamicTestToWinByDirectHu(164, 67),
        dynamicTestToWinByDirectHu(213, 91),
        dynamicTestToWinByDirectHu(227, 98),
        dynamicTestToWinByDirectHu(263, 116),
        dynamicTestToWinByDirectHu(299, 134),
        dynamicTestToWinByDirectHu(332, 151),
        dynamicTestToWinByDirectHu(377, 173),
        dynamicTestToWinByDirectHu(409, 189),
        dynamicTestToWinByDirectHu(428, 199),

        dynamicTestToWinByIndirectHu(3, 8),
        dynamicTestToWinByIndirectHu(39, 8),
        dynamicTestToWinByIndirectHu(52, 21),
        dynamicTestToWinByIndirectHu(64, 33),
        dynamicTestToWinByIndirectHu(68, 37),
        dynamicTestToWinByIndirectHu(87, 56),
        dynamicTestToWinByIndirectHu(115, 84),
        dynamicTestToWinByIndirectHu(116, 85),
        dynamicTestToWinByIndirectHu(117, 86),
        dynamicTestToWinByIndirectHu(118, 87),
        dynamicTestToWinByIndirectHu(119, 88),
        dynamicTestToWinByIndirectHu(120, 89),
        dynamicTestToWinByIndirectHu(164, 133),
        dynamicTestToWinByIndirectHu(213, 182),
        dynamicTestToWinByIndirectHu(227, 196),
        dynamicTestToWinByIndirectHu(263, 232),
        dynamicTestToWinByIndirectHu(299, 268),
        dynamicTestToWinByIndirectHu(332, 301),
        dynamicTestToWinByIndirectHu(377, 346),
        dynamicTestToWinByIndirectHu(409, 378),
        dynamicTestToWinByIndirectHu(428, 397),
    )

    private fun dynamicTestToWinBySelfPick(diffPoints: Int, neededPoints: Int) =
        DynamicTest.dynamicTest("$diffPoints points of diff need $neededPoints by self pick") {
            assertThat(diffPoints.getNeededPointsBySelfPick()).isEqualTo(neededPoints)
        }

    private fun dynamicTestToWinByDirectHu(diffPoints: Int, neededPoints: Int) =
        DynamicTest.dynamicTest("$diffPoints points of diff need $neededPoints by direct hu") {
            assertThat(diffPoints.getNeededPointsByDirectHu()).isEqualTo(neededPoints)
        }

    private fun dynamicTestToWinByIndirectHu(diffPoints: Int, neededPoints: Int) =
        DynamicTest.dynamicTest("$diffPoints points of diff need $neededPoints by indirect hu") {
            assertThat(diffPoints.getNeededPointsByIndirectHu()).isEqualTo(neededPoints)
        }
}