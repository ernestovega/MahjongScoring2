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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.etologic.mahjongscoring2.app.base.BaseViewModel
import com.etologic.mahjongscoring2.app.screens.game.GameFragment.GamePages
import com.etologic.mahjongscoring2.business.model.dtos.HuData
import com.etologic.mahjongscoring2.business.model.dtos.PenaltyData
import com.etologic.mahjongscoring2.business.model.entities.GameId
import com.etologic.mahjongscoring2.business.model.entities.RoundId
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.model.entities.UiGame.Companion.NOT_SET_GAME_ID
import com.etologic.mahjongscoring2.business.model.enums.SeatsArrangement
import com.etologic.mahjongscoring2.business.model.enums.ShareGameOptions
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.use_cases.CancelAllPenaltiesUseCase
import com.etologic.mahjongscoring2.business.use_cases.DeleteRoundUseCase
import com.etologic.mahjongscoring2.business.use_cases.EditGameNamesUseCase
import com.etologic.mahjongscoring2.business.use_cases.EndGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.ExportGameToCsvUseCase
import com.etologic.mahjongscoring2.business.use_cases.ExportGameToJsonUseCase
import com.etologic.mahjongscoring2.business.use_cases.ExportGameToTextUseCase
import com.etologic.mahjongscoring2.business.use_cases.GetOneGameFlowUseCase
import com.etologic.mahjongscoring2.business.use_cases.HuDiscardUseCase
import com.etologic.mahjongscoring2.business.use_cases.HuDrawUseCase
import com.etologic.mahjongscoring2.business.use_cases.HuSelfPickUseCase
import com.etologic.mahjongscoring2.business.use_cases.ResumeGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.SetPenaltyUseCase
import com.etologic.mahjongscoring2.business.use_cases.ShowInAppReviewUseCase
import com.etologic.mahjongscoring2.data_source.repositories.DiffCalcsFeatureEnabledRepository
import com.etologic.mahjongscoring2.data_source.repositories.SelectedSeatsOrientationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

enum class GamePageActions {
    NOTHING,
    HIGHLIGHT_LAST_ROUND,
    SHOW_RANKING,
}

sealed interface GameUiState {

    data class Error(val throwable: Throwable) : GameUiState

    data object Loading : GameUiState

    data class Loaded(
        val error: Throwable?,
        val game: UiGame,
        val isDiffsCalcsFeatureEnabled: Boolean,
        val shouldShowDiffs: Boolean,
        val seatsArrangement: SeatsArrangement,
        val pageToShow: Pair<GamePages, GamePageActions>,
        val selectedSeat: TableWinds,
    ) : GameUiState
}

