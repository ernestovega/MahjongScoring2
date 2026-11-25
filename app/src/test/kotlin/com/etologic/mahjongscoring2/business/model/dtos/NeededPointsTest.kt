package com.etologic.mahjongscoring2.business.model.dtos

import com.etologic.mahjongscoring2.app.model.Diff
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.testutil.Goldens
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

/**
 * "Points needed" to overtake a player, used by the table diffs and the diffs calculator screen.
 * The whole table 0..2000 is pinned in a golden file (golden/needed_points.csv).
 */
class NeededPointsTest {

    @ParameterizedTest(name = "diff {0}")
    @CsvSource(
        "0,8,8,8",
        "39,8,8,8",
        "40,8,8,9",
        "47,8,8,16",
        "48,8,9,17",
        "49,8,9,18",
        "50,8,10,19",
        "63,8,16,32",
        "64,9,17,33",
        "65,9,17,34",
        "68,10,19,37",
        "100,18,35,69",
        "101,18,35,70",
    )
    fun `boundaries`(diff: Int, selfPick: Int, direct: Int, indirect: Int) {
        assertThat(diff.getNeededPointsBySelfPick()).isEqualTo(selfPick)
        assertThat(diff.getNeededPointsByDirectHu()).isEqualTo(direct)
        assertThat(diff.getNeededPointsByIndirectHu()).isEqualTo(indirect)
        val model = Diff(diff)
        assertThat(listOf(model.selfPick, model.directHu, model.indirectHu)).containsExactly(selfPick, direct, indirect).inOrder()
    }

    @Test
    fun `needed points always make the chasing player strictly overtake`() {
        // Winning by self pick with X gives (X+8)*3 to the winner and costs the leader X+8: a swing of 4(X+8).
        // Direct hu (leader discards): swing of (X+24) + (X+8). Indirect hu (third player discards): X+24+8.
        for (diff in 1..2000) {
            val s = diff.getNeededPointsBySelfPick()
            val d = diff.getNeededPointsByDirectHu()
            val i = diff.getNeededPointsByIndirectHu()
            assertThat(4 * (s + 8)).isGreaterThan(diff)
            assertThat((d + 24) + (d + 8)).isGreaterThan(diff)
            assertThat(i + 24 + 8).isGreaterThan(diff)
            assertThat(listOf(s, d, i).all { it >= 8 }).isTrue()
        }
    }

    @Test
    fun `needed points table golden`() {
        val csv = buildString {
            appendLine("diff,selfPick,directHu,indirectHu")
            for (diff in 0..2000) {
                appendLine("$diff,${diff.getNeededPointsBySelfPick()},${diff.getNeededPointsByDirectHu()},${diff.getNeededPointsByIndirectHu()}")
            }
        }
        Goldens.assertMatches("needed_points.csv", csv)
    }

    @Test
    fun `table diffs for every ordering pattern golden`() {
        // Every combination of 4 scores from a small set covers all tie patterns and seat orders.
        val values = listOf(-64, 0, 16, 64, 200)
        val text = buildString {
            for (e in values) for (s in values) for (w in values) for (n in values) {
                val diffs = TableDiffs(e, s, w, n).seatsDiffs
                assertThat(diffs.map { it.seat }).containsExactlyElementsIn(TableWinds.asArray).inOrder()
                append("[$e,$s,$w,$n] ")
                appendLine(diffs.joinToString(" ") { d -> "${d.seat.name[0]}:${d.pointsToBeFirst.s()}/${d.pointsToBeSecond.s()}/${d.pointsToBeThird.s()}" })
            }
        }
        Goldens.assertMatches("table_diffs.txt", text)
    }

    @Test
    fun `points diff equality helper`() {
        val a = PointsDiff(1, 2, 3)
        assertThat(a.areEquals(PointsDiff(1, 2, 3))).isTrue()
        assertThat(a.areEquals(PointsDiff(1, 2, 4))).isFalse()
        assertThat(a.areEquals(null)).isFalse()
        assertThat((null as PointsDiff?).areEquals(null)).isTrue()
    }

    private fun PointsDiff?.s() = this?.let { "${it.bySelfPick},${it.byDirectHu},${it.byIndirectHu}" } ?: "-"
}
