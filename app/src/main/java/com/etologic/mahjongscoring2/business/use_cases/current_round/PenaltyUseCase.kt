/*
    Copyright (C) 2020  Ernesto Vega de la Iglesia Soria
 
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.etologic.mahjongscoring2.business.use_cases.current_round

import com.etologic.mahjongscoring2.business.model.dtos.PenaltyData
import com.etologic.mahjongscoring2.business.model.entities.Table
import com.etologic.mahjongscoring2.data_source.repositories.CurrentTableRepository
import com.etologic.mahjongscoring2.data_source.repositories.RoundsRepository
import com.etologic.mahjongscoring2.data_source.repositories.TablesRepository
import io.reactivex.Single
import javax.inject.Inject

class PenaltyUseCase @Inject
constructor(
    private val currentTableRepository: CurrentTableRepository,
    private val tablesRepository: TablesRepository,
    private val roundsRepository: RoundsRepository
) {
    
    internal fun penalty(penaltyData: PenaltyData): Single<Table> =
        currentTableRepository.get()
            .flatMap { currentTable ->
                val currentRound = currentTable.rounds.last()
                if (penaltyData.isDivided)
                    currentRound.setAllPlayersPenaltyPoints(
                        currentTable.getPlayerInitialSeatByCurrentSeat(penaltyData.penalizedPlayerCurrentSeat, currentTable.rounds.size),
                        penaltyData.points
                    )
                else
                    currentRound.setPlayerPenaltyPoints(
                        currentTable.getPlayerInitialSeatByCurrentSeat(penaltyData.penalizedPlayerCurrentSeat, currentTable.rounds.size),
                        penaltyData.points
                    )
                roundsRepository.updateOne(currentRound)
                    .flatMap { tablesRepository.getTable(currentRound.gameId) }
                    .flatMap { currentTableRepository.set(it) }
            }
    
    internal fun cancelPenalties(): Single<Table> =
        currentTableRepository.get()
            .flatMap { currentGameWithRounds ->
                val currentRound = currentGameWithRounds.rounds.last()
                currentRound.cancelAllPlayersPenalties()
                roundsRepository.updateOne(currentRound)
                    .flatMap { tablesRepository.getTable(currentRound.gameId) }
                    .flatMap { currentTableRepository.set(it) }
            }
}
