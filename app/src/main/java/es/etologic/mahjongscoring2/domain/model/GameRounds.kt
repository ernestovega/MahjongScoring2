package es.etologic.mahjongscoring2.domain.model

import es.etologic.mahjongscoring2.domain.model.enums.TableWinds
import es.etologic.mahjongscoring2.domain.model.enums.TableWinds.*

open class GameRounds {
    
    companion object {
        
        fun getTotalScoreP1(rounds: List<Round>): Int {
            var totalPoints = 0
            for (round in rounds) totalPoints += round.pointsP1
            return totalPoints
        }
        
        fun getTotalScoreP2(rounds: List<Round>): Int {
            var totalPoints = 0
            for (round in rounds) totalPoints += round.pointsP2
            return totalPoints
        }
        
        fun getTotalScoreP3(rounds: List<Round>): Int {
            var totalPoints = 0
            for (round in rounds) totalPoints += round.pointsP3
            return totalPoints
        }
        
        fun getTotalScoreP4(rounds: List<Round>): Int {
            var totalPoints = 0
            for (round in rounds) totalPoints += round.pointsP4
            return totalPoints
        }
        
        fun getEastPlayerCurrentSeat(roundId: Int): TableWinds {
            return when (roundId) {
                1, 2, 3, 4 -> EAST
                5, 6, 7, 8 -> SOUTH
                9, 10, 11, 12 -> WEST
                13, 14, 15, 16 -> NORTH
                else -> EAST
            }
        }
        
        fun getSouthSeatPlayerByRound(roundId: Int): TableWinds {
            return when (roundId) {
                1, 2, 3, 4 -> SOUTH
                5, 6, 7, 8 -> EAST
                9, 10, 11, 12 -> NORTH
                13, 14, 15, 16 -> WEST
                else -> SOUTH
            }
        }
        
        fun getWestSeatPlayerByRound(roundId: Int): TableWinds {
            return when (roundId) {
                1, 2, 3, 4 -> WEST
                5, 6, 7, 8 -> NORTH
                9, 10, 11, 12 -> SOUTH
                13, 14, 15, 16 -> EAST
                else -> WEST
            }
        }
        
        fun getNorthSeatPlayerByRound(roundId: Int): TableWinds {
            return when (roundId) {
                1, 2, 3, 4 -> NORTH
                5, 6, 7, 8 -> WEST
                9, 10, 11, 12 -> EAST
                13, 14, 15, 16 -> SOUTH
                else -> NORTH
            }
        }
        
        fun getPlayerInitialSeatByCurrentSeat(seatPosition: TableWinds, roundId: Int): TableWinds {
            return when (roundId) {
                1, 2, 3, 4 -> getPlayerInitialPositionBySeatInRoundEast(seatPosition)
                5, 6, 7, 8 -> getPlayerInitialPositionBySeatInRoundSouth(seatPosition)
                9, 10, 11, 12 -> getPlayerInitialPositionBySeatInRoundWest(seatPosition)
                13, 14, 15, 16 -> getPlayerInitialPositionBySeatInRoundNorth(seatPosition)
                else -> getPlayerInitialPositionBySeatInRoundNorth(seatPosition)
            }
        }
        
        private fun getPlayerInitialPositionBySeatInRoundEast(seatPosition: TableWinds): TableWinds {
            return when (seatPosition) {
                EAST -> EAST
                SOUTH -> SOUTH
                WEST -> WEST
                NORTH -> NORTH
                else -> NORTH
            }
        }
        
        private fun getPlayerInitialPositionBySeatInRoundSouth(seatPosition: TableWinds): TableWinds {
            return when (seatPosition) {
                EAST -> SOUTH
                SOUTH -> EAST
                WEST -> NORTH
                NORTH -> WEST
                else -> WEST
            }
        }
        
        private fun getPlayerInitialPositionBySeatInRoundWest(seatPosition: TableWinds): TableWinds {
            return when (seatPosition) {
                EAST -> WEST
                SOUTH -> NORTH
                WEST -> SOUTH
                NORTH -> EAST
                else -> EAST
            }
        }
        
        private fun getPlayerInitialPositionBySeatInRoundNorth(seatPosition: TableWinds): TableWinds {
            return when (seatPosition) {
                EAST -> NORTH
                SOUTH -> WEST
                WEST -> EAST
                NORTH -> SOUTH
                else -> SOUTH
            }
        }
    }
}
