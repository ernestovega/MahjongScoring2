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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.etologic.mahjongscoring2.business.model.entities.GameId
import com.etologic.mahjongscoring2.business.model.entities.UiGame.Companion.NOT_SET_GAME_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // Kept in SavedStateHandle so the active game survives process death.
    val activeGameId: StateFlow<GameId> = savedStateHandle.getStateFlow(KEY_ACTIVE_GAME_ID, NOT_SET_GAME_ID)

    fun updateActiveGameId(gameId: GameId) {
        savedStateHandle[KEY_ACTIVE_GAME_ID] = gameId
    }

    private companion object {
        const val KEY_ACTIVE_GAME_ID = "active_game_id"
    }

}
