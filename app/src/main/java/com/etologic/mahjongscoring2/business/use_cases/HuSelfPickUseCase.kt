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

import com.etologic.mahjongscoring2.business.model.dtos.HuData
import com.etologic.mahjongscoring2.business.model.entities.Round
import com.etologic.mahjongscoring2.business.model.entities.UIGame
import com.etologic.mahjongscoring2.business.model.entities.applyPenaltiesAndMarkRoundAsEnded
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.data_source.repositories.RoundsRepository
import javax.inject.Inject

class HuSelfPickUseCase @Inject constructor(
    private val roundsRepository: RoundsRepository,
    private val resumeGameUseCase: ResumeGameUseCase,
) {
    suspend operator fun invoke(round: Round, huData: HuData): Result<Boolean> {
        round.finishByHuSelfPick(huData)
        return roundsRepository.updateOne(round)
            .also { resumeGameUseCase(round.gameId, round.roundNumber) }
    }

    private fun Round.finishByHuSelfPick(huData: HuData) {
        this.winnerInitialSeat = huData.winnerInitialSeat
        this.handPoints = huData.points
        this.pointsP1 += calculateSelfPickSeatPoints(TableWinds.EAST, huData)
        this.pointsP2 += calculateSelfPickSeatPoints(TableWinds.SOUTH, huData)
        this.pointsP3 += calculateSelfPickSeatPoints(TableWinds.WEST, huData)
        this.pointsP4 += calculateSelfPickSeatPoints(TableWinds.NORTH, huData)
        this.applyPenaltiesAndMarkRoundAsEnded()
    }

    private fun calculateSelfPickSeatPoints(seat: TableWinds, huData: HuData): Int =
        if (seat == huData.winnerInitialSeat) {
            UIGame.getHuSelfPickWinnerPoints(huData.points)
        } else {
            UIGame.getHuSelfPickDiscarderPoints(huData.points)
        }
}
