/*
 *     Copyright © 2023  Ernesto Vega de la Iglesia Soria
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.etologic.mahjongscoring2.business.model.dtos

import com.etologic.mahjongscoring2.app.extensions.fourth
import com.etologic.mahjongscoring2.app.extensions.second
import com.etologic.mahjongscoring2.app.extensions.third
import com.etologic.mahjongscoring2.business.model.entities.UiGame.Companion.MIN_MCR_POINTS
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import kotlin.math.abs
import kotlin.math.ceil

data class PointsDiff(
    val bySelfPick: Int,
    val byDirectHu: Int,
    val byIndirectHu: Int,
)

fun PointsDiff?.areEquals(other: PointsDiff?): Boolean =
    this?.bySelfPick == other?.bySelfPick &&
            this?.byDirectHu == other?.byDirectHu &&
            this?.byIndirectHu == other?.byIndirectHu

data class SeatDiffs(
    val seat: TableWinds,
    val points: Int,
    var pointsToBeFirst: PointsDiff? = null,
    var pointsToBeSecond: PointsDiff? = null,
    var pointsToBeThird: PointsDiff? = null,
)

data class TableDiffs(
    private val eastSeatPoints: Int,
    private val southSeatPoints: Int,
    private val westSeatPoints: Int,
    private val northSeatPoints: Int,
) {

    var seatsDiffs: List<SeatDiffs>

    init {
        seatsDiffs = listOf(
            SeatDiffs(TableWinds.EAST, eastSeatPoints),
            SeatDiffs(TableWinds.SOUTH, southSeatPoints),
            SeatDiffs(TableWinds.WEST, westSeatPoints),
            SeatDiffs(TableWinds.NORTH, northSeatPoints),
        ).sortedByDescending { it.points }
            .apply {
                val diff2ndTo1st = abs(first().points - second().points)
                val diff3rdTo1st = abs(first().points - third().points)
                val diff3rdTo2nd = abs(second().points - third().points)
                val diff4thTo1st = abs(first().points - fourth().points)
                val diff4thTo2nd = abs(second().points - fourth().points)
                val diff4thTo3rd = abs(third().points - fourth().points)

                if (// 1st == 2nd == 3rd == 4th
                    diff4thTo3rd == 0 && diff4thTo2nd == 0 && diff4thTo1st == 0) {
                    first().pointsToBeFirst = drawPointsDiff
                    second().pointsToBeFirst = drawPointsDiff
                    third().pointsToBeFirst = drawPointsDiff
                    fourth().pointsToBeFirst = drawPointsDiff
                }
                // 1st == 2nd == 3rd
                else if (diff3rdTo2nd == 0 && diff3rdTo1st == 0) {
                    first().pointsToBeFirst = drawPointsDiff
                    second().pointsToBeFirst = drawPointsDiff
                    third().pointsToBeFirst = drawPointsDiff
                    fourth().pointsToBeFirst = getPointsDiffs(diff4thTo1st)
                }
                // 2nd == 3rd == 4th
                else if (diff4thTo3rd == 0 && diff4thTo2nd == 0) {
                    second().pointsToBeFirst = getPointsDiffs(diff2ndTo1st)
                    second().pointsToBeSecond = drawPointsDiff
                    third().pointsToBeFirst = getPointsDiffs(diff3rdTo1st)
                    third().pointsToBeSecond = drawPointsDiff
                    fourth().pointsToBeFirst = getPointsDiffs(diff4thTo1st)
                    fourth().pointsToBeSecond = drawPointsDiff
                }
                // 1st == 2nd && 3rd == 4th
                else if (diff2ndTo1st == 0 && diff4thTo3rd == 0) {
                    first().pointsToBeFirst = drawPointsDiff
                    second().pointsToBeFirst = drawPointsDiff
                    third().pointsToBeFirst = getPointsDiffs(diff3rdTo1st)
                    third().pointsToBeThird = drawPointsDiff
                    fourth().pointsToBeFirst = getPointsDiffs(diff4thTo1st)
                    fourth().pointsToBeThird = drawPointsDiff
                }
                // 1st == 2nd
                else if (diff2ndTo1st == 0) {
                    first().pointsToBeFirst = drawPointsDiff
                    second().pointsToBeFirst = drawPointsDiff
                    third().pointsToBeFirst = getPointsDiffs(diff3rdTo1st)
                    fourth().pointsToBeFirst = getPointsDiffs(diff4thTo1st)
                    fourth().pointsToBeThird = getPointsDiffs(diff4thTo3rd)
                }
                // 2nd == 3rd
                else if (diff3rdTo2nd == 0) {
                    second().pointsToBeFirst = getPointsDiffs(diff2ndTo1st)
                    second().pointsToBeSecond = drawPointsDiff
                    third().pointsToBeFirst = getPointsDiffs(diff3rdTo1st)
                    third().pointsToBeSecond = drawPointsDiff
                    fourth().pointsToBeFirst = getPointsDiffs(diff4thTo1st)
                    fourth().pointsToBeSecond = getPointsDiffs(diff4thTo2nd)
                }
                // 3rd == 4th
                else if (diff4thTo3rd == 0) {
                    second().pointsToBeFirst = getPointsDiffs(diff2ndTo1st)
                    third().pointsToBeFirst = getPointsDiffs(diff3rdTo1st)
                    third().pointsToBeSecond = getPointsDiffs(diff3rdTo2nd)
                    third().pointsToBeThird = drawPointsDiff
                    fourth().pointsToBeFirst = getPointsDiffs(diff4thTo1st)
                    fourth().pointsToBeSecond = getPointsDiffs(diff4thTo2nd)
                    fourth().pointsToBeThird = drawPointsDiff
                }
                // 1st != 2nd != 3rd != 4th
                else {
                    second().pointsToBeFirst = getPointsDiffs(diff2ndTo1st)
                    third().pointsToBeFirst = getPointsDiffs(diff3rdTo1st)
                    third().pointsToBeSecond = getPointsDiffs(diff3rdTo2nd)
                    fourth().pointsToBeFirst = getPointsDiffs(diff4thTo1st)
                    fourth().pointsToBeSecond = getPointsDiffs(diff4thTo2nd)
                    fourth().pointsToBeThird = getPointsDiffs(diff4thTo3rd)
                }

                // Remove unnecessary cases
                if (second().pointsToBeSecond.areEquals(second().pointsToBeFirst)) {
                    second().pointsToBeSecond = null
                }
                if (third().pointsToBeThird.areEquals(third().pointsToBeSecond)) {
                    third().pointsToBeThird = null
                }
                if (third().pointsToBeSecond.areEquals(third().pointsToBeFirst)) {
                    third().pointsToBeSecond = null
                }
                if (fourth().pointsToBeThird.areEquals(fourth().pointsToBeSecond)) {
                    fourth().pointsToBeThird = null
                }
                if (fourth().pointsToBeSecond.areEquals(fourth().pointsToBeFirst)) {
                    fourth().pointsToBeSecond = null
                }
            }.sortedBy { it.seat.code }
    }

    private fun getPointsDiffs(pointsDiff: Int) =
        PointsDiff(
            bySelfPick = pointsDiff.getNeededPointsBySelfPick(),
            byDirectHu = pointsDiff.getNeededPointsByDirectHu(),
            byIndirectHu = pointsDiff.getNeededPointsByIndirectHu(),
        )

    companion object {
        private val drawPointsDiff = PointsDiff(
            bySelfPick = MIN_MCR_POINTS,
            byDirectHu = MIN_MCR_POINTS,
            byIndirectHu = MIN_MCR_POINTS,
        )
    }
}

fun Int.getNeededPointsBySelfPick(): Int =
    when {
        this < 64 -> MIN_MCR_POINTS

        this.rem(4) > 0 -> (ceil(this.toFloat() / 4) - MIN_MCR_POINTS).toInt()

        else -> (this / 4) - MIN_MCR_POINTS + 1
    }

fun Int.getNeededPointsByDirectHu(): Int =
    when {
        this < 48 -> MIN_MCR_POINTS

        this.rem(2) > 0 -> ceil((this - 32).toFloat() / 2).toInt()

        else -> ((this - 32) / 2) + 1
    }

fun Int.getNeededPointsByIndirectHu(): Int =
    when {
        this < 40 -> MIN_MCR_POINTS

        else -> this - 32 + 1
    }