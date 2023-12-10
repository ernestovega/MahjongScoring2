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
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.base.BaseViewModel
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel.GameScreens.HAND_ACTION
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel.GameScreens.PLAYERS
import com.etologic.mahjongscoring2.app.game.game_table.GameTableFragment.GameTablePages
import com.etologic.mahjongscoring2.business.model.dtos.HuData
import com.etologic.mahjongscoring2.business.model.dtos.PenaltyData
import com.etologic.mahjongscoring2.data_source.model.GameId
import com.etologic.mahjongscoring2.business.model.entities.RoundId
import com.etologic.mahjongscoring2.business.model.entities.UIGame
import com.etologic.mahjongscoring2.business.model.enums.SeatOrientation
import com.etologic.mahjongscoring2.business.model.enums.SeatOrientation.DOWN
import com.etologic.mahjongscoring2.business.model.enums.SeatOrientation.OUT
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NONE
import com.etologic.mahjongscoring2.business.use_cases.CancelPenaltyUseCase
import com.etologic.mahjongscoring2.business.use_cases.HuDiscardUseCase
import com.etologic.mahjongscoring2.business.use_cases.DrawUseCase
import com.etologic.mahjongscoring2.business.use_cases.EndGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.GetGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.RemoveRoundUseCase
import com.etologic.mahjongscoring2.business.use_cases.ResumeGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.SavePlayersNamesUseCase
import com.etologic.mahjongscoring2.business.use_cases.HuSelfPickUseCase
import com.etologic.mahjongscoring2.business.use_cases.SetPenaltyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

typealias ShouldHighlightLastRound = Boolean

