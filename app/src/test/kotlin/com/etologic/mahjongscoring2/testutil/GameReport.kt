package com.etologic.mahjongscoring2.testutil

import com.etologic.mahjongscoring2.app.screens.game.dialogs.RankingTableHelper
import com.etologic.mahjongscoring2.business.model.dtos.PointsDiff
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.model.enums.TableWinds

/**
 * Renders everything the app derives from a game into a stable, human readable text.
 *
 * This is the "logical fingerprint" of a game: any change in scoring, seat rotation, best hand, ranking
 * or the "points needed" diffs changes this text and therefore breaks a golden test.
 * The format only uses plain values (no Android types), so it can be reproduced by any port.
 */
object GameReport {

    fun render(game: UiGame): String = buildString {
        appendLine("# ${game.gameName}")
        appendLine("players (initial seat): EAST=${game.nameP1.q()} SOUTH=${game.nameP2.q()} WEST=${game.nameP3.q()} NORTH=${game.nameP4.q()}")
        appendLine("startDate: ${game.startDate.time}")
        appendLine("endDate: ${game.endDate?.time}")
        appendLine("isEnded: ${game.isEnded}")
        appendLine("rounds: ${game.uiRounds.size} (finished: ${game.finishedRounds.size})")
        appendLine()

        appendLine("## Rounds")
        appendLine("n  | winner | discarder | hand | penalties            | points                       | totals                       | best | ongoing")
        game.uiRounds.forEach { r ->
            appendLine(
                listOf(
                    r.roundNumber.toString().padEnd(2),
                    r.winnerInitialSeat.s().padEnd(6),
                    r.discarderInitialSeat.s().padEnd(9),
                    r.handPoints.toString().padEnd(4),
                    ints(r.penaltyP1, r.penaltyP2, r.penaltyP3, r.penaltyP4).padEnd(20),
                    ints(r.pointsP1, r.pointsP2, r.pointsP3, r.pointsP4).padEnd(28),
                    ints(r.totalPointsP1, r.totalPointsP2, r.totalPointsP3, r.totalPointsP4).padEnd(28),
                    r.isBestHand.toString().padEnd(4),
                    r.isOngoing().toString(),
                ).joinToString(" | ")
            )
        }
        appendLine()

        val bestHand = game.getBestHand()
        appendLine("## Best hand")
        appendLine("round=${bestHand.roundNumber} value=${bestHand.handValue} seat=${bestHand.playerInitialPosition} name=${bestHand.playerName.q()}")
        appendLine()

        appendLine("## Table (ongoing round ${game.ongoingRound.roundNumber})")
        appendLine("seat winds by initial seat: ${game.getSeatsCurrentWind(game.ongoingRound.roundNumber).joinToString()}")
        appendLine("names by current seat: ${game.getPlayersNamesByCurrentSeat().joinToString { it.q() }}")
        appendLine(
            "current east/south/west/north names: ${
                listOf(
                    game.getCurrentEastSeatPlayerName(),
                    game.getCurrentSouthSeatPlayerName(),
                    game.getCurrentWestSeatPlayerName(),
                    game.getCurrentNorthSeatPlayerName(),
                ).joinToString { it?.q() ?: "null" }
            }"
        )
        appendLine("initial seat by current seat: ${TableWinds.asArray.joinToString { "$it->${game.getPlayerInitialSeatByCurrentSeat(it)}" }}")
        appendLine("totals by current seat: ${game.getPlayersTotalPointsByCurrentSeat().joinToString(prefix = "[", postfix = "]")}")
        appendLine("penalties by current seat: ${game.getPlayersPenaltiesByCurrentSeat().joinToString(prefix = "[", postfix = "]")}")
        appendLine()

        appendLine("## Points needed (diffs) by current seat")
        game.getTableDiffs().seatsDiffs.forEach { seat ->
            appendLine(
                "${seat.seat.toString().padEnd(5)} points=${seat.points.toString().padEnd(6)} " +
                        "toBe1st=${seat.pointsToBeFirst.s()} toBe2nd=${seat.pointsToBeSecond.s()} toBe3rd=${seat.pointsToBeThird.s()}"
            )
        }
        appendLine()

        appendLine("## Ranking")
        val ranking = RankingTableHelper.generateRankingTable(game)!!
        ranking.sortedPlayersRankings.forEachIndexed { index, player ->
            appendLine("${index + 1}. ${player.name.q()} score=${player.score} tablePoints=${player.points}")
        }
        appendLine("bestHandPlayerPoints=${ranking.bestHandPlayerPoints} bestHandPlayerName=${ranking.bestHandPlayerName.q()}")
        appendLine("numRounds=${ranking.numRounds} sNumRounds=${ranking.sNumRounds}")
    }

    private fun String.q() = "\"$this\""
    private fun TableWinds?.s() = this?.name ?: "null"
    private fun ints(vararg values: Int) = values.joinToString(prefix = "[", postfix = "]")
    private fun PointsDiff?.s() = this?.let { "(selfPick=${it.bySelfPick}, direct=${it.byDirectHu}, indirect=${it.byIndirectHu})" } ?: "-"
}
