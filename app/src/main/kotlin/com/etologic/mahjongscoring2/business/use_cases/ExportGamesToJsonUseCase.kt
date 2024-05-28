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

import com.etologic.mahjongscoring2.app.utils.writeToCsvFile
import com.etologic.mahjongscoring2.business.model.dtos.PortableGame
import com.etologic.mahjongscoring2.business.model.dtos.toPortableGame
import com.etologic.mahjongscoring2.business.model.exceptions.GameNotFoundException
import com.etologic.mahjongscoring2.business.model.exceptions.GamesNotFoundException
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject

class ExportGamesToJsonUseCase @Inject constructor(
    private val getAllGamesFlowUseCase: GetAllGamesFlowUseCase
) {
    @Throws(GameNotFoundException::class)
    suspend operator fun invoke(getExternalFilesDir: () -> File?): Result<List<File>> =
        runCatching {
            val uiGames = getAllGamesFlowUseCase.invoke().firstOrNull() ?: throw GamesNotFoundException()
            val portableGames = uiGames.map { uiGame -> uiGame.toPortableGame() }
            val jsonText = Json.encodeToString(ListSerializer(PortableGame.serializer()), portableGames)
            val jsonFile = writeToCsvFile(
                fileName = "MahjongMadrid2_DataBase.json",
                fileText = jsonText,
                externalFilesDir = getExternalFilesDir.invoke(),
            )
            listOf(jsonFile)
        }
}