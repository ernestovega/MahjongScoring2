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

package com.etologic.mahjongscoring2.business.use_cases

import androidx.annotation.VisibleForTesting
import com.etologic.mahjongscoring2.business.model.dtos.PortableGame
import com.etologic.mahjongscoring2.business.model.dtos.toPortableGame
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.model.exceptions.GamesNotFoundException
import com.etologic.mahjongscoring2.business.use_cases.utils.writeToCsvFile
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject

class ExportGamesToJsonUseCase @Inject constructor(
    private val getAllGamesFlowUseCase: GetAllGamesFlowUseCase,
) {
    suspend operator fun invoke(directory: File?): Result<Array<File>> =
        runCatching {
            val uiGames = getAllGamesFlowUseCase.invoke().firstOrNull() ?: throw GamesNotFoundException()
            val jsonText = jsonFrom(uiGames)
            val fileName = "MahjongMadrid2_DataBase".plus(".json")
            val jsonFile = writeToCsvFile(fileName, jsonText, directory)
            arrayOf(jsonFile)
        }

    @VisibleForTesting
    fun jsonFrom(uiGames: List<UiGame>): String =
        Json.encodeToString(
            serializer = ListSerializer(PortableGame.serializer()),
            value = uiGames.map { uiGame -> uiGame.toPortableGame() }
        )
}