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

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.base.BaseViewModel
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel.GameScreens.HAND_ACTION
import com.etologic.mahjongscoring2.app.game.game_table.GameTableFragment.GameTablePages
import com.etologic.mahjongscoring2.business.model.dtos.HuData
import com.etologic.mahjongscoring2.business.model.dtos.PenaltyData
import com.etologic.mahjongscoring2.business.model.entities.RoundId
import com.etologic.mahjongscoring2.business.model.entities.UIGame
import com.etologic.mahjongscoring2.business.model.enums.SeatOrientation
import com.etologic.mahjongscoring2.business.model.enums.SeatOrientation.DOWN
import com.etologic.mahjongscoring2.business.model.enums.SeatOrientation.OUT
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NONE
import com.etologic.mahjongscoring2.business.use_cases.CancelPenaltyUseCase
import com.etologic.mahjongscoring2.business.use_cases.EditGameNamesUseCase
import com.etologic.mahjongscoring2.business.use_cases.EndGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.ExportGameToTextUseCase
import com.etologic.mahjongscoring2.business.use_cases.GetOneGameFlowUseCase
import com.etologic.mahjongscoring2.business.use_cases.HuDiscardUseCase
import com.etologic.mahjongscoring2.business.use_cases.HuDrawUseCase
import com.etologic.mahjongscoring2.business.use_cases.HuSelfPickUseCase
import com.etologic.mahjongscoring2.business.use_cases.RemoveRoundUseCase
import com.etologic.mahjongscoring2.business.use_cases.ResumeGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.SetPenaltyUseCase
import com.etologic.mahjongscoring2.data_source.model.GameId
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

typealias ShouldHighlightLastRound = Boolean

class GameViewModel @AssistedInject constructor(
    @ApplicationContext context: Context,
    getOneGameFlowUseCase: GetOneGameFlowUseCase,
    @Assisted private val gameId: GameId,
    private val editGameNamesUseCase: EditGameNamesUseCase,
    private val huDiscardUseCase: HuDiscardUseCase,
    private val huSelfPickUseCase: HuSelfPickUseCase,
    private val huDrawUseCase: HuDrawUseCase,
    private val setPenaltyUseCase: SetPenaltyUseCase,
    private val cancelPenaltyUseCase: CancelPenaltyUseCase,
    private val removeRoundUseCase: RemoveRoundUseCase,
    private val resumeGameUseCase: ResumeGameUseCase,
    private val endGameUseCase: EndGameUseCase,
    private val exportGameToTextUseCase: ExportGameToTextUseCase,
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
    private var playerOneLiteral: String? = null
    private var playerTwoLiteral: String? = null
    private var playerThreeLiteral: String? = null
    private var playerFourLiteral: String? = null

    private val _pageToShow = MutableLiveData<Pair<GameTablePages, ShouldHighlightLastRound>?>()
    fun getPageToShow(): LiveData<Pair<GameTablePages, ShouldHighlightLastRound>?> = _pageToShow
    private val _currentScreen = MutableLiveData<GameScreens>()
    fun getCurrentScreen(): LiveData<GameScreens> = _currentScreen
    private var _selectedSeat = MutableLiveData<TableWinds>()
    fun getSelectedSeat(): LiveData<TableWinds> = _selectedSeat
    private var _seatOrientation = MutableLiveData<SeatOrientation>()
    fun getSeatsOrientation(): LiveData<SeatOrientation> = _seatOrientation
    private var _shouldShowDiffs = MutableLiveData<Boolean>()
    fun shouldShowDiffs(): LiveData<Boolean> = _shouldShowDiffs
    private val exportedGame = MutableLiveData<String>()
    fun getExportedGame(): LiveData<String> = exportedGame

    init {
        playerOneLiteral = context.getString(R.string.player_one)
        playerTwoLiteral = context.getString(R.string.player_two)
        playerThreeLiteral = context.getString(R.string.player_three)
        playerFourLiteral = context.getString(R.string.player_four)
        _selectedSeat.postValue(NONE)
        _seatOrientation.postValue(DOWN)
    }

    lateinit var game: UIGame

    val gameFlow: SharedFlow<UIGame> = getOneGameFlowUseCase(gameId)
        .onEach { game -> this.game = game }
        .shareIn(viewModelScope, SharingStarted.Lazily, replay = 1)

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
            resumeGameUseCase(game.dbGame, game.currentRound.roundNumber)
                .onFailure(::showError)
        }
    }

    fun endGame() {
        viewModelScope.launch {
            endGameUseCase(game)
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
                dbGame = game.dbGame,
                gameName = gameName,
                nameP1 = nameP1,
                nameP2 = nameP2,
                nameP3 = nameP3,
                nameP4 = nameP4,
            )
                .onFailure(::showError)
        }
    }

    //ROUND OPERATIONS
    fun saveHuDiscardRound(discarderCurrentSeat: TableWinds, huPoints: Int) {
        viewModelScope.launch {
            huDiscardUseCase(
                uiGame = game,
                huData = HuData(
                    points = huPoints,
                    winnerInitialSeat = game.getPlayerInitialSeatByCurrentSeat(_selectedSeat.value!!),
                    discarderInitialSeat = game.getPlayerInitialSeatByCurrentSeat(discarderCurrentSeat),
                )
            ).fold({ showSavedRound() }, ::showError)
        }
    }

    fun saveHuSelfPickRound(huPoints: Int) {
        viewModelScope.launch {
            huSelfPickUseCase(
                uiGame = game,
                huData = HuData(
                    points = huPoints,
                    winnerInitialSeat = game.getPlayerInitialSeatByCurrentSeat(_selectedSeat.value!!),
                )
            ).fold({ showSavedRound() }, ::showError)
        }
    }

    fun saveDrawRound() {
        viewModelScope.launch {
            huDrawUseCase(game)
                .fold({ showSavedRound() }, ::showError)
        }
    }

    private fun showSavedRound() {
        showPage(page = GameTablePages.LIST, highlightLastRound = true)
    }

    fun savePenalty(penaltyData: PenaltyData) {
        viewModelScope.launch {
            penaltyData.penalizedPlayerInitialSeat =
                game.getPlayerInitialSeatByCurrentSeat(_selectedSeat.value!!)

            setPenaltyUseCase(game.currentRound, penaltyData)
                .onFailure(::showError)
        }
    }

    fun cancelPenalties() {
        viewModelScope.launch {
            cancelPenaltyUseCase(game.currentRound)
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

    fun showDiffs() {
        _shouldShowDiffs.postValue(true)
    }

    fun hideDiffs() {
        _shouldShowDiffs.postValue(false)
    }

    //SHARE ACTIONS
    fun shareGame(getStringRes: (Int) -> String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                exportGameToTextUseCase.invoke(gameId, getStringRes)
                    .also { exportedGame.postValue(it) }
            }
        }
    }
}
