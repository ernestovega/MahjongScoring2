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
import com.etologic.mahjongscoring2.app.game.dialogs.ranking.RankingTableHelper
import com.etologic.mahjongscoring2.app.game.game_table.GameTableFragment.GameTablePages
import com.etologic.mahjongscoring2.business.model.dtos.HuData
import com.etologic.mahjongscoring2.business.model.dtos.PenaltyData
import com.etologic.mahjongscoring2.business.model.entities.Table
import com.etologic.mahjongscoring2.business.model.enums.SeatOrientation
import com.etologic.mahjongscoring2.business.model.enums.SeatOrientation.DOWN
import com.etologic.mahjongscoring2.business.model.enums.SeatOrientation.OUT
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NONE
import com.etologic.mahjongscoring2.business.use_cases.current_game.GetCurrentGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.current_game.SaveCurrentPlayersUseCase
import com.etologic.mahjongscoring2.business.use_cases.current_round.GameActionsUseCase
import com.etologic.mahjongscoring2.business.use_cases.current_round.PenaltyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

typealias ShouldHighlightLastRound = Boolean

@HiltViewModel
class GameViewModel @Inject constructor(
    @ApplicationContext context: Context,
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
    private val _currentTable = MutableLiveData<Table>()
    fun getCurrentTable(): LiveData<Table> = _currentTable
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
            getCurrentGameUseCase.getCurrentGameWithRounds()
                .subscribeOn(Schedulers.io())
                .doOnSuccess { _currentTable.postValue(it) }
                .subscribe(this::showPlayersDialogIfProceed, ::showError)
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
    fun onSeatClicked(wind: TableWinds) {
        _selectedSeat.postValue(wind)
        _currentScreen.postValue(HAND_ACTION)
    }

    fun unselectSelectedSeat() {
        _selectedSeat.postValue(NONE)
        _currentTable.postValue(_currentTable.value)
    }

    fun showPage(page: GameTablePages?, highlightLastRound: Boolean = false) {
        _pageToShow.postValue(page?.let { it to highlightLastRound})
    }

    fun navigateTo(gameScreens: GameScreens) {
        _currentScreen.postValue(gameScreens)
    }

    //GAME OPERATIONS
    fun resumeGame() {
        disposables.add(
            gameActionsUseCase.resume()
                .subscribeOn(Schedulers.io())
                .subscribe(_currentTable::postValue, ::showError)
        )
    }

    fun endGame() {
        disposables.add(
            gameActionsUseCase.end()
                .subscribeOn(Schedulers.io())
                .subscribe(_currentTable::postValue, ::showError)
        )
    }

    fun savePlayersNames(names: Array<String>) {
        disposables.add(
            saveCurrentPlayersUseCase.saveCurrentGamePlayersNames(names)
                .subscribeOn(Schedulers.io())
                .subscribe(_currentTable::postValue, ::showError)
        )
    }

    //ROUND OPERATIONS
    fun saveRonRound(discarderCurrentSeat: TableWinds, huPoints: Int) {
        disposables.add(
            gameActionsUseCase.discard(HuData(_selectedSeat.value!!, discarderCurrentSeat, huPoints))
                .subscribeOn(Schedulers.io())
                .subscribe(::roundSaved, ::showError)
        )
    }

    fun saveTsumoRound(huPoints: Int) {
        disposables.add(
            gameActionsUseCase.selfPick(HuData(_selectedSeat.value!!, huPoints))
                .subscribeOn(Schedulers.io())
                .subscribe(::roundSaved, ::showError)
        )
    }

    fun saveDrawRound() {
        disposables.add(
            gameActionsUseCase.draw()
                .subscribeOn(Schedulers.io())
                .subscribe(::roundSaved, ::showError)
        )
    }

    private fun roundSaved(table: Table?) {
        _currentTable.postValue(table)
        showPage(GameTablePages.LIST, highlightLastRound = true)
    }

    fun savePenalty(penaltyData: PenaltyData) {
        penaltyData.penalizedPlayerCurrentSeat = _selectedSeat.value!!
        disposables.add(
            penaltyUseCase.penalty(penaltyData)
                .subscribeOn(Schedulers.io())
                .subscribe(_currentTable::postValue, ::showError)
        )
    }

    fun cancelPenalties() {
        disposables.add(
            penaltyUseCase.cancelPenalties()
                .subscribeOn(Schedulers.io())
                .subscribe(_currentTable::postValue, ::showError)
        )
    }

    fun removeRound(roundId: Int) {
        disposables.add(
            gameActionsUseCase.removeRound(roundId)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { table ->
                        lastHighlightedRound = ""
                        _currentTable.postValue(table)
                    },
                    ::showError
                )
        )
    }

    //QUERIES
    fun loadRankingData() =
        getCurrentGameUseCase.getCurrentGameWithRounds()
            .subscribeOn(Schedulers.io())
            .map(RankingTableHelper::generateRankingTable)
            .blockingGet()

    fun thereArePenaltiesCurrently(): Boolean =
        getCurrentGameUseCase.getCurrentGameWithRounds()
            .subscribeOn(Schedulers.io())
            .map { it.rounds.last().areTherePenalties() }
            .blockingGet()

    fun getNamesByCurrentSeat(): Array<String> =
        getCurrentGameUseCase.getCurrentGameWithRounds()
            .subscribeOn(Schedulers.io())
            .map { it.getPlayersNamesByCurrentSeat() }
            .onErrorReturnItem(arrayOf("", "", "", ""))
            .blockingGet()

    fun getNamesByInitialPosition(): Array<String> =
        getCurrentGameUseCase.getCurrentGameWithRounds()
            .subscribeOn(Schedulers.io())
            .map { it.game.getPlayersNames() }
            .blockingGet()

    fun toggleSeatsRotation() {
        _seatOrientation.postValue(if (_seatOrientation.value == DOWN) OUT else DOWN)
    }

    fun isGameEnded(): Boolean = _currentTable.value?.rounds?.last()?.isEnded == true

    fun showDiffs() {
        _shouldShowDiffs.postValue(true)
    }

    fun hideDiffs() {
        _shouldShowDiffs.postValue(false)
    }
}
