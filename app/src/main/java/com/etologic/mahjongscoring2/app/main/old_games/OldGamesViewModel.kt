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
package com.etologic.mahjongscoring2.app.main.old_games

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.etologic.mahjongscoring2.app.base.BaseViewModel
import com.etologic.mahjongscoring2.business.model.entities.UIGame
import com.etologic.mahjongscoring2.business.use_cases.CreateGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.DeleteGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.GetAllGamesFlowUseCase
import com.etologic.mahjongscoring2.data_source.model.GameId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OldGamesViewModel @Inject constructor(
    private val getAllGamesFlowUseCase: GetAllGamesFlowUseCase,
    private val createGameUseCase: CreateGameUseCase,
    private val deleteGameUseCase: DeleteGameUseCase,
) : BaseViewModel() {

    private val _allGames = MutableLiveData<List<UIGame>>()
    fun getGames(): LiveData<List<UIGame>> = _allGames
    private val _createdGameId = MutableLiveData<GameId>()
    fun getCreatedGameId(): LiveData<GameId> = _createdGameId

    private lateinit var gamesFlow: SharedFlow<List<UIGame>>

    fun loadGames() {
        gamesFlow = getAllGamesFlowUseCase()
            .onEach(_allGames::postValue)
            .shareIn(viewModelScope, SharingStarted.Eagerly)
    }

    fun createGame(playersNames: Array<String>) {
        viewModelScope.launch {
            createGameUseCase(playersNames)
                .fold({ _createdGameId.postValue(it) }, { showError(it) })
        }
    }

    fun deleteGame(gameId: GameId) {
        viewModelScope.launch {
            deleteGameUseCase(gameId)
                .onFailure { showError(it) }
        }
    }
}
