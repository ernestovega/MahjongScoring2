package com.etologic.mahjongscoring2.business.model.entities

import androidx.room.*
import com.etologic.mahjongscoring2.app.base.RecyclerViewable
import com.etologic.mahjongscoring2.business.model.entities.GameWithRounds.Companion.POINTS_DISCARD_NEUTRAL_PLAYERS
import com.etologic.mahjongscoring2.business.model.entities.GameWithRounds.Companion.getHuDiscardDiscarderPoints
import com.etologic.mahjongscoring2.business.model.entities.GameWithRounds.Companion.getHuDiscardWinnerPoints
import com.etologic.mahjongscoring2.business.model.entities.GameWithRounds.Companion.getHuSelfpickDiscarderPoints
import com.etologic.mahjongscoring2.business.model.entities.GameWithRounds.Companion.getHuSelfpickWinnerPoints
import com.etologic.mahjongscoring2.business.model.entities.GameWithRounds.Companion.getPenaltyOtherPlayersPoints
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.*
import com.etologic.mahjongscoring2.data_source.local_data_source.local.converters.TableWindsConverter

@Entity(
    tableName = "Rounds",
    primaryKeys = ["gameId", "roundId"],
    foreignKeys = [ForeignKey(entity = Game::class, parentColumns = ["gameId"], childColumns = ["gameId"])],
    indices = [Index(value = ["gameId", "roundId"], unique = true)]
)
class Round(val gameId: Long, val roundId: Int) : RecyclerViewable<Round>() {
    
    companion object {
        
        fun areEqual(rounds1: List<Round>?, rounds2: List<Round>?): Boolean {
            if (rounds1 == null && rounds2 == null) {
                return true
            } else if (rounds1 != null && rounds2 != null) {
                if (rounds1.size != rounds2.size) {
                    return false
                } else {
                    for (i in rounds1.indices) {
                        if (!areEqual(rounds1[i], rounds2[i])) {
                            return false
                        }
                    }
                    return true
                }
            } else {
                return false
            }
        }
        
        private fun areEqual(round1: Round, round2: Round): Boolean {
            return round1.gameId == round2.gameId &&
                round1.roundId == round2.roundId &&
                round1.handPoints == round2.handPoints &&
                round1.winnerInitialSeat === round2.winnerInitialSeat &&
                round1.discarderInitialSeat === round2.discarderInitialSeat &&
                round1.pointsP1 == round2.pointsP1 &&
                round1.pointsP2 == round2.pointsP2 &&
                round1.pointsP3 == round2.pointsP3 &&
                round1.pointsP4 == round2.pointsP4 &&
                round1.penaltyP1 == round2.penaltyP1 &&
                round1.penaltyP2 == round2.penaltyP2 &&
                round1.penaltyP3 == round2.penaltyP3 &&
                round1.penaltyP4 == round2.penaltyP4 &&
                round1.roundDuration == round2.roundDuration &&
                round1.isBestHand == round2.isBestHand
        }
    }
    
    @TypeConverters(TableWindsConverter::class)
    var winnerInitialSeat = NONE
    
    @TypeConverters(TableWindsConverter::class)
    var discarderInitialSeat = NONE
    var handPoints = 0
    var pointsP1 = 0
    var pointsP2 = 0
    var pointsP3 = 0
    var pointsP4 = 0
    var penaltyP1 = 0
    var penaltyP2 = 0
    var penaltyP3 = 0
    var penaltyP4 = 0
    var roundDuration: Long = 0
    var isEnded = false
    
    @Ignore
    var isBestHand = false
    
    internal constructor(gameId: Long) : this(gameId, 1)
    
    private constructor(
        gameId: Long,
        roundId: Int,
        handPoints: Int,
        winnerInitialPosition: TableWinds,
        discarderInitialPosition: TableWinds,
        pointsP1: Int, pointsP2: Int, pointsP3: Int, pointsP4: Int,
        penaltyP1: Int, penaltyP2: Int, penaltyP3: Int, penaltyP4: Int,
        roundDuration: Long,
        isEnded: Boolean,
        isBestHand: Boolean
    ) : this(gameId, roundId) {
        this.handPoints = handPoints
        this.winnerInitialSeat = winnerInitialPosition
        this.discarderInitialSeat = discarderInitialPosition
        this.pointsP1 = pointsP1
        this.pointsP2 = pointsP2
        this.pointsP3 = pointsP3
        this.pointsP4 = pointsP4
        this.penaltyP1 = penaltyP1
        this.penaltyP2 = penaltyP2
        this.penaltyP3 = penaltyP3
        this.penaltyP4 = penaltyP4
        this.roundDuration = roundDuration
        this.isEnded = isEnded
        this.isBestHand = isBestHand
    }
    
