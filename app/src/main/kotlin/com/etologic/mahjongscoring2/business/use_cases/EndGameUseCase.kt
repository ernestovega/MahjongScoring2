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

import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.*
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbGame
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbRound
import com.etologic.mahjongscoring2.data_source.repositories.GamesRepository
import com.etologic.mahjongscoring2.data_source.repositories.RoundsRepository
import java.util.Date
import javax.inject.Inject

class EndGameUseCase @Inject constructor(
    private val roundsRepository: RoundsRepository,
    private val gamesRepository: GamesRepository,
) {
    suspend operator fun invoke(uiGame: UiGame): Result<Boolean> =
        if (uiGame.currentRound.isNotEnded()) {
            if (uiGame.currentRound.areTherePenalties()) {
                // Apply draw
                roundsRepository.updateOne(
                    DbRound(
                        gameId = uiGame.currentRound.gameId,
                        roundId = uiGame.currentRound.roundId,
                        winnerInitialSeat = NONE,
                        discarderInitialSeat = NONE,
                        handPoints = uiGame.currentRound.handPoints,
                        penaltyP1 = uiGame.currentRound.penaltyP1,
                        penaltyP2 = uiGame.currentRound.penaltyP2,
                        penaltyP3 = uiGame.currentRound.penaltyP3,
                        penaltyP4 = uiGame.currentRound.penaltyP4,
                    )
                )
            } else {
                roundsRepository.deleteOne(
                    gameId = uiGame.gameId,
                    roundId = uiGame.currentRound.roundId
                )
            }
        } else {
            Result.success(true)
        }
            .onSuccess {
                // Apply game end
                gamesRepository.updateOne(
                    DbGame(
                        gameId = uiGame.gameId,
                        nameP1 = uiGame.nameP1,
                        nameP2 = uiGame.nameP2,
                        nameP3 = uiGame.nameP3,
                        nameP4 = uiGame.nameP4,
                        startDate = uiGame.startDate,
                        endDate = Date(),
                        gameName = uiGame.gameName,
                    )
                )
            }
}
