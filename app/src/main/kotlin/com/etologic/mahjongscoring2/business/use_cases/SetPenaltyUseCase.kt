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
package com.etologic.mahjongscoring2.business.use_cases

import com.etologic.mahjongscoring2.business.model.dtos.PenaltyData
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.model.entities.UiRound
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.data_source.repositories.RoundsRepository
import javax.inject.Inject

class SetPenaltyUseCase @Inject constructor(
    private val roundsRepository: RoundsRepository,
) {
    suspend operator fun invoke(round: UiRound, penaltyData: PenaltyData): Result<Boolean> {
        round.updatePenalties(penaltyData)
        return roundsRepository.updateOne(round.dbRound)
    }

    private fun UiRound.updatePenalties(penaltyData: PenaltyData) {
        if (penaltyData.isDivided) {
            this.setAllPlayersPenaltyPoints(penaltyData.penalizedPlayerInitialSeat, penaltyData.points)
        } else {
            this.setPlayerPenaltyPoints(penaltyData.penalizedPlayerInitialSeat, penaltyData.points)
        }
    }

    private fun UiRound.setPlayerPenaltyPoints(penalizedPlayerInitialPosition: TableWinds, penaltyPoints: Int) {
        when (penalizedPlayerInitialPosition) {
            TableWinds.EAST -> this.dbRound.penaltyP1 -= penaltyPoints
            TableWinds.SOUTH -> this.dbRound.penaltyP2 -= penaltyPoints
            TableWinds.WEST -> this.dbRound.penaltyP3 -= penaltyPoints
            else -> this.dbRound.penaltyP4 -= penaltyPoints
        }
    }

    private fun UiRound.setAllPlayersPenaltyPoints(penalizedPlayerInitialSeat: TableWinds, penaltyPoints: Int) {
        val noPenalizedPlayerPoints = UiGame.getPenaltyOtherPlayersPoints(penaltyPoints)
        this.dbRound.penaltyP1 += if (TableWinds.EAST === penalizedPlayerInitialSeat) -penaltyPoints else noPenalizedPlayerPoints
        this.dbRound.penaltyP2 += if (TableWinds.SOUTH === penalizedPlayerInitialSeat) -penaltyPoints else noPenalizedPlayerPoints
        this.dbRound.penaltyP3 += if (TableWinds.WEST === penalizedPlayerInitialSeat) -penaltyPoints else noPenalizedPlayerPoints
        this.dbRound.penaltyP4 += if (TableWinds.NORTH === penalizedPlayerInitialSeat) -penaltyPoints else noPenalizedPlayerPoints
    }
}
