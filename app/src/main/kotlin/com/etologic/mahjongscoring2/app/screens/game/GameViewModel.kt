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

package com.etologic.mahjongscoring2.app.screens.game

import androidx.lifecycle.viewModelScope
import com.etologic.mahjongscoring2.app.base.BaseViewModel
import com.etologic.mahjongscoring2.app.screens.game.game_table.GameTableFragment
import com.etologic.mahjongscoring2.app.screens.game.game_table.GameTableFragment.GameTablePages.TABLE
import com.etologic.mahjongscoring2.business.model.dtos.HuData
import com.etologic.mahjongscoring2.business.model.dtos.PenaltyData
import com.etologic.mahjongscoring2.business.model.entities.GameId
import com.etologic.mahjongscoring2.business.model.entities.RoundId
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.model.entities.UiGame.Companion.NOT_SET_GAME_ID
import com.etologic.mahjongscoring2.business.model.enums.SeatOrientation
import com.etologic.mahjongscoring2.business.model.enums.SeatOrientation.DOWN
import com.etologic.mahjongscoring2.business.model.enums.SeatOrientation.OUT
import com.etologic.mahjongscoring2.business.model.enums.ShareGameOptions
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NONE
import com.etologic.mahjongscoring2.business.use_cases.CancelAllPenaltiesUseCase
import com.etologic.mahjongscoring2.business.use_cases.DeleteRoundUseCase
import com.etologic.mahjongscoring2.business.use_cases.EditGameNamesUseCase
import com.etologic.mahjongscoring2.business.use_cases.EndGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.ExportGameToCsvUseCase
import com.etologic.mahjongscoring2.business.use_cases.ExportGameToJsonUseCase
import com.etologic.mahjongscoring2.business.use_cases.ExportGameToTextUseCase
import com.etologic.mahjongscoring2.business.use_cases.GetIsDiffCalcsFeatureEnabledUseCase
import com.etologic.mahjongscoring2.business.use_cases.GetOneGameFlowUseCase
import com.etologic.mahjongscoring2.business.use_cases.HuDiscardUseCase
import com.etologic.mahjongscoring2.business.use_cases.HuDrawUseCase
import com.etologic.mahjongscoring2.business.use_cases.HuSelfPickUseCase
import com.etologic.mahjongscoring2.business.use_cases.ResumeGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.SaveIsDiffCalcsFeatureEnabledUseCase
import com.etologic.mahjongscoring2.business.use_cases.SetPenaltyUseCase
import com.etologic.mahjongscoring2.business.use_cases.ShowInAppReviewUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

typealias ShouldHighlightLastRound = Boolean

