/*
 *     Copyright © 2024  Ernesto Vega de la Iglesia Soria
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
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.EAST
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NORTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.SOUTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.WEST
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbRound
import com.etologic.mahjongscoring2.data_source.repositories.rounds.RoundsRepository
import javax.inject.Inject

class SetPenaltyUseCase @Inject constructor(
    private val roundsRepository: RoundsRepository,
) {
    suspend operator fun invoke(uiRound: UiRound, penaltyData: PenaltyData): Result<Boolean> =
        roundsRepository.updateOne(
            if (penaltyData.isDivided) {
                getDbRoundApplyingAllPlayersPenaltyPoints(uiRound, penaltyData.penalizedPlayerInitialSeat, penaltyData.points)
            } else {
                getDbRoundApplyingOnePlayerPenaltyPoints(uiRound, penaltyData.penalizedPlayerInitialSeat, penaltyData.points)
            }
        )

    private fun getDbRoundApplyingAllPlayersPenaltyPoints(
        uiRound: UiRound,
        penalizedPlayerInitialSeat: TableWinds,
        penaltyPoints: Int
    ): DbRound =
        with(uiRound) {
            UiGame.getPenaltyOtherPlayersPoints(penaltyPoints).let { noPenalizedPlayerPoints ->
                DbRound(
                    gameId = gameId,
                    roundId = roundId,
                    winnerInitialSeat = winnerInitialSeat,
                    discarderInitialSeat = discarderInitialSeat,
                    handPoints = handPoints,
                    penaltyP1 = penaltyP1 + if (EAST === penalizedPlayerInitialSeat) -penaltyPoints else noPenalizedPlayerPoints,
                    penaltyP2 = penaltyP2 + if (SOUTH === penalizedPlayerInitialSeat) -penaltyPoints else noPenalizedPlayerPoints,
                    penaltyP3 = penaltyP3 + if (WEST === penalizedPlayerInitialSeat) -penaltyPoints else noPenalizedPlayerPoints,
                    penaltyP4 = penaltyP4 + if (NORTH === penalizedPlayerInitialSeat) -penaltyPoints else noPenalizedPlayerPoints,
                )
            }
        }

    private fun getDbRoundApplyingOnePlayerPenaltyPoints(
        uiRound: UiRound,
        penalizedPlayerInitialPosition: TableWinds,
        penaltyPoints: Int
    ): DbRound =
        with(uiRound) {
            DbRound(
                gameId = gameId,
                roundId = roundId,
                winnerInitialSeat = winnerInitialSeat,
                discarderInitialSeat = discarderInitialSeat,
                handPoints = handPoints,
                penaltyP1 = if (penalizedPlayerInitialPosition == EAST) penaltyP1 - penaltyPoints else penaltyP1,
                penaltyP2 = if (penalizedPlayerInitialPosition == SOUTH) penaltyP2 - penaltyPoints else penaltyP2,
                penaltyP3 = if (penalizedPlayerInitialPosition == WEST) penaltyP3 - penaltyPoints else penaltyP3,
                penaltyP4 = if (penalizedPlayerInitialPosition == NORTH) penaltyP4 - penaltyPoints else penaltyP4,
            )
        }
}
