package com.etologic.mahjongscoring2.app.game.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.etologic.mahjongscoring2.app.base.BaseViewModel
import com.etologic.mahjongscoring2.app.game.game_table.RankingTableHelper.generateRankingTable
import com.etologic.mahjongscoring2.app.model.DialogType
import com.etologic.mahjongscoring2.app.model.DialogType.*
import com.etologic.mahjongscoring2.app.model.GamePages
import com.etologic.mahjongscoring2.app.model.GamePages.LIST
import com.etologic.mahjongscoring2.app.model.GamePages.TABLE
import com.etologic.mahjongscoring2.app.model.Seat
import com.etologic.mahjongscoring2.app.model.SeatStates.*
import com.etologic.mahjongscoring2.app.model.ShowState.HIDE
import com.etologic.mahjongscoring2.app.model.ShowState.SHOW
import com.etologic.mahjongscoring2.app.utils.GameRoundsUtils
import com.etologic.mahjongscoring2.business.model.dtos.HuData
import com.etologic.mahjongscoring2.business.model.dtos.RankingData
import com.etologic.mahjongscoring2.business.model.entities.GameWithRounds
import com.etologic.mahjongscoring2.business.model.entities.Round
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.*
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NONE
import com.etologic.mahjongscoring2.business.use_cases.current_game.GetCurrentGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.current_game.SaveCurrentPlayersUseCase
import com.etologic.mahjongscoring2.business.use_cases.current_round.DrawUseCase
import com.etologic.mahjongscoring2.business.use_cases.current_round.HuUseCase
import com.etologic.mahjongscoring2.business.use_cases.current_round.PenaltyUseCase
import io.reactivex.schedulers.Schedulers