@HiltViewModel
class GameViewModel @Inject constructor(
    getOneGameFlowUseCase: GetOneGameFlowUseCase,
    private val editGameNamesUseCase: EditGameNamesUseCase,
    private val huDiscardUseCase: HuDiscardUseCase,
    private val huSelfPickUseCase: HuSelfPickUseCase,
    private val huDrawUseCase: HuDrawUseCase,
    private val setPenaltyUseCase: SetPenaltyUseCase,
    private val cancelAllPenaltiesUseCase: CancelAllPenaltiesUseCase,
    private val deleteRoundUseCase: DeleteRoundUseCase,
    private val resumeGameUseCase: ResumeGameUseCase,
    private val endGameUseCase: EndGameUseCase,
    private val exportGameToTextUseCase: ExportGameToTextUseCase,
    private val exportGameToCsvUseCase: ExportGameToCsvUseCase,
    private val exportGameToJsonUseCase: ExportGameToJsonUseCase,
    getIsDiffCalcsFeatureEnabledUseCase: GetIsDiffCalcsFeatureEnabledUseCase,
    private val saveIsDiffCalcsFeatureEnabledUseCase: SaveIsDiffCalcsFeatureEnabledUseCase,
    private val showInAppReviewUseCase: ShowInAppReviewUseCase,
) : BaseViewModel() {

    var lastHighlightedRound: CharSequence = ""

    private val _gameId = MutableStateFlow(NOT_SET_GAME_ID)

    @OptIn(ExperimentalCoroutinesApi::class)
    val gameFlow: StateFlow<UiGame> =
        _gameId.filter { it != NOT_SET_GAME_ID }
            .flatMapLatest { getOneGameFlowUseCase(it) }
            .stateIn(viewModelScope, SharingStarted.Lazily, UiGame())

    val isDiffsCalcsFeatureEnabledFlow: StateFlow<Boolean> = getIsDiffCalcsFeatureEnabledUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    private var _shouldShowDiffsFlow = MutableStateFlow(false)
    val shouldShowDiffsFlow: StateFlow<Boolean> = _shouldShowDiffsFlow

    private var _seatOrientationFlow = MutableStateFlow(DOWN)
    val seatsOrientationFlow: StateFlow<SeatOrientation> = _seatOrientationFlow

    private val _pageToShowFlow = MutableStateFlow(TABLE to false)
    val pageToShowFlow: StateFlow<Pair<GameTableFragment.GameTablePages, ShouldHighlightLastRound>> = _pageToShowFlow

    private var _selectedSeatFlow = MutableStateFlow(NONE)
    val selectedSeatFlow: StateFlow<TableWinds> = _selectedSeatFlow

    private val _exportedTextFlow = MutableStateFlow<String?>(null)
    val exportedTextFlow: StateFlow<String?> = _exportedTextFlow

    private val _exportedFilesFlow = MutableStateFlow<List<File>?>(null)
    val exportedFilesFlow: StateFlow<List<File>?> = _exportedFilesFlow

    fun setGameId(gameId: GameId) {
        viewModelScope.launch {
            _gameId.emit(gameId)
        }
    }

    //SELECTED PLAYER/SEAT
    fun onSeatClicked(wind: TableWinds) {
        viewModelScope.launch {
            _selectedSeatFlow.emit(wind)
        }
    }

    fun unselectSelectedSeat() {
        viewModelScope.launch {
            _selectedSeatFlow.emit(NONE)
        }
    }

    fun showPage(page: GameTableFragment.GameTablePages, highlightLastRound: Boolean = false) {
        viewModelScope.launch {
            _pageToShowFlow.emit(page to highlightLastRound)
        }
    }

    //GAME OPERATIONS
    fun resumeGame() {
        viewModelScope.launch {
            resumeGameUseCase(gameFlow.first())
                .onFailure(::showError)
        }
    }

    fun endGame() {
        viewModelScope.launch {
            endGameUseCase(gameFlow.first())
                .onSuccess { showInAppReviewUseCase() }
                .onFailure(::showError)
        }
    }

    fun saveGameNames(
        game: UiGame,
        gameName: String,
        nameP1: String,
        nameP2: String,
        nameP3: String,
        nameP4: String,
    ) {
        viewModelScope.launch {
            editGameNamesUseCase(
                uiGame = game,
                newGameName = gameName,
                newNameP1 = nameP1,
                newNameP2 = nameP2,
                newNameP3 = nameP3,
                newNameP4 = nameP4,
            )
                .onFailure(::showError)
        }
    }

    //ROUND OPERATIONS
    fun saveHuDiscardRound(huData: HuData) {
        viewModelScope.launch {
            huDiscardUseCase(gameFlow.first(), huData)
                .fold({ showSavedRound() }, ::showError)
        }
    }

    fun saveHuSelfPickRound(huData: HuData) {
        viewModelScope.launch {
            huSelfPickUseCase(gameFlow.first(), huData)
                .fold({ showSavedRound() }, ::showError)
        }
    }

    fun saveDrawRound() {
        viewModelScope.launch {
            huDrawUseCase(gameFlow.first())
                .fold({ showSavedRound() }, ::showError)
        }
    }

    private fun showSavedRound() {
        showPage(page = GameTableFragment.GameTablePages.LIST, highlightLastRound = true)
    }

    fun savePenalty(penaltyData: PenaltyData) {
        viewModelScope.launch {
            setPenaltyUseCase(gameFlow.first().ongoingRound, penaltyData)
                .onFailure(::showError)
        }
    }

    fun cancelPenalties() {
        viewModelScope.launch {
            cancelAllPenaltiesUseCase(gameFlow.first().ongoingRound)
                .onFailure(::showError)
        }
    }

    fun removeRound(roundId: RoundId) {
        viewModelScope.launch {
            deleteRoundUseCase(roundId)
                .onSuccess { lastHighlightedRound = "" }
                .onFailure(::showError)
        }
    }

    //VIEWS ACTIONS
    fun toggleSeatsRotation() {
        viewModelScope.launch {
            _seatOrientationFlow.update { currentOrientation -> if (currentOrientation == DOWN) OUT else DOWN }
        }
    }

    fun toggleDiffsFeature(isEnabled: Boolean) {
        viewModelScope.launch {
            saveIsDiffCalcsFeatureEnabledUseCase(isEnabled)
        }
    }

    fun toggleDiffsView(shouldShow: Boolean) {
        viewModelScope.launch {
            if (isDiffsCalcsFeatureEnabledFlow.first()) {
                _shouldShowDiffsFlow.emit(shouldShow)
            }
        }
    }

    //SHARE ACTIONS
    fun shareGame(option: ShareGameOptions, getExternalFilesDir: () -> File?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                when (option) {
                    ShareGameOptions.TEXT -> exportGameToTextUseCase.invoke(gameFlow.first().gameId)
                        .fold({ _exportedTextFlow.emit(it) }, ::showError)

                    ShareGameOptions.CSV -> exportGameToCsvUseCase.invoke(gameFlow.first().gameId, getExternalFilesDir)
                        .fold({ _exportedFilesFlow.emit(it) }, ::showError)

                    ShareGameOptions.JSON -> exportGameToJsonUseCase.invoke(gameFlow.first().gameId, getExternalFilesDir)
                        .fold({ _exportedFilesFlow.emit(it) }, ::showError)
                }
            }
        }
    }
}