package com.etologic.mahjongscoring2.business.model.entities

import androidx.room.*
import com.etologic.mahjongscoring2.app.base.RecyclerViewable
import com.etologic.mahjongscoring2.business.model.entities.Table.Companion.POINTS_DISCARD_NEUTRAL_PLAYERS
import com.etologic.mahjongscoring2.business.model.entities.Table.Companion.getHuDiscardDiscarderPoints
import com.etologic.mahjongscoring2.business.model.entities.Table.Companion.getHuDiscardWinnerPoints
import com.etologic.mahjongscoring2.business.model.entities.Table.Companion.getHuSelfpickDiscarderPoints
import com.etologic.mahjongscoring2.business.model.entities.Table.Companion.getHuSelfpickWinnerPoints
import com.etologic.mahjongscoring2.business.model.entities.Table.Companion.getPenaltyOtherPlayersPoints
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.*
import com.etologic.mahjongscoring2.data_source.local_data_source.local.converters.TableWindsConverter

@Entity(
    tableName = "Rounds",
    foreignKeys = [ForeignKey(entity = Game::class, parentColumns = ["gameId"], childColumns = ["gameId"])],
    indices = [Index(value = ["gameId", "roundId"], unique = true)]
)
class Round(
    val gameId: Long,
    @field:PrimaryKey(autoGenerate = true) val roundId: Int
) : RecyclerViewable<Round>() {
    
    @TypeConverters(TableWindsConverter::class)
    var winnerInitialSeat = NONE
    
    @TypeConverters(TableWindsConverter::class)
    var discarderInitialSeat = NONE
    var handPoints = 0
    var pointsP1 = 0
    var pointsP2 = 0
    var pointsP3 = 0
    var pointsP4 = 0
    @Ignore var totalPointsP1 = 0
    @Ignore var totalPointsP2 = 0
    @Ignore var totalPointsP3 = 0
    @Ignore var totalPointsP4 = 0
    var penaltyP1 = 0
    var penaltyP2 = 0
    var penaltyP3 = 0
    var penaltyP4 = 0
    var roundDuration: Long = 0
    var isEnded = false
    
    @Ignore
    var isBestHand = false
    
    internal constructor(gameId: Long) : this(gameId, NOT_SET_ROUND_ID)
    
    private constructor(
        gameId: Long,
        roundId: Int,
        handPoints: Int,
        winnerInitialPosition: TableWinds,
        discarderInitialPosition: TableWinds,
        pointsP1: Int, pointsP2: Int, pointsP3: Int, pointsP4: Int,
        totalPointsP1: Int, totalPointsP2: Int, totalPointsP3: Int, totalPointsP4: Int,
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
        this.totalPointsP1 = totalPointsP1
        this.totalPointsP2 = totalPointsP2
        this.totalPointsP3 = totalPointsP3
        this.totalPointsP4 = totalPointsP4
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
            totalPointsP1,
            totalPointsP2,
            totalPointsP3,
            totalPointsP4,
            penaltyP1,
            penaltyP2,
            penaltyP3,
            penaltyP4,
            roundDuration,
            isEnded,
            isBestHand
        )
    }
    
    internal fun finishRoundByHuDiscard(
        winnerInitialSeat: TableWinds,
        discarderInitialSeat: TableWinds,
        huPoints: Int
    ) {
        this.winnerInitialSeat = winnerInitialSeat
        this.discarderInitialSeat = discarderInitialSeat
        handPoints = huPoints
        pointsP1 += calculateDiscardSeatPoints(EAST, huPoints)
        pointsP2 += calculateDiscardSeatPoints(SOUTH, huPoints)
        pointsP3 += calculateDiscardSeatPoints(WEST, huPoints)
        pointsP4 += calculateDiscardSeatPoints(NORTH, huPoints)
        finishRoundApplyingPenalties()
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
        finishRoundApplyingPenalties()
    }
    
    private fun calculateSelfpickSeatPoints(seat: TableWinds, huPoints: Int): Int {
        return if (seat == winnerInitialSeat)
            getHuSelfpickWinnerPoints(huPoints)
        else
            getHuSelfpickDiscarderPoints(huPoints)
    }
    
    internal fun finishRoundByDraw() {
        finishRoundApplyingPenalties()
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
    
    internal fun finishRoundApplyingPenalties() {
        applyPlayersPenalties()
        endRound()
    }
    
    private fun applyPlayersPenalties() {
        pointsP1 += penaltyP1
        pointsP2 += penaltyP2
        pointsP3 += penaltyP3
        pointsP4 += penaltyP4
    }
    
    internal fun endRound() {
        isEnded = true
    }
    
    internal fun resumeRound() {
        isEnded = false
    }
    
    internal fun areTherePenalties(): Boolean = penaltyP1 > 0 || penaltyP2 > 0 || penaltyP3 > 0 || penaltyP4 > 0
    
    internal fun setTotalsPoints(playersTotalPoints: IntArray) {
        totalPointsP1 = playersTotalPoints[EAST.code]
        totalPointsP2 = playersTotalPoints[SOUTH.code]
        totalPointsP3 = playersTotalPoints[WEST.code]
        totalPointsP4 = playersTotalPoints[NORTH.code]
    }
    
    companion object {
        
        private const val NOT_SET_ROUND_ID: Int = 0
        
        fun areEqual(rounds1: List<Round>?, rounds2: List<Round>?): Boolean {
            if (rounds1 == null && rounds2 == null)
                return true
            else if (rounds1 != null && rounds2 != null) {
                if (rounds1.size != rounds2.size)
                    return false
                else {
                    for (i in rounds1.indices) {
                        if (!areEqual(rounds1[i], rounds2[i]))
                            return false
                    }
                    return true
                }
            } else
                return false
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
                round1.totalPointsP1 == round2.totalPointsP1 &&
                round1.totalPointsP2 == round2.totalPointsP2 &&
                round1.totalPointsP3 == round2.totalPointsP3 &&
                round1.totalPointsP4 == round2.totalPointsP4 &&
                round1.penaltyP1 == round2.penaltyP1 &&
                round1.penaltyP2 == round2.penaltyP2 &&
                round1.penaltyP3 == round2.penaltyP3 &&
                round1.penaltyP4 == round2.penaltyP4 &&
                round1.roundDuration == round2.roundDuration &&
                round1.isBestHand == round2.isBestHand &&
                round1.isEnded == round2.isEnded
        }
    }
}