class GameActivityViewModel internal constructor(
    private val getCurrentGameUseCase: GetCurrentGameUseCase,
    private val saveCurrentPlayersUseCase: SaveCurrentPlayersUseCase,
    private val huUseCase: HuUseCase,
    private val drawUseCase: DrawUseCase,
    private val penaltyUseCase: PenaltyUseCase
) : BaseViewModel() {
    
    //List
    private val _listNames = MutableLiveData<Array<String>>()
    internal fun getListNames(): LiveData<Array<String>> = _listNames
    private val _listRounds = MutableLiveData<List<Round>>()
    internal fun getListRounds(): LiveData<List<Round>> = _listRounds
    private val _listTotals = MutableLiveData<Array<String>>()
    internal fun getListTotals(): LiveData<Array<String>> = _listTotals
    
    //Table
    private val _eastSeat = MutableLiveData<Seat>()
    internal fun getEastSeat(): LiveData<Seat> = _eastSeat
    private val _southSeat = MutableLiveData<Seat>()
    internal fun getSouthSeat(): LiveData<Seat> = _southSeat
    private val _westSeat = MutableLiveData<Seat>()
    internal fun getWestSeat(): LiveData<Seat> = _westSeat
    private val _northSeat = MutableLiveData<Seat>()
    internal fun getNorthSeat(): LiveData<Seat> = _northSeat
    private val _currentRound = MutableLiveData<Round>()
    internal fun getCurrentRound(): LiveData<Round> = _currentRound
    
    //Navigation
    private val _currentPage = MutableLiveData<GamePages>()
    internal fun getCurrentPage(): LiveData<GamePages> = _currentPage
    private val _dialogToShow = MutableLiveData<DialogType>()
    internal fun getDialogToShow(): LiveData<DialogType> = _dialogToShow
    private val _rankingData = MutableLiveData<RankingData>()
    internal fun getRankingData(): LiveData<RankingData> = _rankingData
    
    //METHODS
    internal fun loadGame() {
        disposables.add(
            getCurrentGameUseCase.getCurrentGameWithRounds()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnSuccess { _listNames.postValue(it.game.getPlayersNames()) }
                .doOnSuccess { _listRounds.postValue(it.getRoundsWithBestHand()) }
                .doOnSuccess { _listTotals.postValue(it.getPlayersTotalPointsString()) }
                .doOnSuccess { this.resetTable(it) }
                .subscribe({ progressState.postValue(HIDE) }, this::showError)
        )
    }
    
    private fun resetTable(gameWithRounds: GameWithRounds) {
        val currentRound = gameWithRounds.rounds.last()
        _currentRound.postValue(currentRound)
        _eastSeat.postValue(buildNewSeat(gameWithRounds, EAST))
        _southSeat.postValue(buildNewSeat(gameWithRounds, SOUTH))
        _westSeat.postValue(buildNewSeat(gameWithRounds, WEST))
        _northSeat.postValue(buildNewSeat(gameWithRounds, NORTH))
        if (currentRound.isEnded || currentRound.roundId == 16)
            setSeatsDisabled()
    }
    
    private fun setSeatsDisabled() {
        _eastSeat.value?.let {
            it.state = DISABLED
            _eastSeat.postValue(it)
        }
        _southSeat.value?.let {
            it.state = DISABLED
            _eastSeat.postValue(it)
        }
        _westSeat.value?.let {
            it.state = DISABLED
            _eastSeat.postValue(it)
        }
        _northSeat.value?.let {
            it.state = DISABLED
            _eastSeat.postValue(it)
        }
    }
    
    private fun buildNewSeat(gameWithRounds: GameWithRounds, wind: TableWinds): Seat {
        val initialPosition = GameRoundsUtils.getPlayerInitialSeatByCurrentSeat(wind, gameWithRounds.rounds.last().roundId)
        val name = gameWithRounds.game.getPlayerNameByInitialPosition(initialPosition)
        val points = gameWithRounds.getPlayersTotalPoints()[initialPosition.code]
        val penaltyPoints = gameWithRounds.rounds.last().getPenaltyPointsFromInitialPlayerPosition(initialPosition)
        return Seat(wind, name, points, penaltyPoints, DISABLED)
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
            _eastSeat.postValue(it)
        }
        _westSeat.value?.let {
            it.state = if (currentSeat == WEST) SELECTED else NORMAL
            _eastSeat.postValue(it)
        }
        _northSeat.value?.let {
            it.state = if (currentSeat == NORTH) SELECTED else NORMAL
            _eastSeat.postValue(it)
        }
    }
    
    //NAVIGATION
    internal fun showPage(page: GamePages) {
        _currentPage.postValue(page)
    }
    
    internal fun onBackPressed() {
        if (_currentPage.value === LIST)
            _currentPage.postValue(TABLE)
        else
            _dialogToShow.postValue(EXIT)
    }
    
    internal fun loadRankingData() {
        disposables.add(
            getCurrentGameUseCase.getCurrentGameWithRounds()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnSuccess{ _rankingData.postValue(generateRankingTable(it)) }
                .subscribe({ progressState.postValue(HIDE) }, this::showError)
        )
    }
    
    internal fun showDialog(dialogType: DialogType) {
        _dialogToShow.postValue(dialogType)
    }
    
    internal fun savePlayersNames(names: Array<String>) {
        disposables.add(
            saveCurrentPlayersUseCase.saveCurrentGamePlayersNames(names)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnSuccess(this::resetTable)
                .subscribe({ progressState.postValue(HIDE) }, this::showError)
        )
    }
    
    internal fun saveRonRound(points: Int, discarderCurrentSeat: TableWinds) {
        disposables.add(
            huUseCase.discard(HuData(getSelectedSeat(), discarderCurrentSeat, points))
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnSuccess(this::resetTable)
                .subscribe({ progressState.postValue(HIDE) }, this::showError)
        )
    }
    
    internal fun saveTsumoRound(points: Int) {
        disposables.add(
            huUseCase.selfpick(HuData(getSelectedSeat(), points))
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnSuccess(this::resetTable)
                .subscribe({ progressState.postValue(HIDE) }, this::showError)
        )
    }
    
    internal fun saveDrawRound() {
        disposables.add(
            drawUseCase.draw()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnSuccess(this::resetTable)
                .subscribe({ progressState.postValue(HIDE) }, this::showError)
        )
    }
    
    internal fun savePenalty(penalizedPlayerCurrentSeat: TableWinds, points: Int, isDivided: Boolean) {
        disposables.add(
            penaltyUseCase.penalty(penalizedPlayerCurrentSeat, points, isDivided)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnSuccess(this::resetTable)
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
}
