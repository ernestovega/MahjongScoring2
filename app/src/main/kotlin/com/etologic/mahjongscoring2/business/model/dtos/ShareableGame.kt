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
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbGame
import kotlinx.serialization.Serializable
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Serializable
data class PortableGame(
    val gameName: String,
    val startDate: String,
    val endDate: String,
    val nameP1: String,
    val nameP2: String,
    val nameP3: String,
    val nameP4: String,
    val rounds: List<PortableRound> = emptyList(),
)

fun UiGame.toPortableGame() = PortableGame(
    gameName = gameName,
    startDate = startDate.toString(),
    endDate = endDate?.toString().orEmpty(),
    nameP1 = nameP1,
    nameP2 = nameP2,
    nameP3 = nameP3,
    nameP4 = nameP4,
    rounds = uiRounds.map { it.toPortableRound() },
)

fun PortableGame.toDbGame(gameId: GameId): DbGame =
    DbGame(
        gameId = gameId,
        nameP1 = nameP1,
        nameP2 = nameP2,
        nameP3 = nameP3,
        nameP4 = nameP4,
        startDate = startDate.toDate(),
        endDate = endDate.toDate(),
        gameName = gameName,
    )

private fun String.toDate(): Date =
    SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault()).let { dateFormat ->
        try {
            dateFormat.parse(this) ?: Date()
        } catch (e: ParseException) {
            Date()
        }
    }
