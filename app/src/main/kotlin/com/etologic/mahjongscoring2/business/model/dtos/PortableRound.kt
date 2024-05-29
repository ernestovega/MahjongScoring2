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

package com.etologic.mahjongscoring2.business.model.dtos

import com.etologic.mahjongscoring2.business.model.entities.GameId
import com.etologic.mahjongscoring2.business.model.entities.UiRound
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbRound
import kotlinx.serialization.Serializable

@Serializable
data class PortableRound(
    val roundNumber: Int,
    val winnerInitialSeat: Int?,
    val discarderInitialSeat: Int?,
    val handPoints: Int,
    val pointsP1: Int,
    val pointsP2: Int,
    val pointsP3: Int,
    val pointsP4: Int,
    val penaltyP1: Int,
    val penaltyP2: Int,
    val penaltyP3: Int,
    val penaltyP4: Int,
)

fun UiRound.toPortableRound() = PortableRound(
    roundNumber = roundNumber,
    winnerInitialSeat = winnerInitialSeat?.code,
    discarderInitialSeat = discarderInitialSeat?.code,
    handPoints = handPoints,
    pointsP1 = pointsP1,
    pointsP2 = pointsP2,
    pointsP3 = pointsP3,
    pointsP4 = pointsP4,
    penaltyP1 = penaltyP1,
    penaltyP2 = penaltyP2,
    penaltyP3 = penaltyP3,
    penaltyP4 = penaltyP4
)

fun List<PortableRound>.toDbRounds(gameId: GameId): List<DbRound> =
    this.map { it.toDbRound(gameId) }

private fun PortableRound.toDbRound(gameId: GameId): DbRound =
    DbRound(
        gameId = gameId,
        winnerInitialSeat = TableWinds.from(winnerInitialSeat),
        discarderInitialSeat = TableWinds.from(discarderInitialSeat),
        handPoints = handPoints,
        penaltyP1 = penaltyP1,
        penaltyP2 = penaltyP2,
        penaltyP3 = penaltyP3,
        penaltyP4 = penaltyP4,
    )