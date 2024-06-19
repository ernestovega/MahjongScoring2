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

package com.etologic.mahjongscoring2.app.screens.old_games

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.etologic.mahjongscoring2.app.base.BaseViewModel
import com.etologic.mahjongscoring2.business.model.entities.GameId
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.model.enums.ShareGameOptions
import com.etologic.mahjongscoring2.business.model.enums.ShareGameOptions.CSV
import com.etologic.mahjongscoring2.business.model.enums.ShareGameOptions.JSON
import com.etologic.mahjongscoring2.business.model.enums.ShareGameOptions.TEXT
import com.etologic.mahjongscoring2.business.use_cases.CreateGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.DeleteGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.ExportGameToCsvUseCase
import com.etologic.mahjongscoring2.business.use_cases.ExportGameToJsonUseCase
import com.etologic.mahjongscoring2.business.use_cases.ExportGameToTextUseCase
import com.etologic.mahjongscoring2.business.use_cases.ExportGamesToJsonUseCase
import com.etologic.mahjongscoring2.business.use_cases.GetAllGamesFlowUseCase
import com.etologic.mahjongscoring2.business.use_cases.GetIsDiffCalcsFeatureEnabledUseCase
import com.etologic.mahjongscoring2.business.use_cases.ImportGamesFromJsonUseCase
import com.etologic.mahjongscoring2.business.use_cases.SaveIsDiffCalcsFeatureEnabledUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

sealed interface OldGamesUiState {
    data object Loading : OldGamesUiState
    data class Loaded(
        val oldGamesList: List<UiGame>,
        val isDiffsCalcsFeatureEnabled: Boolean,
    ) : OldGamesUiState
    data class Error(val throwable: Throwable) : OldGamesUiState
}

@HiltViewModel
class OldGamesViewModel @Inject constructor(
    getAllGamesFlowUseCase: GetAllGamesFlowUseCase,
    private val createGameUseCase: CreateGameUseCase,
    private val deleteGameUseCase: DeleteGameUseCase,
    private val exportGameToTextUseCase: ExportGameToTextUseCase,
    private val exportGameToCsvUseCase: ExportGameToCsvUseCase,
    private val exportGameToJsonUseCase: ExportGameToJsonUseCase,
    private val exportGamesToJsonUseCase: ExportGamesToJsonUseCase,
    private val importGamesFromJsonUseCase: ImportGamesFromJsonUseCase,
    getIsDiffCalcsFeatureEnabledUseCase: GetIsDiffCalcsFeatureEnabledUseCase,
    private val saveIsDiffCalcsFeatureEnabledUseCase: SaveIsDiffCalcsFeatureEnabledUseCase,
) : BaseViewModel() {

    private val isDiffsCalcsFeatureEnabledFlow: StateFlow<Boolean> =
        getIsDiffCalcsFeatureEnabledUseCase.invoke()
            .stateIn(viewModelScope, SharingStarted.Lazily, false)

    val oldGamesUiStateFlow: StateFlow<OldGamesUiState> =
        combine(
            errorFlow,
            getAllGamesFlowUseCase.invoke(),
            isDiffsCalcsFeatureEnabledFlow,
        ) { error, oldGames, isDiffsCalcsFeatureEnabled ->
            if (error != null) {
               OldGamesUiState.Error(error)
            } else {
               OldGamesUiState.Loaded(oldGames, isDiffsCalcsFeatureEnabled)
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, OldGamesUiState.Loading)

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

    fun deleteGame(gameId: GameId) {
        viewModelScope.launch {
            deleteGameUseCase.invoke(gameId)
                .onFailure { showError(it) }
        }
    }

    fun shareGame(
        gameId: GameId,
        option: ShareGameOptions,
        directory: File?,
        showShareText: (String) -> Unit,
        showShareFiles: (Array<File>) -> Unit,
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                when (option) {
                    TEXT -> exportGameToTextUseCase.invoke(gameId)
                        .fold({ showShareText.invoke(it) }, ::showError)

                    CSV -> exportGameToCsvUseCase.invoke(gameId, directory)
                        .fold({ showShareFiles.invoke(arrayOf(it)) }, ::showError)

                    JSON -> exportGameToJsonUseCase.invoke(gameId, directory)
                        .fold({ showShareFiles.invoke(arrayOf(it)) }, ::showError)
                }
            }
        }
    }

    fun exportGames(directory: File?, showShareFiles: (Array<File>) -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                exportGamesToJsonUseCase.invoke(directory)
                    .fold({ showShareFiles.invoke(it) }, ::showError)
            }
        }
    }

    fun importGames(uri: Uri, getContentResolver: () -> ContentResolver) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                importGamesFromJsonUseCase.invoke(uri, getContentResolver)
                    .fold({}, ::showError)
            }
        }
    }

    fun toggleDiffsFeature(isEnabled: Boolean) {
        viewModelScope.launch {
            saveIsDiffCalcsFeatureEnabledUseCase.invoke(isEnabled)
        }
    }
}
