/*
    Copyright (C) 2020  Ernesto Vega de la Iglesia Soria
 
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.etologic.mahjongscoring2.app.game.activity

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.base.BaseViewModel
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel.GameScreens.HAND_ACTION
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel.GameScreens.PLAYERS
import com.etologic.mahjongscoring2.app.game.dialogs.ranking.RankingTableHelper
import com.etologic.mahjongscoring2.app.game.game_table.GameTableFragment.GameTablePages
import com.etologic.mahjongscoring2.business.model.dtos.HuData
import com.etologic.mahjongscoring2.business.model.dtos.PenaltyData
import com.etologic.mahjongscoring2.business.model.entities.Table
import com.etologic.mahjongscoring2.business.model.enums.ScreenOrientation
import com.etologic.mahjongscoring2.business.model.enums.ScreenOrientation.LANDSCAPE
import com.etologic.mahjongscoring2.business.model.enums.ScreenOrientation.PORTRAIT
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NONE
import com.etologic.mahjongscoring2.business.use_cases.current_game.GetCurrentGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.current_game.SaveCurrentPlayersUseCase
import com.etologic.mahjongscoring2.business.use_cases.current_round.GameActionsUseCase
import com.etologic.mahjongscoring2.business.use_cases.current_round.PenaltyUseCase
import io.reactivex.schedulers.Schedulers

class GameViewModel internal constructor(
    contextForResources: Context,
    private val getCurrentGameUseCase: GetCurrentGameUseCase,
    private val saveCurrentPlayersUseCase: SaveCurrentPlayersUseCase,
    private val gameActionsUseCase: GameActionsUseCase,
    private val penaltyUseCase: PenaltyUseCase
) : BaseViewModel() {

    enum class GameScreens {
        COMBINATIONS,
        PLAYERS,
        DICE,
        HAND_ACTION,
        HU,
        DISCARDER,
        PENALTY,
        RANKING,
        EXIT
    }

    private var playerOneLiteral: String? = null
    private var playerTwoLiteral: String? = null
    private var playerThreeLiteral: String? = null
    private var playerFourLiteral: String? = null
    private val _currentPage = MutableLiveData<GameTablePages>()
    internal fun getCurrentPage(): LiveData<GameTablePages> = _currentPage
    private val _currentScreen = MutableLiveData<GameScreens>()
    internal fun getCurrentScreen(): LiveData<GameScreens> = _currentScreen
    private val _currentTable = MutableLiveData<Table>()
    internal fun getCurrentTable(): LiveData<Table> = _currentTable
    private var _selectedSeat = MutableLiveData<TableWinds>()
    internal fun getSelectedSeat(): LiveData<TableWinds> = _selectedSeat
    private var _seatsRotation = MutableLiveData<ScreenOrientation>()
    internal fun getScreenOrientation(): LiveData<ScreenOrientation> = _seatsRotation

    //DTOs
    internal var huPoints = 0

    init {
        playerOneLiteral = contextForResources.getString(R.string.player_one)
        playerTwoLiteral = contextForResources.getString(R.string.player_two)
        playerThreeLiteral = contextForResources.getString(R.string.player_three)
        playerFourLiteral = contextForResources.getString(R.string.player_four)
        _selectedSeat.postValue(NONE)
        _seatsRotation.postValue(LANDSCAPE)
    }

    //METHODS
    internal fun loadTable() {
        disposables.add(
            getCurrentGameUseCase.getCurrentGameWithRounds()
                .subscribeOn(Schedulers.io())
                .doOnSuccess { _currentTable.postValue(it) }
                .subscribe(this::showPlayersDialogIfProceed, this::showError)
        )
    }

    private fun showPlayersDialogIfProceed(table: Table) {
        if (table.rounds.size == 1 &&
            table.game.nameP1 == playerOneLiteral &&
            table.game.nameP2 == playerTwoLiteral &&
            table.game.nameP3 == playerThreeLiteral &&
            table.game.nameP4 == playerFourLiteral
        ) navigateTo(PLAYERS)
    }

    //SELECTED PLAYER/SEAT
    internal fun onSeatClicked(wind: TableWinds) {
        _selectedSeat.postValue(wind)
        _currentScreen.postValue(HAND_ACTION)
    }

    internal fun unselectedSelectedSeat() {
        _selectedSeat.postValue(NONE)
        _currentTable.postValue(_currentTable.value)
    }

    //NAVIGATION
    internal fun showPage(page: GameTablePages) {
        _currentPage.postValue(page)
    }

    internal fun navigateTo(gameScreens: GameScreens) {
        _currentScreen.postValue(gameScreens)
    }

    //GAME OPERATIONS
    internal fun resumeGame() {
        disposables.add(
            gameActionsUseCase.resume()
                .subscribeOn(Schedulers.io())
                .subscribe(_currentTable::postValue, this::showError)
        )
    }

    internal fun endGame() {
        disposables.add(
            gameActionsUseCase.end()
                .subscribeOn(Schedulers.io())
                .subscribe(_currentTable::postValue, this::showError)
        )
    }

    internal fun savePlayersNames(names: Array<String>) {
        disposables.add(
            saveCurrentPlayersUseCase.saveCurrentGamePlayersNames(names)
                .subscribeOn(Schedulers.io())
                .subscribe(_currentTable::postValue, this::showError)
        )
    }

    //ROUND OPERATIONS
    internal fun saveRonRound(discarderCurrentSeat: TableWinds, huPoints: Int) {
        disposables.add(
            gameActionsUseCase.discard(HuData(_selectedSeat.value!!, discarderCurrentSeat, huPoints))
                .subscribeOn(Schedulers.io())
                .subscribe(_currentTable::postValue, this::showError)
        )
    }

    internal fun saveTsumoRound(huPoints: Int) {
        disposables.add(
            gameActionsUseCase.selfPick(HuData(_selectedSeat.value!!, huPoints))
                .subscribeOn(Schedulers.io())
                .subscribe(_currentTable::postValue, this::showError)
        )
    }

    internal fun saveDrawRound() {
        disposables.add(
            gameActionsUseCase.draw()
                .subscribeOn(Schedulers.io())
                .subscribe(_currentTable::postValue, this::showError)
        )
    }

    internal fun savePenalty(penaltyData: PenaltyData) {
        penaltyData.penalizedPlayerCurrentSeat = _selectedSeat.value!!
        disposables.add(
            penaltyUseCase.penalty(penaltyData)
                .subscribeOn(Schedulers.io())
                .subscribe(_currentTable::postValue, this::showError)
        )
    }

    internal fun cancelPenalties() {
        disposables.add(
            penaltyUseCase.cancelPenalties()
                .subscribeOn(Schedulers.io())
                .subscribe(_currentTable::postValue, this::showError)
        )
    }

    internal fun removeRound(roundId: Int) {
        disposables.add(
            gameActionsUseCase.removeRound(roundId)
                .subscribeOn(Schedulers.io())
                .subscribe(_currentTable::postValue, this::showError)
        )
    }

    //QUERIES
    internal fun loadRankingData() =
        getCurrentGameUseCase.getCurrentGameWithRounds()
            .subscribeOn(Schedulers.io())
            .map(RankingTableHelper::generateRankingTable)
            .blockingGet()

    internal fun thereArePenaltiesCurrently(): Boolean =
        getCurrentGameUseCase.getCurrentGameWithRounds()
            .subscribeOn(Schedulers.io())
            .map { it.rounds.last().areTherePenalties() }
            .blockingGet()

    internal fun getNamesByCurrentSeat(): Array<String> =
        getCurrentGameUseCase.getCurrentGameWithRounds()
            .subscribeOn(Schedulers.io())
            .map { it.getPlayersNamesByCurrentSeat() }
            .onErrorReturnItem(arrayOf("", "", "", ""))
            .blockingGet()

    internal fun getNamesByInitialPosition(): Array<String> =
        getCurrentGameUseCase.getCurrentGameWithRounds()
            .subscribeOn(Schedulers.io())
            .map { it.game.getPlayersNames() }
            .blockingGet()

    internal fun toggleSeatsRotation() {
        _seatsRotation.postValue(if (_seatsRotation.value == LANDSCAPE) PORTRAIT else LANDSCAPE)
    }

    fun isGameEnded(): Boolean = _currentTable.value?.rounds?.last()?.isEnded == true
}
