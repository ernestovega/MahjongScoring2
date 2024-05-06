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
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.model.entities.UiRound
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.data_source.repositories.RoundsRepository
import javax.inject.Inject

class HuSelfPickUseCase @Inject constructor(
    private val roundsRepository: RoundsRepository,
    private val endRoundUseCase: EndRoundUseCase,
) {

    suspend operator fun invoke(uiGame: UiGame, huData: HuData): Result<Boolean> =
        roundsRepository.updateOne(uiGame.currentRound.applyHuSelfPick(huData).dbRound)
            .onSuccess { endRoundUseCase(uiGame) }

    private fun UiRound.applyHuSelfPick(huData: HuData): UiRound = apply {
        this.dbRound.winnerInitialSeat = huData.winnerInitialSeat
        this.dbRound.discarderInitialSeat = TableWinds.NONE
        this.dbRound.handPoints = huData.points
        this.dbRound.pointsP1 += calculateSelfPickSeatPoints(TableWinds.EAST, huData)
        this.dbRound.pointsP2 += calculateSelfPickSeatPoints(TableWinds.SOUTH, huData)
        this.dbRound.pointsP3 += calculateSelfPickSeatPoints(TableWinds.WEST, huData)
        this.dbRound.pointsP4 += calculateSelfPickSeatPoints(TableWinds.NORTH, huData)
    }

    private fun calculateSelfPickSeatPoints(seat: TableWinds, huData: HuData): Int =
        if (seat == huData.winnerInitialSeat) {
            UiGame.getHuSelfPickWinnerPoints(huData.points)
        } else {
            UiGame.getHuSelfPickDiscarderPoints(huData.points)
        }
}
