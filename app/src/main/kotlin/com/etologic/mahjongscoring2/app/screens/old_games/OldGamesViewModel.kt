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

import androidx.lifecycle.viewModelScope
import com.etologic.mahjongscoring2.app.base.BaseViewModel
import com.etologic.mahjongscoring2.business.model.entities.GameId
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.use_cases.DeleteGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.GetAllGamesFlowUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface OldGamesUiState {
    data object Loading : OldGamesUiState
    data class Loaded(val oldGamesList: List<UiGame>) : OldGamesUiState
    data object Empty : OldGamesUiState
}

@HiltViewModel
class OldGamesViewModel @Inject constructor(
    getAllGamesFlowUseCase: GetAllGamesFlowUseCase,
    private val deleteGameUseCase: DeleteGameUseCase,
) : BaseViewModel() {

    val oldGamesUiState: StateFlow<OldGamesUiState> =
        getAllGamesFlowUseCase.invoke()
            .map { oldGames ->
                if (oldGames.isEmpty()) {
                    OldGamesUiState.Empty
                } else {
                    OldGamesUiState.Loaded(oldGames)
                }
            }
            .stateIn(viewModelScope, SharingStarted.Lazily, OldGamesUiState.Loading)

    fun deleteGame(gameId: GameId) {
        viewModelScope.launch {
            deleteGameUseCase(gameId)
                .onFailure { showError(it) }
        }
    }
}
