package es.etologic.mahjongscoring2.domain.model

import androidx.room.*
import es.etologic.mahjongscoring2.app.base.RecyclerViewable
import es.etologic.mahjongscoring2.data.local_data_source.local.converters.TableWindsConverter
import es.etologic.mahjongscoring2.domain.model.enums.TableWinds
import es.etologic.mahjongscoring2.domain.model.enums.TableWinds.*

@Entity(
    tableName = "Rounds",
    primaryKeys = ["gameId", "roundId"],
    foreignKeys = [ForeignKey(entity = Game::class, parentColumns = ["gameId"], childColumns = ["gameId"])],
    indices = [Index(value = ["gameId", "roundId"], unique = true)]
)
class Round(val gameId: Int, val roundId: Int) : RecyclerViewable<Round>() {
    
    companion object {
        
        private const val HU_BASE_POINTS = 8
        private const val NUM_NO_WINNER_PLAYERS = 3
        private const val NUM_NO_WINNER_AND_NO_LOOSER_PLAYERS_IN_RON = 2
        
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
                round1.winnerInitialPosition === round2.winnerInitialPosition &&
                round1.discarderInitialPosition === round2.discarderInitialPosition &&
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
    
    var handPoints = 0
    @TypeConverters(TableWindsConverter::class)
    var winnerInitialPosition = NONE
    @TypeConverters(TableWindsConverter::class)
    var discarderInitialPosition = NONE
    var pointsP1 = 0
    var pointsP2 = 0
    var pointsP3 = 0
    var pointsP4 = 0
    var penaltyP1 = 0
    var penaltyP2 = 0
    var penaltyP3 = 0
    var penaltyP4 = 0
    var roundDuration: Long = 0
    @Ignore
    var isBestHand = false
    
    private constructor(
        gameId: Int,
        roundId: Int,
        handPoints: Int,
        winnerInitialPosition: TableWinds,
        discarderInitialPosition: TableWinds,
        pointsP1: Int, pointsP2: Int, pointsP3: Int, pointsP4: Int,
        penaltyP1: Int, penaltyP2: Int, penaltyP3: Int, penaltyP4: Int,
        roundDuration: Long,
        isBestHand: Boolean
    ) : this(gameId, roundId) {
        this.handPoints = handPoints
        this.winnerInitialPosition = winnerInitialPosition
        this.discarderInitialPosition = discarderInitialPosition
        this.pointsP1 = pointsP1
        this.pointsP2 = pointsP2
        this.pointsP3 = pointsP3
        this.pointsP4 = pointsP4
        this.penaltyP1 = penaltyP1
        this.penaltyP2 = penaltyP2
        this.penaltyP3 = penaltyP3
        this.penaltyP4 = penaltyP4
        this.roundDuration = roundDuration
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
            winnerInitialPosition,
            discarderInitialPosition,
            pointsP1,
            pointsP2,
            pointsP3,
            pointsP4,
            penaltyP1,
            penaltyP2,
            penaltyP3,
            penaltyP4,
            roundDuration,
            isBestHand
        )
    }
    
    fun setAllPlayersTsumoPoints(winnerInitialPosition: TableWinds, winnerHandPoints: Int) {
        this.winnerInitialPosition = winnerInitialPosition
        this.handPoints = winnerHandPoints
        val looserTotalPoints = winnerHandPoints + HU_BASE_POINTS
        val winnerTotalPoints = looserTotalPoints * NUM_NO_WINNER_PLAYERS
        pointsP1 += if (EAST === winnerInitialPosition) winnerTotalPoints else -looserTotalPoints
        pointsP2 += if (SOUTH === winnerInitialPosition) winnerTotalPoints else -looserTotalPoints
        pointsP3 += if (WEST === winnerInitialPosition) winnerTotalPoints else -looserTotalPoints
        pointsP4 += if (NORTH === winnerInitialPosition) winnerTotalPoints else -looserTotalPoints
    }
    
    fun setAllPlayersRonPoints(winnerInitialPosition: TableWinds, winnerHandPoints: Int, looserInitialPosition: TableWinds) {
        this.winnerInitialPosition = winnerInitialPosition
        this.handPoints = winnerHandPoints
        this.discarderInitialPosition = looserInitialPosition
        val looserTotalPoints = winnerHandPoints + HU_BASE_POINTS
        val winnerTotalPoints = looserTotalPoints + HU_BASE_POINTS * NUM_NO_WINNER_AND_NO_LOOSER_PLAYERS_IN_RON
        if (EAST === winnerInitialPosition) {
            pointsP1 += winnerTotalPoints
        } else {
            pointsP1 -= if (EAST === looserInitialPosition) looserTotalPoints else HU_BASE_POINTS
        }
        if (SOUTH === winnerInitialPosition) {
            pointsP2 += winnerTotalPoints
        } else {
            pointsP2 -= if (SOUTH === looserInitialPosition) looserTotalPoints else HU_BASE_POINTS
        }
        if (WEST === winnerInitialPosition) {
            pointsP3 += winnerTotalPoints
        } else {
            pointsP3 -= if (WEST === looserInitialPosition) looserTotalPoints else HU_BASE_POINTS
        }
        if (NORTH === winnerInitialPosition) {
            pointsP4 += winnerTotalPoints
        } else {
            pointsP4 -= if (NORTH === looserInitialPosition) looserTotalPoints else HU_BASE_POINTS
        }
    }
    
    fun setPlayerPenaltyPoints(penalizedPlayerInitialPosition: TableWinds, penaltyPoints: Int) {
        when (penalizedPlayerInitialPosition) {
            EAST -> penaltyP1 -= penaltyPoints
            SOUTH -> penaltyP2 -= penaltyPoints
            WEST -> penaltyP3 -= penaltyPoints
            else -> penaltyP4 -= penaltyPoints
        }
    }
    
    fun setAllPlayersPenaltyPoints(penalizedPlayerInitialPosition: TableWinds, penaltyPoints: Int) {
        val noPenalizedPlayerPoints = penaltyPoints / NUM_NO_WINNER_PLAYERS
        penaltyP1 -= if (EAST === penalizedPlayerInitialPosition) penaltyPoints else -noPenalizedPlayerPoints
        penaltyP2 -= if (SOUTH === penalizedPlayerInitialPosition) penaltyPoints else -noPenalizedPlayerPoints
        penaltyP3 -= if (WEST === penalizedPlayerInitialPosition) penaltyPoints else -noPenalizedPlayerPoints
        penaltyP4 -= if (NORTH === penalizedPlayerInitialPosition) penaltyPoints else -noPenalizedPlayerPoints
    }
    
    fun applyAllPlayersPenalties() {
        pointsP1 += penaltyP1
        pointsP2 += penaltyP2
        pointsP3 += penaltyP3
        pointsP4 += penaltyP4
    }
    
    fun cancelAllPlayersPenalties() {
        penaltyP1 = 0
        penaltyP2 = 0
        penaltyP3 = 0
        penaltyP4 = 0
    }
    
    fun isPenalizedPlayer(playerInitialPosition: TableWinds): Boolean {
        return when (playerInitialPosition) {
            EAST -> penaltyP1 < 0
            SOUTH -> penaltyP2 < 0
            WEST -> penaltyP3 < 0
            else -> penaltyP4 < 0
        }
    }
    
    fun getPenaltyPointsFromInitialPlayerPosition(playerInitialPosition: TableWinds): Int {
        return when (playerInitialPosition) {
            EAST -> penaltyP1
            SOUTH -> penaltyP2
            WEST -> penaltyP3
            else -> penaltyP4
        }
    }
}