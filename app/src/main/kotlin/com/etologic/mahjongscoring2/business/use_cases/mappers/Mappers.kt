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

package com.etologic.mahjongscoring2.business.use_cases.mappers

import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.model.entities.UiRound
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbGame
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbRound

fun DbGame.toUiGame(dbRounds: List<DbRound>): UiGame =
    UiGame(
        gameId = this.gameId,
        nameP1 = this.nameP1,
        nameP2 = this.nameP2,
        nameP3 = this.nameP3,
        nameP4 = this.nameP4,
        startDate = this.startDate,
        endDate = this.endDate,
        gameName = this.gameName,
        uiRounds = dbRounds.map { dbRound ->
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