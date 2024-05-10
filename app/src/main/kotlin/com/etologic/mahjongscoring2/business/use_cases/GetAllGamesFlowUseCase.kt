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
import com.etologic.mahjongscoring2.business.model.entities.UiRound
import com.etologic.mahjongscoring2.data_source.repositories.GamesRepository
import com.etologic.mahjongscoring2.data_source.repositories.RoundsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

class GetAllGamesFlowUseCase @Inject constructor(
    private val gamesRepository: GamesRepository,
    private val roundsRepository: RoundsRepository,
) {
    operator fun invoke(): Flow<List<UiGame>> =
        combine(
            gamesRepository.getAllFlow(),
            roundsRepository.getAllFlow(),
        ) { dbGames, dbRounds ->
            dbGames.map { dbGame ->
                val gameRounds = dbRounds.filter { round -> round.gameId == dbGame.gameId }
                if (gameRounds.isEmpty()) {
                    return@combine null
                } else {
                    UiGame(
                        gameId = dbGame.gameId,
                        nameP1 = dbGame.nameP1,
                        nameP2 = dbGame.nameP2,
                        nameP3 = dbGame.nameP3,
                        nameP4 = dbGame.nameP4,
                        startDate = dbGame.startDate,
                        endDate = dbGame.endDate,
                        gameName = dbGame.gameName,
                        uiRounds = gameRounds.map { dbRound ->
                            UiRound(
                                gameId = dbRound.gameId,
                                roundId = dbRound.roundId,
                                winnerInitialSeat = dbRound.winnerInitialSeat,
                                discarderInitialSeat = dbRound.discarderInitialSeat,
                                handPoints = dbRound.handPoints,
                                penaltyP1 = dbRound.penaltyP1,
                                penaltyP2 = dbRound.penaltyP2,
                                penaltyP3 = dbRound.penaltyP3,
                                penaltyP4 = dbRound.penaltyP4,
                            )
                        },
                    )
                }
            }
        }
            .filterNotNull()
}
