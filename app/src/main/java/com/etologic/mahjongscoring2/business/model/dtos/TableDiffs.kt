package com.etologic.mahjongscoring2.business.model.dtos

import com.etologic.mahjongscoring2.app.extensions.fourth
import com.etologic.mahjongscoring2.app.extensions.second
import com.etologic.mahjongscoring2.app.extensions.third
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.google.android.gms.common.util.VisibleForTesting
import kotlin.math.abs
import kotlin.math.ceil

data class SeatPoints(
    val seat: TableWinds,
    val points: Int,
)

data class TableDiffs(
    val eastSeatPoints: Int,
    val southSeatPoints: Int,
    val westSeatPoints: Int,
    val northSeatPoints: Int,
) {

    private var seatsPoints: List<SeatPoints>

    private var diff2ndTo1st: Int
    private var diff3rdTo1st: Int
    private var diff3rdTo2nd: Int
    private var diff4thTo1st: Int
    private var diff4thTo2nd: Int
    private var diff4thTo3rd: Int

    private var pointsFor2ndToBe1stBySelfPick: Int
    private var pointsFor2ndToBe1stByDirectHu: Int
    private var pointsFor2ndToBe1stByIndirectHu: Int
    private var pointsFor3rdToBe1stBySelfPick: Int
    private var pointsFor3rdToBe1stByDirectHu: Int
    private var pointsFor3rdToBe1stByIndirectHu: Int
    private var pointsFor3rdToBe2ndBySelfPick: Int
    private var pointsFor3rdToBe2ndByDirectHu: Int
    private var pointsFor3rdToBe2ndByIndirectHu: Int
    private var pointsFor4thToBe1stBySelfPick: Int
    private var pointsFor4thToBe1stByDirectHu: Int
    private var pointsFor4thToBe1stByIndirectHu: Int
    private var pointsFor4thToBe2ndBySelfPick: Int
    private var pointsFor4thToBe2ndByDirectHu: Int
    private var pointsFor4thToBe2ndByIndirectHu: Int
    private var pointsFor4thToBe3rdBySelfPick: Int
    private var pointsFor4thToBe3rdByDirectHu: Int
    private var pointsFor4thToBe3rdByIndirectHu: Int

    init {
        seatsPoints = listOf(
            SeatPoints(TableWinds.EAST, eastSeatPoints),
            SeatPoints(TableWinds.SOUTH, southSeatPoints),
            SeatPoints(TableWinds.WEST, westSeatPoints),
            SeatPoints(TableWinds.NORTH, northSeatPoints),
        ).sortedBy { it.points }
            .also {
                diff2ndTo1st = abs(it.first().points - it.second().points)

                diff3rdTo1st = abs(it.first().points - it.third().points)
                diff3rdTo2nd = abs(it.second().points - it.third().points)

                diff4thTo1st = abs(it.first().points - it.fourth().points)
                diff4thTo2nd = abs(it.second().points - it.fourth().points)
                diff4thTo3rd = abs(it.third().points - it.fourth().points)

                pointsFor2ndToBe1stBySelfPick = diff2ndTo1st.getNeededPointsBySelfPick()
                pointsFor2ndToBe1stByDirectHu = diff2ndTo1st.getNeededPointsByDirectHu()
                pointsFor2ndToBe1stByIndirectHu = diff2ndTo1st.getNeededPointsByIndirectHu()

                pointsFor3rdToBe1stBySelfPick = diff3rdTo1st.getNeededPointsBySelfPick()
                pointsFor3rdToBe1stByDirectHu = diff3rdTo1st.getNeededPointsByDirectHu()
                pointsFor3rdToBe1stByIndirectHu = diff3rdTo1st.getNeededPointsByIndirectHu()
                pointsFor3rdToBe2ndBySelfPick = diff3rdTo2nd.getNeededPointsBySelfPick()
                pointsFor3rdToBe2ndByDirectHu = diff3rdTo2nd.getNeededPointsByDirectHu()
                pointsFor3rdToBe2ndByIndirectHu = diff3rdTo2nd.getNeededPointsByIndirectHu()

                pointsFor4thToBe1stBySelfPick = diff4thTo1st.getNeededPointsBySelfPick()
                pointsFor4thToBe1stByDirectHu = diff4thTo1st.getNeededPointsByDirectHu()
                pointsFor4thToBe1stByIndirectHu = diff4thTo1st.getNeededPointsByIndirectHu()
                pointsFor4thToBe2ndBySelfPick = diff4thTo2nd.getNeededPointsBySelfPick()
                pointsFor4thToBe2ndByDirectHu = diff4thTo2nd.getNeededPointsByDirectHu()
                pointsFor4thToBe2ndByIndirectHu = diff4thTo2nd.getNeededPointsByIndirectHu()
                pointsFor4thToBe3rdBySelfPick = diff4thTo3rd.getNeededPointsBySelfPick()
                pointsFor4thToBe3rdByDirectHu = diff4thTo3rd.getNeededPointsByDirectHu()
                pointsFor4thToBe3rdByIndirectHu = diff4thTo3rd.getNeededPointsByIndirectHu()
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