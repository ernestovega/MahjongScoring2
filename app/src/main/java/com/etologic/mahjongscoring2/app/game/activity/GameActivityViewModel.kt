package com.etologic.mahjongscoring2.app.game.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.etologic.mahjongscoring2.app.base.BaseViewModel
import com.etologic.mahjongscoring2.app.game.activity.GameActivityViewModel.GameScreens.*
import com.etologic.mahjongscoring2.app.game.dialogs.ranking.RankingTableHelper.generateRankingTable
import com.etologic.mahjongscoring2.app.game.game_table.GameTableFragment.GameTablePages
import com.etologic.mahjongscoring2.app.model.Seat
import com.etologic.mahjongscoring2.app.model.SeatStates.*
import com.etologic.mahjongscoring2.app.model.ShowState.HIDE
import com.etologic.mahjongscoring2.app.model.ShowState.SHOW
import com.etologic.mahjongscoring2.business.model.dtos.HuData
import com.etologic.mahjongscoring2.business.model.dtos.RankingData
import com.etologic.mahjongscoring2.business.model.entities.GameWithRounds
import com.etologic.mahjongscoring2.business.model.entities.Round
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.*
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
    
    //Navigation
    private val _currentPage = MutableLiveData<GameTablePages>()
    internal fun getCurrentPage(): LiveData<GameTablePages> = _currentPage
    private val _dialogToShow = MutableLiveData<GameScreens>()
    internal fun getDialogToShow(): LiveData<GameScreens> = _dialogToShow
    private val _rankingData = MutableLiveData<RankingData>()
    internal fun getRankingData(): LiveData<RankingData> = _rankingData
    //List
    private val _listNames = MutableLiveData<Array<String>>()
    internal fun getListNames(): LiveData<Array<String>> = _listNames
    private val _listRounds = MutableLiveData<List<Round>>()
    internal fun getListRounds(): LiveData<List<Round>> = _listRounds
    private val _listTotals = MutableLiveData<Array<String>>()
    internal fun getListTotals(): LiveData<Array<String>> = _listTotals
    //Table
    private val _eastSeat = MutableLiveData<Seat>()
    internal fun eastSeatObservable(): LiveData<Seat> = _eastSeat
    private val _southSeat = MutableLiveData<Seat>()
    internal fun southSeatObservable(): LiveData<Seat> = _southSeat
    private val _westSeat = MutableLiveData<Seat>()
    internal fun westSeatObservable(): LiveData<Seat> = _westSeat
    private val _northSeat = MutableLiveData<Seat>()
    internal fun northSeatObservable(): LiveData<Seat> = _northSeat
    private val _currentRound = MutableLiveData<Round>()
    internal fun getCurrentRound(): LiveData<Round> = _currentRound
    
    //DTOs
    internal var listNamesByCurrentSeat: Array<String>? = null
    internal var huPoints: Int? = null
    
    //METHODS
    internal fun loadGame() {
        disposables.add(
            getCurrentGameUseCase.getCurrentGameWithRounds()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnSuccess {
                    showPlayersDialogIfProceed(it)
                    updateTableAndList(it)
                }
                .subscribe({ progressState.postValue(HIDE) }, this::showError)
        )
    }
    
    private fun showPlayersDialogIfProceed(gameWithRounds: GameWithRounds) {
        if (gameWithRounds.rounds.size == 1 &&
            gameWithRounds.game.nameP1 == "Player 1" &&
            gameWithRounds.game.nameP2 == "Player 2" &&
            gameWithRounds.game.nameP3 == "Player 3" &&
            gameWithRounds.game.nameP4 == "Player 4"
        ) navigateTo(PLAYERS)
    }
    
    private fun updateTableAndList(gameWithRounds: GameWithRounds) {
        updateList(gameWithRounds)
        updateTable(gameWithRounds)
        listNamesByCurrentSeat = gameWithRounds.getPlayersNamesByCurrentSeat()
    }
    
    private fun updateList(it: GameWithRounds) {
        _listNames.postValue(it.game.getPlayersNames())
        _listRounds.postValue(it.getEndedRoundsWithBestHand())
        _listTotals.postValue(it.getPlayersTotalPointsString())
    }
    
    private fun updateTable(gameWithRounds: GameWithRounds) {
        val currentRound = gameWithRounds.rounds.last()
        updateRoundStuff(currentRound)
        updateSeats(gameWithRounds, currentRound.isEnded)
    }
    
    private fun updateRoundStuff(currentRound: Round) {
        _currentRound.postValue(currentRound)
    }
    
    private fun updateSeats(gameWithRounds: GameWithRounds, isEnded: Boolean) {
        val newEastSeat = buildNewSeat(gameWithRounds, EAST)
        val newSouthSeat = buildNewSeat(gameWithRounds, SOUTH)
        val newWestSeat = buildNewSeat(gameWithRounds, WEST)
        val newNorthSeat = buildNewSeat(gameWithRounds, NORTH)
    
        if (isEnded) {
            setSeatDisabled(newEastSeat, _eastSeat)
            setSeatDisabled(newSouthSeat, _southSeat)
            setSeatDisabled(newWestSeat, _westSeat)
            setSeatDisabled(newNorthSeat, _northSeat)
            _dialogToShow.postValue(RANKING)
        }
    
        _eastSeat.postValue(newEastSeat)
        _southSeat.postValue(newSouthSeat)
        _westSeat.postValue(newWestSeat)
        _northSeat.postValue(newNorthSeat)
    }
    
    private fun buildNewSeat(gameWithRounds: GameWithRounds, wind: TableWinds): Seat {
        val initialPosition = gameWithRounds.getPlayerInitialSeatByCurrentSeat(wind, gameWithRounds.rounds.last().roundId)
        val name = gameWithRounds.game.getPlayerNameByInitialPosition(initialPosition)
        val points = gameWithRounds.getPlayersTotalPoints()[initialPosition.code]
        val penaltyPoints = gameWithRounds.rounds.last().getPenaltyPointsFromInitialPlayerPosition(initialPosition)
        return Seat(wind, name, points, penaltyPoints, NORMAL)
    }
    
    private fun setSeatDisabled(seat: Seat, mutableLiveData: MutableLiveData<Seat>) {
        seat.state = DISABLED
        seat.wind = NONE
        mutableLiveData.postValue(seat)
    }
    
    //SELECTED PLAYER/SEAT
    internal fun onSeatClicked(seatPosition: TableWinds) {
        setSeatsSelected(seatPosition)
        _dialogToShow.postValue(HAND_ACTION)
    }
    
    private fun setSeatsSelected(currentSeat: TableWinds) {
        _eastSeat.value?.let {
            it.state = if (currentSeat == EAST) SELECTED else NORMAL
            _eastSeat.postValue(it)
        }
        _southSeat.value?.let {
            it.state = if (currentSeat == SOUTH) SELECTED else NORMAL
            _southSeat.postValue(it)
        }
        _westSeat.value?.let {
            it.state = if (currentSeat == WEST) SELECTED else NORMAL
            _westSeat.postValue(it)
        }
        _northSeat.value?.let {
            it.state = if (currentSeat == NORTH) SELECTED else NORMAL
            _northSeat.postValue(it)
        }
    }
    
    private fun unselectedSelectedSeat() {
        when (getSelectedSeat()) {
            EAST -> _eastSeat.value?.let {
                it.state = NORMAL
                _eastSeat.postValue(it)
            }
            SOUTH -> _southSeat.value?.let {
                it.state = NORMAL
                _southSeat.postValue(it)
            }
            WEST -> _westSeat.value?.let {
                it.state = NORMAL
                _westSeat.postValue(it)
            }
            NORTH -> _northSeat.value?.let {
                it.state = NORMAL
                _northSeat.postValue(it)
            }
            NONE -> {
                loadGame()
            }
        }
    }
    
    //NAVIGATION
    internal fun showPage(page: GameTablePages) {
        _currentPage.postValue(page)
    }
    
    internal fun navigateTo(gameScreens: GameScreens) {
        _dialogToShow.postValue(gameScreens)
    }
    
    //OPERATIONS
    internal fun resumeGame() {
        disposables.add(
            gameActionsUseCase.resume()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnSuccess(this::updateTableAndList)
                .subscribe({ progressState.postValue(HIDE) }, this::showError)
        )
    }
    
    internal fun endGame() {
        disposables.add(
            gameActionsUseCase.end()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnSuccess(this::updateTableAndList)
                .subscribe({ progressState.postValue(HIDE) }, this::showError)
        )
    }
    
    internal fun savePlayersNames(names: Array<String>) {
        disposables.add(
            saveCurrentPlayersUseCase.saveCurrentGamePlayersNames(names)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnSuccess(this::updateTableAndList)
                .subscribe({ progressState.postValue(HIDE) }, this::showError)
        )
    }
    
    internal fun saveRonRound(discarderCurrentSeat: TableWinds, huPoints: Int) {
        disposables.add(
            gameActionsUseCase.discard(HuData(getSelectedSeat(), discarderCurrentSeat, huPoints))
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnSuccess(this::updateTableAndList)
                .subscribe({ progressState.postValue(HIDE) }, this::showError)
        )
    }
    
    internal fun saveTsumoRound(huPoints: Int) {
        disposables.add(
            gameActionsUseCase.selfpick(HuData(getSelectedSeat(), huPoints))
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnSuccess(this::updateTableAndList)
                .subscribe({ progressState.postValue(HIDE) }, this::showError)
        )
    }
    
    internal fun saveDrawRound() {
        disposables.add(
            gameActionsUseCase.draw()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnSuccess(this::updateTableAndList)
                .subscribe({ progressState.postValue(HIDE) }, this::showError)
        )
    }
    
    internal fun savePenalty(points: Int, isDivided: Boolean) {
        disposables.add(
            penaltyUseCase.penalty(getSelectedSeat(), points, isDivided)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnSuccess(this::updateTableAndList)
                .subscribe({ progressState.postValue(HIDE) }, this::showError)
        )
    }
    
    internal fun cancelPenalties() {
        disposables.add(
            penaltyUseCase.cancelPenalties()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnSuccess(this::updateTableAndList)
                .subscribe({ progressState.postValue(HIDE) }, this::showError)
        )
    }
    
    internal fun loadRankingData() {
        disposables.add(
            getCurrentGameUseCase.getCurrentGameWithRounds()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnSuccess { _rankingData.postValue(generateRankingTable(it)) }
                .subscribe({ progressState.postValue(HIDE) }, this::showError)
        )
    }
    
    internal fun getSelectedSeat(): TableWinds =
        when {
            _eastSeat.value?.state == SELECTED -> EAST
            _southSeat.value?.state == SELECTED -> SOUTH
            _westSeat.value?.state == SELECTED -> WEST
            _northSeat.value?.state == SELECTED -> NORTH
            else -> NONE
        }
    
    internal fun handDialogCanceled() {
        unselectedSelectedSeat()
    }
    
    internal fun thereArePenaltiesCurrently(): Boolean =
        (_eastSeat.value?.penalty ?: 0) != 0 ||
            (_southSeat.value?.penalty ?: 0) != 0 ||
            (_westSeat.value?.penalty ?: 0) != 0 ||
            (_northSeat.value?.penalty ?: 0) != 0
}
