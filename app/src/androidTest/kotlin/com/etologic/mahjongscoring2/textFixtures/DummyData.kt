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

package com.etologic.mahjongscoring2.textFixtures

import com.etologic.mahjongscoring2.business.model.entities.GameId
import com.etologic.mahjongscoring2.business.model.entities.RoundId
import com.etologic.mahjongscoring2.business.model.entities.UiRound.Companion.NOT_SET_ROUND_ID
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbGame
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbRound
import java.util.Date

val dummyDbGame1 = DbGame(
    gameId = 1,
    nameP1 = "Cecil Jordan",
    nameP2 = "Julio Soto",
    nameP3 = "Alvaro Stevens",
    nameP4 = "Katie Russell",
    startDate = Date(1985, 12, 20, 8, 0),
    endDate = null,
    gameName = "Championship",
)

val dummyDbGame2 = DbGame(
    gameId = 2,
    nameP1 = "Zelma Thomas",
    nameP2 = "Kevin Freeman",
    nameP3 = "Erica Velez",
    nameP4 = "Ofelia Thompson",
    startDate = Date(1987, 11, 4, 20, 0),
    endDate = null,
    gameName = "Alana Witt Championship"
)

fun createOnGoingDbRound(
    gameId: GameId,
    roundId: RoundId = NOT_SET_ROUND_ID,
    penaltyP1: Int = 0,
    penaltyP2: Int = 0,
    penaltyP3: Int = 0,
    penaltyP4: Int = 0,
) = DbRound(
    gameId = gameId,
    roundId = roundId,
    winnerInitialSeat = null,
    discarderInitialSeat = null,
    handPoints = 0,
    penaltyP1 = penaltyP1,
    penaltyP2 = penaltyP2,
    penaltyP3 = penaltyP3,
    penaltyP4 = penaltyP4
)

fun createDrawDbRound(
    gameId: GameId,
    roundId: RoundId = NOT_SET_ROUND_ID,
    penaltyP1: Int = 0,
    penaltyP2: Int = 0,
    penaltyP3: Int = 0,
    penaltyP4: Int = 0,
) = DbRound(
    gameId = gameId,
    roundId = roundId,
    winnerInitialSeat = TableWinds.NONE,
    discarderInitialSeat = TableWinds.NONE,
    handPoints = 0,
    penaltyP1 = penaltyP1,
    penaltyP2 = penaltyP2,
    penaltyP3 = penaltyP3,
    penaltyP4 = penaltyP4
)

fun createHuSelfpickDbRound(
    gameId: GameId,
    roundId: RoundId = NOT_SET_ROUND_ID,
    winnerInitialSeat: TableWinds,
    penaltyP1: Int = 0,
    penaltyP2: Int = 0,
    penaltyP3: Int = 0,
    penaltyP4: Int = 0,
) = DbRound(
    gameId = gameId,
    roundId = roundId,
    winnerInitialSeat = winnerInitialSeat,
    discarderInitialSeat = TableWinds.NONE,
    handPoints = 0,
    penaltyP1 = penaltyP1,
    penaltyP2 = penaltyP2,
    penaltyP3 = penaltyP3,
    penaltyP4 = penaltyP4,
)

fun createHuDiscardDbRound(
    gameId: GameId,
    roundId: RoundId = NOT_SET_ROUND_ID,
    winnerInitialSeat: TableWinds,
    discarderInitialSeat: TableWinds,
    penaltyP1: Int = 0,
    penaltyP2: Int = 0,
    penaltyP3: Int = 0,
    penaltyP4: Int = 0,
) = DbRound(
    gameId = gameId,
    roundId = roundId,
    winnerInitialSeat = winnerInitialSeat,
    discarderInitialSeat = discarderInitialSeat,
    handPoints = 0,
    penaltyP1 = penaltyP1,
    penaltyP2 = penaltyP2,
    penaltyP3 = penaltyP3,
    penaltyP4 = penaltyP4
)