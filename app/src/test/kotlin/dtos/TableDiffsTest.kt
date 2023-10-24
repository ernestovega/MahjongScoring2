package dtos

import com.etologic.mahjongscoring2.business.model.dtos.TableDiffs
import com.etologic.mahjongscoring2.business.model.dtos.getNeededPointsByDirectHu
import com.etologic.mahjongscoring2.business.model.dtos.getNeededPointsByIndirectHu
import com.etologic.mahjongscoring2.business.model.dtos.getNeededPointsBySelfPick
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NONE
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory

class TableDiffsTest {

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
        dynamicTest("$diffPoints points of diff need $neededPoints by self pick") {
            assertThat(diffPoints.getNeededPointsBySelfPick()).isEqualTo(neededPoints)
        }

    private fun dynamicTestToWinByDirectHu(diffPoints: Int, neededPoints: Int) =
        dynamicTest("$diffPoints points of diff need $neededPoints by direct hu") {
            assertThat(diffPoints.getNeededPointsByDirectHu()).isEqualTo(neededPoints)
        }

    private fun dynamicTestToWinByIndirectHu(diffPoints: Int, neededPoints: Int) =
        dynamicTest("$diffPoints points of diff need $neededPoints by indirect hu") {
            assertThat(diffPoints.getNeededPointsByIndirectHu()).isEqualTo(neededPoints)
        }
}