    override fun compareIdTo(`object`: Round): Boolean {
        return gameId == `object`.gameId && roundId == `object`.roundId
    }
    
    override fun compareContentsTo(`object`: Round): Boolean {
        return areEqual(this, `object`)
    }
    
    override fun getCopy(): Round {
        return Round(
            gameId,
            roundId,
            handPoints,
            winnerInitialSeat,
            discarderInitialSeat,
            pointsP1,
            pointsP2,
            pointsP3,
            pointsP4,
            penaltyP1,
            penaltyP2,
            penaltyP3,
            penaltyP4,
            roundDuration,
            isEnded,
            isBestHand
        )
    }
    
    internal fun finishRoundByHuDiscard(winnerInitialSeat: TableWinds, discarderInitialSeat: TableWinds, huPoints: Int) {
        this.winnerInitialSeat = winnerInitialSeat
        this.discarderInitialSeat = discarderInitialSeat
        handPoints = huPoints
        pointsP1 += calculateDiscardSeatPoints(EAST, huPoints)
        pointsP2 += calculateDiscardSeatPoints(SOUTH, huPoints)
        pointsP3 += calculateDiscardSeatPoints(WEST, huPoints)
        pointsP4 += calculateDiscardSeatPoints(NORTH, huPoints)
        finishRound()
    }
    
    private fun calculateDiscardSeatPoints(seat: TableWinds, huPoints: Int): Int {
        return when (seat) {
            winnerInitialSeat -> getHuDiscardWinnerPoints(huPoints)
            discarderInitialSeat -> getHuDiscardDiscarderPoints(huPoints)
            else -> POINTS_DISCARD_NEUTRAL_PLAYERS
        }
    }
    
    internal fun finishRoundByHuSelfpick(winnerInitialSeat: TableWinds, huPoints: Int) {
        this.winnerInitialSeat = winnerInitialSeat
        this.handPoints = huPoints
        pointsP1 += calculateSelfpickSeatPoints(EAST, huPoints)
        pointsP2 += calculateSelfpickSeatPoints(SOUTH, huPoints)
        pointsP3 += calculateSelfpickSeatPoints(WEST, huPoints)
        pointsP4 += calculateSelfpickSeatPoints(NORTH, huPoints)
        finishRound()
    }
    
    private fun calculateSelfpickSeatPoints(seat: TableWinds, huPoints: Int): Int {
        return if (seat == winnerInitialSeat)
            getHuSelfpickWinnerPoints(huPoints)
        else
            getHuSelfpickDiscarderPoints(huPoints)
    }
    
    internal fun setPlayerPenaltyPoints(penalizedPlayerInitialPosition: TableWinds, penaltyPoints: Int) {
        when (penalizedPlayerInitialPosition) {
            EAST -> penaltyP1 -= penaltyPoints
            SOUTH -> penaltyP2 -= penaltyPoints
            WEST -> penaltyP3 -= penaltyPoints
            else -> penaltyP4 -= penaltyPoints
        }
    }
    
    internal fun setAllPlayersPenaltyPoints(penalizedPlayerInitialSeat: TableWinds, penaltyPoints: Int) {
        val noPenalizedPlayerPoints = getPenaltyOtherPlayersPoints(penaltyPoints)
        penaltyP1 += if (EAST === penalizedPlayerInitialSeat) -penaltyPoints else noPenalizedPlayerPoints
        penaltyP2 += if (SOUTH === penalizedPlayerInitialSeat) -penaltyPoints else noPenalizedPlayerPoints
        penaltyP3 += if (WEST === penalizedPlayerInitialSeat) -penaltyPoints else noPenalizedPlayerPoints
        penaltyP4 += if (NORTH === penalizedPlayerInitialSeat) -penaltyPoints else noPenalizedPlayerPoints
    }
    
    internal fun cancelAllPlayersPenalties() {
        penaltyP1 = 0
        penaltyP2 = 0
        penaltyP3 = 0
        penaltyP4 = 0
    }
    
    internal fun getPenaltyPointsFromInitialPlayerPosition(playerInitialPosition: TableWinds): Int {
        return when (playerInitialPosition) {
            EAST -> penaltyP1
            SOUTH -> penaltyP2
            WEST -> penaltyP3
            else -> penaltyP4
        }
    }
    
    internal fun finishRound() {
        applyPlayersPenalties()
//        roundDuration =
        isEnded = true
    }
    
    private fun applyPlayersPenalties() {
        pointsP1 += penaltyP1
        pointsP2 += penaltyP2
        pointsP3 += penaltyP3
        pointsP4 += penaltyP4
    }
}