package com.etologic.mahjongscoring2.app.game.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.etologic.mahjongscoring2.app.base.BaseViewModel
import com.etologic.mahjongscoring2.app.game.activity.GameActivityViewModel.GameScreens.HAND_ACTION
import com.etologic.mahjongscoring2.app.game.activity.GameActivityViewModel.GameScreens.PLAYERS
import com.etologic.mahjongscoring2.app.game.dialogs.ranking.RankingTableHelper
import com.etologic.mahjongscoring2.app.game.game_table.GameTableFragment.GameTablePages
import com.etologic.mahjongscoring2.app.model.ShowState.HIDE
import com.etologic.mahjongscoring2.app.model.ShowState.SHOW
import com.etologic.mahjongscoring2.business.model.dtos.HuData
import com.etologic.mahjongscoring2.business.model.entities.Table
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NONE
import com.etologic.mahjongscoring2.business.use_cases.current_game.GetCurrentGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.current_game.SaveCurrentPlayersUseCase
import com.etologic.mahjongscoring2.business.use_cases.current_round.GameActionsUseCase
import com.etologic.mahjongscoring2.business.use_cases.current_round.PenaltyUseCase
import io.reactivex.schedulers.Schedulers

class GameActivityViewModel internal constructor(
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
    
    private val _currentPage = MutableLiveData<GameTablePages>()
    internal fun getCurrentPage(): LiveData<GameTablePages> = _currentPage
    private val _dialogToShow = MutableLiveData<GameScreens>()
    internal fun getDialogToShow(): LiveData<GameScreens> = _dialogToShow
    private val _currentTable = MutableLiveData<Table>()
    internal fun getCurrentTable(): LiveData<Table> = _currentTable
    private var _selectedSeat = MutableLiveData<TableWinds>()
    internal fun getSelectedSeat(): LiveData<TableWinds> = _selectedSeat
    internal var huPoints = 0
    
    init {
        _selectedSeat.postValue(NONE)
    }
    
    //METHODS
    internal fun loadTable() {
        disposables.add(
            getCurrentGameUseCase.getCurrentGameWithRounds()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnSuccess {
                    _currentTable.postValue(it)
                    showPlayersDialogIfProceed(it)
                }
                .subscribe({ progressState.postValue(HIDE) }, this::showError)
        )
    }
    
    private fun showPlayersDialogIfProceed(table: Table) {
        if (table.rounds.size == 1 &&
            table.game.nameP1 == "Player 1" &&
            table.game.nameP2 == "Player 2" &&
            table.game.nameP3 == "Player 3" &&
            table.game.nameP4 == "Player 4"
        ) navigateTo(PLAYERS)
    }
    
    //SELECTED PLAYER/SEAT
    internal fun onSeatClicked(wind: TableWinds) {
        _selectedSeat.postValue(wind)
        _dialogToShow.postValue(HAND_ACTION)
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
        _dialogToShow.postValue(gameScreens)
    }
    
    //GAME OPERATIONS
    internal fun resumeGame() {
        disposables.add(
            gameActionsUseCase.resume()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnSuccess(_currentTable::postValue)
                .subscribe({ progressState.postValue(HIDE) }, this::showError)
        )
    }
    
    internal fun endGame() {
        disposables.add(
            gameActionsUseCase.end()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnSuccess(_currentTable::postValue)
                .subscribe({ progressState.postValue(HIDE) }, this::showError)
        )
    }
    
    internal fun savePlayersNames(names: Array<String>) {
        disposables.add(
            saveCurrentPlayersUseCase.saveCurrentGamePlayersNames(names)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnSuccess(_currentTable::postValue)
                .subscribe({ progressState.postValue(HIDE) }, this::showError)
        )
    }
    
    //ROUND OPERATIONS
    internal fun saveRonRound(discarderCurrentSeat: TableWinds, huPoints: Int) {
        disposables.add(
            gameActionsUseCase.discard(HuData(_selectedSeat.value!!, discarderCurrentSeat, huPoints))
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnSuccess(_currentTable::postValue)
                .subscribe({ progressState.postValue(HIDE) }, this::showError)
        )
    }
    
    internal fun saveTsumoRound(huPoints: Int) {
        disposables.add(
            gameActionsUseCase.selfpick(HuData(_selectedSeat.value!!, huPoints))
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnSuccess(_currentTable::postValue)
                .subscribe({ progressState.postValue(HIDE) }, this::showError)
        )
    }
    
    internal fun saveDrawRound() {
        disposables.add(
            gameActionsUseCase.draw()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnSuccess(_currentTable::postValue)
                .subscribe({ progressState.postValue(HIDE) }, this::showError)
        )
    }
    
    internal fun savePenalty(points: Int, isDivided: Boolean) {
        disposables.add(
            penaltyUseCase.penalty(_selectedSeat.value!!, points, isDivided)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnSuccess(_currentTable::postValue)
                .subscribe({ progressState.postValue(HIDE) }, this::showError)
        )
    }
    
    internal fun cancelPenalties() {
        disposables.add(
            penaltyUseCase.cancelPenalties()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnSuccess(_currentTable::postValue)
                .subscribe({ progressState.postValue(HIDE) }, this::showError)
        )
    }
    
    internal fun removeRound(roundId: Int) {
        disposables.add(
            gameActionsUseCase.removeRound(roundId)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnSuccess(_currentTable::postValue)
                .subscribe({ progressState.postValue(HIDE) }, this::showError)
        )
    }
    
    internal fun editRound(roundId: Int) {
    
    }
    
    //QUERYS
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
            .onErrorReturnItem(arrayOf("","","",""))
            .blockingGet()
    
    internal fun getNamesByInitialPosition(): Array<String> =
        getCurrentGameUseCase.getCurrentGameWithRounds()
            .subscribeOn(Schedulers.io())
            .map { it.game.getPlayersNames() }
            .blockingGet()
}