@HiltViewModel
class GameViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val getGameUseCase: GetGameUseCase,
    private val savePlayersNamesUseCase: SavePlayersNamesUseCase,
    private val huDiscardUseCase: HuDiscardUseCase,
    private val huSelfPickUseCase: HuSelfPickUseCase,
    private val drawUseCase: DrawUseCase,
    private val setPenaltyUseCase: SetPenaltyUseCase,
    private val cancelPenaltyUseCase: CancelPenaltyUseCase,
    private val removeRoundUseCase: RemoveRoundUseCase,
    private val resumeGameUseCase: ResumeGameUseCase,
    private val endGameUseCase: EndGameUseCase,
) : BaseViewModel() {

    enum class GameScreens {
        COMBINATIONS,
        PLAYERS,
        DICE,
        HAND_ACTION,
        HU,
        PENALTY,
        RANKING,
        EXIT
    }

    var activeGameId: GameId? = null
    var lastHighlightedRound: CharSequence = ""
    private var playerOneLiteral: String? = null
    private var playerTwoLiteral: String? = null
    private var playerThreeLiteral: String? = null
    private var playerFourLiteral: String? = null
    private val _pageToShow = MutableLiveData<Pair<GameTablePages, ShouldHighlightLastRound>?>()
    fun getPageToShow(): LiveData<Pair<GameTablePages, ShouldHighlightLastRound>?> = _pageToShow
    private val _currentScreen = MutableLiveData<GameScreens>()
    fun getCurrentScreen(): LiveData<GameScreens> = _currentScreen
    private val _activeGame = MutableLiveData<UIGame>()
    fun getActiveGame(): LiveData<UIGame> = _activeGame
    private var _selectedSeat = MutableLiveData<TableWinds>()
    fun getSelectedSeat(): LiveData<TableWinds> = _selectedSeat
    private var _seatOrientation = MutableLiveData<SeatOrientation>()
    fun getSeatsOrientation(): LiveData<SeatOrientation> = _seatOrientation
    private var _shouldShowDiffs = MutableLiveData<Boolean>()
    fun shouldShowDiffs(): LiveData<Boolean> = _shouldShowDiffs

    init {
        playerOneLiteral = context.getString(R.string.player_one)
        playerTwoLiteral = context.getString(R.string.player_two)
        playerThreeLiteral = context.getString(R.string.player_three)
        playerFourLiteral = context.getString(R.string.player_four)
        _selectedSeat.postValue(NONE)
        _seatOrientation.postValue(DOWN)
    }

    fun loadTable() {
        disposables.add(
            getGameUseCase(activeGameId!!)
                .subscribeOn(Schedulers.io())
                .doOnSuccess { _activeGame.postValue(it) }
                .subscribe(this::showPlayersDialogIfProceed, ::showError)
        )
    }

    private fun showPlayersDialogIfProceed(uiGame: UIGame) {
        if (uiGame.rounds.size == 1 &&
            uiGame.dbGame.nameP1 == playerOneLiteral &&
            uiGame.dbGame.nameP2 == playerTwoLiteral &&
            uiGame.dbGame.nameP3 == playerThreeLiteral &&
            uiGame.dbGame.nameP4 == playerFourLiteral
        ) {
            navigateTo(PLAYERS)
        }
    }

    //SELECTED PLAYER/SEAT
    fun onSeatClicked(wind: TableWinds) {
        _selectedSeat.postValue(wind)
        _currentScreen.postValue(HAND_ACTION)
    }

    fun unselectSelectedSeat() {
        _selectedSeat.postValue(NONE)
        _activeGame.postValue(_activeGame.value)
    }

    fun showPage(page: GameTablePages?, highlightLastRound: Boolean = false) {
        _pageToShow.postValue(page?.let { it to highlightLastRound })
    }

    fun navigateTo(gameScreens: GameScreens) {
        _currentScreen.postValue(gameScreens)
    }

    //GAME OPERATIONS
    fun resumeGame() {
        disposables.add(
            resumeGameUseCase(activeGameId!!, _activeGame.value!!.rounds.size)
                .subscribeOn(Schedulers.io())
                .subscribe(_activeGame::postValue, ::showError)
        )
    }

    fun endGame() {
        disposables.add(
            endGameUseCase(activeGameId!!)
                .subscribeOn(Schedulers.io())
                .subscribe(_activeGame::postValue, ::showError)
        )
    }

    fun savePlayersNames(names: Array<String>) {
        disposables.add(
            savePlayersNamesUseCase(activeGameId!!, names)
                .subscribeOn(Schedulers.io())
                .subscribe(_activeGame::postValue, ::showError)
        )
    }

    //ROUND OPERATIONS
    fun saveHuDiscardRound(discarderCurrentSeat: TableWinds, huPoints: Int) {
        disposables.add(
            huDiscardUseCase(
                gameId = activeGameId!!,
                huData = HuData(
                    points = huPoints,
                    winnerInitialSeat = _activeGame.value!!.getPlayerInitialSeatByCurrentSeat(_selectedSeat.value!!),
                    discarderInitialSeat = _activeGame.value!!.getPlayerInitialSeatByCurrentSeat(discarderCurrentSeat),
                )
            )
                .subscribeOn(Schedulers.io())
                .subscribe(::roundSaved, ::showError)
        )
    }

    fun saveHuSelfPickRound(huPoints: Int) {
        disposables.add(
            huSelfPickUseCase(
                gameId = activeGameId!!,
                huData = HuData(
                    points = huPoints,
                    winnerInitialSeat = _activeGame.value!!.getPlayerInitialSeatByCurrentSeat(_selectedSeat.value!!),
                )
            )
                .subscribeOn(Schedulers.io())
                .subscribe(::roundSaved, ::showError)
        )
    }

    fun saveDrawRound() {
        disposables.add(
            drawUseCase(activeGameId!!)
                .subscribeOn(Schedulers.io())
                .subscribe(::roundSaved, ::showError)
        )
    }

    private fun roundSaved(uiGame: UIGame) {
        _activeGame.postValue(uiGame)
        showPage(page = GameTablePages.LIST, highlightLastRound = true)
    }

    fun savePenalty(penaltyData: PenaltyData) {
        penaltyData.penalizedPlayerInitialSeat =
            _activeGame.value!!.getPlayerInitialSeatByCurrentSeat(_selectedSeat.value!!)
        disposables.add(
            setPenaltyUseCase(activeGameId!!, penaltyData)
                .subscribeOn(Schedulers.io())
                .subscribe(_activeGame::postValue, ::showError)
        )
    }

    fun cancelPenalties() {
        disposables.add(
            cancelPenaltyUseCase(activeGameId!!)
                .subscribeOn(Schedulers.io())
                .subscribe(_activeGame::postValue, ::showError)
        )
    }

    fun removeRound(roundId: RoundId) {
        disposables.add(
            removeRoundUseCase(activeGameId!!, roundId)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { table ->
                        lastHighlightedRound = ""
                        _activeGame.postValue(table)
                    },
                    ::showError
                )
        )
    }

    //VIEWS ACTIONS
    fun toggleSeatsRotation() {
        _seatOrientation.postValue(if (_seatOrientation.value == DOWN) OUT else DOWN)
    }

    fun isGameEnded(): Boolean = _activeGame.value?.rounds?.last()?.isEnded == true

    fun showDiffs() {
        _shouldShowDiffs.postValue(true)
    }

    fun hideDiffs() {
        _shouldShowDiffs.postValue(false)
    }
}
