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
package com.etologic.mahjongscoring2.app.screens

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.etologic.mahjongscoring2.app.base.BaseViewModel
import com.etologic.mahjongscoring2.business.model.entities.GameId
import com.etologic.mahjongscoring2.business.model.entities.UiGame.Companion.NOT_SET_GAME_ID
import com.etologic.mahjongscoring2.business.model.enums.ShareGameOptions
import com.etologic.mahjongscoring2.business.model.enums.ShareGameOptions.CSV
import com.etologic.mahjongscoring2.business.model.enums.ShareGameOptions.JSON
import com.etologic.mahjongscoring2.business.model.enums.ShareGameOptions.TEXT
import com.etologic.mahjongscoring2.business.use_cases.CreateGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.ExportGameToCsvUseCase
import com.etologic.mahjongscoring2.business.use_cases.ExportGameToJsonUseCase
import com.etologic.mahjongscoring2.business.use_cases.ExportGameToTextUseCase
import com.etologic.mahjongscoring2.business.use_cases.ExportGamesToJsonUseCase
import com.etologic.mahjongscoring2.business.use_cases.GetIsDiffCalcsFeatureEnabledUseCase
import com.etologic.mahjongscoring2.business.use_cases.ImportGamesFromJsonUseCase
import com.etologic.mahjongscoring2.business.use_cases.SaveIsDiffCalcsFeatureEnabledUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val createGameUseCase: CreateGameUseCase,
    private val exportGameToTextUseCase: ExportGameToTextUseCase,
    private val exportGameToCsvUseCase: ExportGameToCsvUseCase,
    private val exportGameToJsonUseCase: ExportGameToJsonUseCase,
    private val exportGamesToJsonUseCase: ExportGamesToJsonUseCase,
    private val importGamesFromJsonUseCase: ImportGamesFromJsonUseCase,
    getIsDiffCalcsFeatureEnabledUseCase: GetIsDiffCalcsFeatureEnabledUseCase,
    private val saveIsDiffCalcsFeatureEnabledUseCase: SaveIsDiffCalcsFeatureEnabledUseCase,
) : BaseViewModel() {

    var activeGameId: GameId = NOT_SET_GAME_ID
        set(value) {
            field = value
            if (value == NOT_SET_GAME_ID) {
                viewModelScope.launch { _currentGameNameFlow.emit("") }
            }
        }

    private val _exportedTextFlow = MutableStateFlow<String?>(null)
    val exportedTextFlow: Flow<String> = _exportedTextFlow.filterNotNull()
    private val _exportedFilesFlow = MutableStateFlow<List<File>?>(null)
    val exportedFilesFlow: Flow<List<File>> = _exportedFilesFlow.filterNotNull()
    private val _currentGameNameFlow = MutableStateFlow<String?>(null)
    val currentGameNameFlow: Flow<String?> = _currentGameNameFlow

    val isDiffsCalcsFeatureEnabledFlow: StateFlow<Boolean> = getIsDiffCalcsFeatureEnabledUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    fun createGame(
        gameName: String,
        nameP1: String,
        nameP2: String,
        nameP3: String,
        nameP4: String,
        onSuccess: (GameId) -> Unit,
    ) {
        viewModelScope.launch {
            createGameUseCase.invoke(gameName, nameP1, nameP2, nameP3, nameP4)
                .fold(onSuccess, ::showError)
        }
    }

    fun setCurrentGameName(gameName: String?) {
        viewModelScope.launch { _currentGameNameFlow.emit(gameName) }
    }

    fun shareGame(gameId: GameId, option: ShareGameOptions, getExternalFilesDir: () -> File?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                when (option) {
                    TEXT -> exportGameToTextUseCase.invoke(gameId)
                        .fold({ _exportedTextFlow.emit(it) }, ::showError)

                    CSV -> exportGameToCsvUseCase.invoke(gameId, getExternalFilesDir)
                        .fold({ _exportedFilesFlow.emit(it) }, ::showError)

                    JSON -> exportGameToJsonUseCase.invoke(gameId, getExternalFilesDir)
                        .fold({ _exportedFilesFlow.emit(it) }, ::showError)
                }
            }
        }
    }

    fun exportGames(getExternalFilesDir: () -> File?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                exportGamesToJsonUseCase.invoke(getExternalFilesDir)
                    .fold({ _exportedFilesFlow.emit(it) }, ::showError)
            }
        }
    }

    fun toggleDiffsFeature(isEnabled: Boolean) {
        viewModelScope.launch {
            saveIsDiffCalcsFeatureEnabledUseCase(isEnabled)
        }
    }

    fun importGames(uri: Uri, getContentResolver: () -> ContentResolver) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                importGamesFromJsonUseCase(uri, getContentResolver)
                    .fold({}, ::showError)
            }
        }
    }
}