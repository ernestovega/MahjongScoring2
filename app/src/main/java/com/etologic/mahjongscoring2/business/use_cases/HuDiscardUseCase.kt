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
import com.etologic.mahjongscoring2.business.model.entities.applyPenalties
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.data_source.repositories.RoundsRepository
import javax.inject.Inject

class HuDiscardUseCase @Inject constructor(
    private val roundsRepository: RoundsRepository,
    private val endRoundUseCase: EndRoundUseCase,
) {
    suspend operator fun invoke(uiGame: UIGame, huData: HuData): Result<Boolean> =
        roundsRepository.updateOne(uiGame.currentRound.applyHuDiscard(huData))
            .onSuccess { endRoundUseCase(uiGame) }

    private fun Round.applyHuDiscard(huData: HuData): Round {
        this.winnerInitialSeat = huData.winnerInitialSeat
        this.discarderInitialSeat = huData.discarderInitialSeat!!
        this.handPoints = huData.points
        this.pointsP1 += calculateDiscardSeatPoints(TableWinds.EAST, huData)
        this.pointsP2 += calculateDiscardSeatPoints(TableWinds.SOUTH, huData)
        this.pointsP3 += calculateDiscardSeatPoints(TableWinds.WEST, huData)
        this.pointsP4 += calculateDiscardSeatPoints(TableWinds.NORTH, huData)
        return this.applyPenalties()
    }

    private fun calculateDiscardSeatPoints(seat: TableWinds, huData: HuData): Int =
        when (seat) {
            huData.winnerInitialSeat -> UIGame.getHuDiscardWinnerPoints(huData.points)
            huData.discarderInitialSeat -> UIGame.getHuDiscardDiscarderPoints(huData.points)
            else -> UIGame.POINTS_DISCARD_NEUTRAL_PLAYERS
        }
}
