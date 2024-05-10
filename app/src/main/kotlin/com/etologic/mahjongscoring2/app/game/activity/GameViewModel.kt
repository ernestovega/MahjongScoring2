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
package com.etologic.mahjongscoring2.app.game.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.etologic.mahjongscoring2.app.base.BaseViewModel
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel.GameScreens.HAND_ACTION
import com.etologic.mahjongscoring2.app.game.game_table.GameTableFragment.GameTablePages
import com.etologic.mahjongscoring2.business.model.dtos.HuData
import com.etologic.mahjongscoring2.business.model.dtos.PenaltyData
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.model.enums.SeatOrientation
import com.etologic.mahjongscoring2.business.model.enums.SeatOrientation.DOWN
import com.etologic.mahjongscoring2.business.model.enums.SeatOrientation.OUT
import com.etologic.mahjongscoring2.business.model.enums.ShareGameOptions
import com.etologic.mahjongscoring2.business.model.enums.ShareGameOptions.CSV
import com.etologic.mahjongscoring2.business.model.enums.ShareGameOptions.TEXT
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NONE
import com.etologic.mahjongscoring2.business.use_cases.CancelPenaltyUseCase
import com.etologic.mahjongscoring2.business.use_cases.EditGameNamesUseCase
import com.etologic.mahjongscoring2.business.use_cases.EndGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.ExportGameToCsvUseCase
import com.etologic.mahjongscoring2.business.use_cases.ExportGameResultsToTextUseCase
import com.etologic.mahjongscoring2.business.use_cases.GetIsDiffCalcsFeatureEnabledUseCase
import com.etologic.mahjongscoring2.business.use_cases.GetOneGameFlowUseCase
import com.etologic.mahjongscoring2.business.use_cases.HuDiscardUseCase
import com.etologic.mahjongscoring2.business.use_cases.HuDrawUseCase
import com.etologic.mahjongscoring2.business.use_cases.HuSelfPickUseCase
import com.etologic.mahjongscoring2.business.use_cases.RemoveRoundUseCase
import com.etologic.mahjongscoring2.business.use_cases.ResumeGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.SaveIsDiffCalcsFeatureEnabledUseCase
import com.etologic.mahjongscoring2.business.use_cases.SetPenaltyUseCase
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.GameId
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.RoundId
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

typealias ShouldHighlightLastRound = Boolean