@HiltViewModel
class GameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
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
    private val diffCalcsFeatureEnabledRepository: DiffCalcsFeatureEnabledRepository,
    private val selectedSeatsOrientationRepository: SelectedSeatsOrientationRepository,
    private val showInAppReviewUseCase: ShowInAppReviewUseCase,
) : BaseViewModel() {

    private val _gameId: StateFlow<GameId> =
        savedStateHandle.getStateFlow("gameId", NOT_SET_GAME_ID)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val gameFlow: StateFlow<UiGame?> =
        _gameId.filter { it != NOT_SET_GAME_ID }
            .flatMapLatest(getOneGameFlowUseCase::invoke)
            .catch { showError(it) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val isDiffsCalcsFeatureEnabledStateFlow: StateFlow<Boolean> =
        diffCalcsFeatureEnabledRepository.diffCalcsFeatureEnabledFlow
            .stateIn(viewModelScope, SharingStarted.Lazily, true)

    private val seatsArrangementStateFlow: StateFlow<SeatsArrangement> =
        selectedSeatsOrientationRepository.selectedSeatsArrangementFlow
            .stateIn(viewModelScope, SharingStarted.Lazily, SeatsArrangement.DOWN)

    private val shouldShowDiffsFlow = MutableStateFlow(false)
    private val pageToShowFlow = MutableStateFlow(GamePages.TABLE to GamePageActions.NOTHING)
    private val selectedSeatFlow = MutableStateFlow(TableWinds.NONE)

    private data class CoreState(
        val error: Throwable?,
        val game: UiGame,
        val isDiffsCalcsFeatureEnabled: Boolean,
        val shouldShowDiffs: Boolean,
        val seatsArrangement: SeatsArrangement,
    )

    private val coreStateFlow: Flow<CoreState> =
        combine(
            errorFlow,
            gameFlow.filterNotNull(),
            isDiffsCalcsFeatureEnabledStateFlow,
            shouldShowDiffsFlow,
            seatsArrangementStateFlow,
        ) { error, game, isDiffsEnabled, shouldShowDiffs, seatsOrientation ->
            CoreState(
                error = error,
                game = game,
                isDiffsCalcsFeatureEnabled = isDiffsEnabled,
                shouldShowDiffs = shouldShowDiffs,
                seatsArrangement = seatsOrientation,
            )
        }

    val gameUiStateFlow: StateFlow<GameUiState> =
        combine(
            coreStateFlow,
            pageToShowFlow,
            selectedSeatFlow,
        ) { core, pageToShow, selectedSeat ->
            val pageToShowUpdated = if (pageToShow.first == GamePages.LIST &&
                pageToShow.second == GamePageActions.HIGHLIGHT_LAST_ROUND &&
                core.game.isEnded
            ) {
                GamePages.TABLE to GamePageActions.SHOW_RANKING
            } else {
                pageToShow
            }
            if (pageToShowUpdated.second == GamePageActions.SHOW_RANKING &&
                core.seatsArrangement != SeatsArrangement.RANKING
            ) {
                viewModelScope.launch {
                    selectedSeatsOrientationRepository.save(SeatsArrangement.RANKING)
                }
                gameUiStateFlow.value
            } else {
                core.error?.let { GameUiState.Error(it) }
                    ?: GameUiState.Loaded(
                        error = null,
                        game = core.game,
                        isDiffsCalcsFeatureEnabled = core.isDiffsCalcsFeatureEnabled,
                        shouldShowDiffs = core.shouldShowDiffs,
                        seatsArrangement = core.seatsArrangement,
                        pageToShow = pageToShowUpdated,
                        selectedSeat = selectedSeat,
                    )
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, GameUiState.Loading)

    //SELECTED PLAYER/SEAT
    fun onSeatClicked(wind: TableWinds) {
        viewModelScope.launch {
            selectedSeatFlow.emit(wind)
        }
    }

    fun unselectSeats() {
        viewModelScope.launch {
            selectedSeatFlow.emit(TableWinds.NONE)
        }
    }

    fun showPage(page: GamePages, action: GamePageActions = GamePageActions.NOTHING) {
        viewModelScope.launch {
            pageToShowFlow.emit(page to action)
        }
    }

    //GAME OPERATIONS
    fun resumeGame() {
        gameFlow.value?.let { game ->
            viewModelScope.launch {
                resumeGameUseCase.invoke(game)
                    .onFailure(::showError)
            }
        }
    }

    fun endGame() {
        gameFlow.value?.let { game ->
            viewModelScope.launch {
                endGameUseCase.invoke(game)
                    .onSuccess { showInAppReviewUseCase.invoke() }
                    .onFailure(::showError)
            }
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
            editGameNamesUseCase.invoke(
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
        gameFlow.value?.let { game ->
            viewModelScope.launch {
                huDiscardUseCase.invoke(game, huData)
                    .fold({ onRoundSaved() }, ::showError)
            }
        }
    }

    fun saveHuSelfPickRound(huData: HuData) {
        gameFlow.value?.let { game ->
            viewModelScope.launch {
                huSelfPickUseCase.invoke(game, huData)
                    .fold({ onRoundSaved() }, ::showError)
            }
        }
    }

    fun saveDrawRound() {
        gameFlow.value?.let { game ->
            viewModelScope.launch {
                huDrawUseCase.invoke(game)
                    .fold({ onRoundSaved() }, ::showError)
            }
        }
    }

    private fun onRoundSaved() {
        showPage(
            page = GamePages.LIST,
            action = GamePageActions.HIGHLIGHT_LAST_ROUND,
        )
    }

    fun savePenalty(penaltyData: PenaltyData) {
        gameFlow.value?.let { game ->
            viewModelScope.launch {
                setPenaltyUseCase.invoke(game.ongoingRound, penaltyData)
                    .onFailure(::showError)
            }
        }
    }

    fun cancelPenalties() {
        gameFlow.value?.let { game ->
            viewModelScope.launch {
                cancelAllPenaltiesUseCase.invoke(game.ongoingRound)
                    .onFailure(::showError)
            }
        }
    }

    fun removeRound(roundId: RoundId) {
        viewModelScope.launch {
            deleteRoundUseCase.invoke(roundId)
                .onFailure(::showError)
        }
    }

    //VIEWS ACTIONS
    fun nextSeatsOrientation() {
        viewModelScope.launch {
            selectedSeatsOrientationRepository.save(
                when (seatsArrangementStateFlow.value) {
                    SeatsArrangement.DOWN -> SeatsArrangement.OUT
                    SeatsArrangement.OUT -> SeatsArrangement.RANKING
                    SeatsArrangement.RANKING -> SeatsArrangement.DOWN
                }
            )
        }
    }

    fun showDiffCalcsFeature() {
        viewModelScope.launch {
            diffCalcsFeatureEnabledRepository.save(true)
        }
    }

    fun hideDiffCalcsFeature() {
        viewModelScope.launch {
            diffCalcsFeatureEnabledRepository.save(false)
        }
    }

    fun toggleDiffsView(shouldShow: Boolean) {
        viewModelScope.launch {
            if (isDiffsCalcsFeatureEnabledStateFlow.value) {
                shouldShowDiffsFlow.emit(shouldShow)
            }
        }
    }

    //SHARE ACTIONS
    fun shareGame(
        option: ShareGameOptions,
        directory: File?,
        showShareText: (String) -> Unit,
        showShareFiles: (Array<File>) -> Unit,
    ) {
        gameFlow.value?.gameId?.let { gameId ->
            viewModelScope.launch {
                withContext(Dispatchers.Default) {
                    when (option) {
                        ShareGameOptions.TEXT -> exportGameToTextUseCase.invoke(gameId)
                            .fold({ showShareText.invoke(it) }, ::showError)

                        ShareGameOptions.CSV -> exportGameToCsvUseCase.invoke(gameId, directory)
                            .fold({ showShareFiles.invoke(arrayOf(it)) }, ::showError)

                        ShareGameOptions.JSON -> exportGameToJsonUseCase.invoke(gameId, directory)
                            .fold({ showShareFiles.invoke(arrayOf(it)) }, ::showError)
                    }
                }
            }
        }
    }
}