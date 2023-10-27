package com.etologic.mahjongscoring2.business.model.dtos

import com.etologic.mahjongscoring2.app.extensions.fourth
import com.etologic.mahjongscoring2.app.extensions.second
import com.etologic.mahjongscoring2.app.extensions.third
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import kotlin.math.abs
import kotlin.math.ceil

data class PointsDiff(
    val bySelfPick: Int,
    val byDirectHu: Int,
    val byIndirectHu: Int,
)

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
        ).sortedBy { it.points }
            .apply {
                val diff2ndTo1st = abs(first().points - second().points)
                second().pointsToBeFirst = PointsDiff(
                    bySelfPick = diff2ndTo1st.getNeededPointsBySelfPick(),
                    byDirectHu = diff2ndTo1st.getNeededPointsByDirectHu(),
                    byIndirectHu = diff2ndTo1st.getNeededPointsByIndirectHu(),
                )

                val diff3rdTo1st = abs(first().points - third().points)
                third().pointsToBeFirst = PointsDiff(
                    bySelfPick = diff3rdTo1st.getNeededPointsBySelfPick(),
                    byDirectHu = diff3rdTo1st.getNeededPointsByDirectHu(),
                    byIndirectHu = diff3rdTo1st.getNeededPointsByIndirectHu(),
                )
                val diff3rdTo2nd = abs(second().points - third().points)
                third().pointsToBeSecond = PointsDiff(
                    bySelfPick = diff3rdTo2nd.getNeededPointsBySelfPick(),
                    byDirectHu = diff3rdTo2nd.getNeededPointsByDirectHu(),
                    byIndirectHu = diff3rdTo2nd.getNeededPointsByIndirectHu(),
                )

                val diff4thTo1st = abs(first().points - fourth().points)
                fourth().pointsToBeFirst = PointsDiff(
                    bySelfPick = diff4thTo1st.getNeededPointsBySelfPick(),
                    byDirectHu = diff4thTo1st.getNeededPointsByDirectHu(),
                    byIndirectHu = diff4thTo1st.getNeededPointsByIndirectHu(),
                )
                val diff4thTo2nd = abs(second().points - fourth().points)
                fourth().pointsToBeSecond = PointsDiff(
                    bySelfPick = diff4thTo2nd.getNeededPointsBySelfPick(),
                    byDirectHu = diff4thTo2nd.getNeededPointsByDirectHu(),
                    byIndirectHu = diff4thTo2nd.getNeededPointsByIndirectHu(),
                )
                val diff4thTo3rd = abs(third().points - fourth().points)
                fourth().pointsToBeThird = PointsDiff(
                    bySelfPick = diff4thTo3rd.getNeededPointsBySelfPick(),
                    byDirectHu = diff4thTo3rd.getNeededPointsByDirectHu(),
                    byIndirectHu = diff4thTo3rd.getNeededPointsByIndirectHu(),
                )
            }
    }
}

fun Int.getNeededPointsBySelfPick(): Int =
    when {
        this < 64 -> 8

        this.rem(4) > 0 -> (ceil(this.toFloat() / 4) - 8).toInt()

        else -> (this / 4) - 8 + 1
    }

fun Int.getNeededPointsByDirectHu(): Int =
    when {
        this < 48 -> 8

        this.rem(2) > 0 -> ceil((this - 32).toFloat() / 2).toInt()

        else -> ((this - 32) / 2) + 1
    }

fun Int.getNeededPointsByIndirectHu(): Int =
    when {
        this < 40 -> 8

        else -> this - 32 + 1
    }