class GameViewModel @AssistedInject constructor(
    @Assisted private val gameId: GameId,
    getOneGameFlowUseCase: GetOneGameFlowUseCase,
    private val editGameNamesUseCase: EditGameNamesUseCase,
    private val huDiscardUseCase: HuDiscardUseCase,
    private val huSelfPickUseCase: HuSelfPickUseCase,
    private val huDrawUseCase: HuDrawUseCase,
    private val setPenaltyUseCase: SetPenaltyUseCase,
    private val cancelPenaltyUseCase: CancelPenaltyUseCase,
    private val removeRoundUseCase: RemoveRoundUseCase,
    private val resumeGameUseCase: ResumeGameUseCase,
    private val endGameUseCase: EndGameUseCase,
    private val exportGameResultsToTextUseCase: ExportGameResultsToTextUseCase,
    private val exportGameToCsvUseCase: ExportGameToCsvUseCase,
    getIsDiffCalcsFeatureEnabledUseCase: GetIsDiffCalcsFeatureEnabledUseCase,
    private val saveIsDiffCalcsFeatureEnabledUseCase: SaveIsDiffCalcsFeatureEnabledUseCase,
) : BaseViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(gameId: GameId): GameViewModel
    }

    companion object {
        fun provideFactory(assistedFactory: Factory, gameId: GameId)
                : ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = assistedFactory.create(gameId) as T
        }
    }

    enum class GameScreens {
        COMBINATIONS,
        EDIT_NAMES,
        DICE,
        HAND_ACTION,
        HU,
        PENALTY,
        RANKING,
        EXIT
    }

    var lastHighlightedRound: CharSequence = ""

    private val _pageToShow = MutableLiveData<Pair<GameTablePages, ShouldHighlightLastRound>?>()
    fun getPageToShow(): LiveData<Pair<GameTablePages, ShouldHighlightLastRound>?> = _pageToShow
    private val _currentScreen = MutableLiveData<GameScreens>()
    fun getCurrentScreen(): LiveData<GameScreens> = _currentScreen
    private var _selectedSeat = MutableLiveData(NONE)
    fun getSelectedSeat(): LiveData<TableWinds> = _selectedSeat
    private var _seatOrientation = MutableLiveData(DOWN)
    fun getSeatsOrientation(): LiveData<SeatOrientation> = _seatOrientation
    private var _shouldShowDiffs = MutableLiveData<Boolean>()
    fun shouldShowDiffs(): LiveData<Boolean> = _shouldShowDiffs
    private val _exportedText = MutableLiveData<String>()
    fun getExportedText(): LiveData<String> = _exportedText
    private val _exportedFiles = MutableLiveData<List<File>>()
    fun getExportedFiles(): LiveData<List<File>> = _exportedFiles

    val gameFlow: StateFlow<UiGame> = getOneGameFlowUseCase(gameId)
        .stateIn(viewModelScope, SharingStarted.Lazily, UiGame())

    val isDiffsCalcsFeatureEnabledFlow: StateFlow<Boolean> = getIsDiffCalcsFeatureEnabledUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    //SELECTED PLAYER/SEAT
    fun onSeatClicked(wind: TableWinds) {
        _selectedSeat.postValue(wind)
        _currentScreen.postValue(HAND_ACTION)
    }

    fun unselectSelectedSeat() {
        _selectedSeat.postValue(NONE)
    }

    fun showPage(page: GameTablePages?, highlightLastRound: Boolean = false) {
        _pageToShow.postValue(page?.let { it to highlightLastRound })
    }

    fun navigateTo(gameScreens: GameScreens) {
        _currentScreen.postValue(gameScreens)
    }

    //GAME OPERATIONS
    fun resumeGame() {
        viewModelScope.launch {
            resumeGameUseCase(gameFlow.value, gameFlow.value.currentRound.roundNumber)
                .onFailure(::showError)
        }
    }

    fun endGame() {
        viewModelScope.launch {
            endGameUseCase(gameFlow.value)
                .onFailure(::showError)
        }
    }

    fun saveGameNames(
        gameName: String,
        nameP1: String,
        nameP2: String,
        nameP3: String,
        nameP4: String,
    ) {
        viewModelScope.launch {
            editGameNamesUseCase(
                uiGame = gameFlow.value,
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
            huDiscardUseCase(gameFlow.value, huData)
                .fold({ showSavedRound() }, ::showError)
        }
    }

    fun saveHuSelfPickRound(huData: HuData) {
        viewModelScope.launch {
            huSelfPickUseCase(gameFlow.value, huData)
                .fold({ showSavedRound() }, ::showError)
        }
    }

    fun saveDrawRound() {
        viewModelScope.launch {
            huDrawUseCase(gameFlow.value)
                .fold({ showSavedRound() }, ::showError)
        }
    }

    private fun showSavedRound() {
        showPage(page = GameTablePages.LIST, highlightLastRound = true)
    }

    fun savePenalty(penaltyData: PenaltyData) {
        viewModelScope.launch {
            setPenaltyUseCase(gameFlow.value.currentRound, penaltyData)
                .onFailure(::showError)
        }
    }

    fun cancelPenalties() {
        viewModelScope.launch {
            cancelPenaltyUseCase(gameFlow.value.currentRound)
                .onFailure(::showError)
        }
    }

    fun removeRound(roundId: RoundId) {
        viewModelScope.launch {
            removeRoundUseCase(gameId, roundId)
                .onSuccess { lastHighlightedRound = "" }
                .onFailure(::showError)
        }
    }

    //VIEWS ACTIONS
    fun toggleSeatsRotation() {
        _seatOrientation.postValue(if (_seatOrientation.value == DOWN) OUT else DOWN)
    }

    fun toggleDiffsFeature(isEnabled: Boolean) {
        viewModelScope.launch {
            saveIsDiffCalcsFeatureEnabledUseCase(isEnabled)
        }
    }

    fun toggleDiffsView(shouldShow: Boolean) {
        if (isDiffsCalcsFeatureEnabledFlow.value) {
            _shouldShowDiffs.postValue(shouldShow)
        }
    }

    //SHARE ACTIONS
    fun shareGame(option: ShareGameOptions, getExternalFilesDir: () -> File?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                when (option) {
                    TEXT -> exportGameResultsToTextUseCase.invoke(gameId)
                        .fold(_exportedText::postValue, ::showError)

                    CSV -> exportGameToCsvUseCase.invoke(gameId, getExternalFilesDir)
                        .fold(_exportedFiles::postValue, ::showError)
                }
            }
        }
    }
}
