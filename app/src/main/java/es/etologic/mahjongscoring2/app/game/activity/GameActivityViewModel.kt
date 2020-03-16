package es.etologic.mahjongscoring2.app.game.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.etologic.mahjongscoring2.app.base.BaseViewModel
import es.etologic.mahjongscoring2.app.game.game_table.RankingTableHelper
import es.etologic.mahjongscoring2.app.model.*
import es.etologic.mahjongscoring2.app.model.DialogType.*
import es.etologic.mahjongscoring2.app.model.GamePages.LIST
import es.etologic.mahjongscoring2.app.model.GamePages.TABLE
import es.etologic.mahjongscoring2.app.model.SeatStates.NORMAL
import es.etologic.mahjongscoring2.app.model.SeatStates.SELECTED
import es.etologic.mahjongscoring2.app.model.ShowState.HIDE
import es.etologic.mahjongscoring2.app.model.ShowState.SHOW
import es.etologic.mahjongscoring2.app.utils.GameRoundsUtils
import es.etologic.mahjongscoring2.domain.model.GameWithRounds
import es.etologic.mahjongscoring2.domain.model.RankingTable
import es.etologic.mahjongscoring2.domain.model.Round
import es.etologic.mahjongscoring2.domain.model.enums.PlayerStates
import es.etologic.mahjongscoring2.domain.model.enums.PlayerStates.PENALIZED
import es.etologic.mahjongscoring2.domain.model.enums.TableWinds
import es.etologic.mahjongscoring2.domain.model.enums.TableWinds.*
import es.etologic.mahjongscoring2.domain.model.enums.TableWinds.NONE
import es.etologic.mahjongscoring2.domain.use_cases.GetCurrentGameUseCase
import es.etologic.mahjongscoring2.domain.use_cases.UpdateGameUseCase
import io.reactivex.schedulers.Schedulers
import java.util.*

class GameActivityViewModel internal constructor(
    private val getCurrentGameUseCase: GetCurrentGameUseCase,
    private val updateGameUseCase: UpdateGameUseCase
) : BaseViewModel() {
    
    companion object {
        private const val MAX_MCR_HANDS_PER_GAME = 16
    }
    
    //List
    internal val listNames = MutableLiveData<Array<String>>()
    private val listRounds = MutableLiveData<List<Round>>()
    private val listTotals = MutableLiveData<Array<String>>()
    
    //Table
    private val currentPage = MutableLiveData<GamePages>()
    private val eastSeat = MutableLiveData<Seat>()
    private val southSeat = MutableLiveData<Seat>()
    private val westSeat = MutableLiveData<Seat>()
    private val northSeat = MutableLiveData<Seat>()
    private val roundNumber = MutableLiveData<Int>()
    private val dialogToShow = MutableLiveData<DialogType>()
    private val endGameState = MutableLiveData<Boolean>()
    private lateinit var gameWithRounds: GameWithRounds
    private var mCurrentRound = Round(0, 0)
    private var selectedPlayerSeat = SelectedPlayerSeat()
    private var mDiscarderCurrentSeat = NONE
    
    //Observables
    internal fun getCurrentViewPagerPage(): LiveData<GamePages> = currentPage
    internal fun getShowDialog(): LiveData<DialogType> = dialogToShow
    internal fun getEndGameState(): LiveData<Boolean> = endGameState
    
    //list
    fun getListNames(): LiveData<Array<String>> = listNames
    
    internal fun getListRounds(): LiveData<List<Round>> = listRounds
    internal fun getListTotals(): LiveData<Array<String>> = listTotals
    
    //Table
    internal fun getEastSeat(): LiveData<Seat> = eastSeat
    internal fun getSouthSeat(): LiveData<Seat> = southSeat
    internal fun getWestSeat(): LiveData<Seat> = westSeat
    internal fun getNorthSeat(): LiveData<Seat> = northSeat
    internal fun getRoundNumber(): LiveData<Int> = roundNumber
    
    //METHODS
    internal fun loadGame() {
        disposables.add(
            getCurrentGameUseCase.getCurrentGameWithRounds()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnEvent { _, _ -> progressState.postValue(HIDE) }
                .subscribe(this::getGameSuccess, error::postValue))
    }
    
    private fun getGameSuccess(gameWithRounds: GameWithRounds) {
        this.gameWithRounds = gameWithRounds
        listNames.postValue(gameWithRounds.game.getPlayersNames())
        listRounds.postValue(gameWithRounds.getRoundsWithBestHand())
        listTotals.postValue(gameWithRounds.getPlayersTotalPointsString())
        createRoundOrFinish(gameWithRounds)
    }
    
    private fun createRoundOrFinish(gameWithRounds: GameWithRounds) {
        val gameId = gameWithRounds.game.gameId
        val newRoundId: Int
        when {
            gameWithRounds.rounds.isEmpty() -> dialogToShow.postValue(PLAYERS)
            gameWithRounds.rounds.size < MAX_MCR_HANDS_PER_GAME -> {
                newRoundId = gameWithRounds.rounds.size + 1
                mCurrentRound = Round(gameId, newRoundId)
                resetTable()
            }
            else -> {
                mCurrentRound = gameWithRounds.rounds[gameWithRounds.rounds.size - 1]
                resetTable(true)
                dialogToShow.postValue(RANKING)
            }
        }
    }
    
    private fun resetTable(seatsDisabled: Boolean = false) {
        selectedPlayerSeat.clear()
        mDiscarderCurrentSeat = NONE
        
        dialogToShow.postValue(DialogType.NONE)
        
        roundNumber.postValue(mCurrentRound.roundId)
        resetSeats(seatsDisabled)
    }
    
    private fun resetSeats(seatsDisabled: Boolean) {
        eastSeat.postValue(buildNewSeat(EAST, seatsDisabled))
        southSeat.postValue(buildNewSeat(SOUTH, seatsDisabled))
        westSeat.postValue(buildNewSeat(WEST, seatsDisabled))
        northSeat.postValue(buildNewSeat(NORTH, seatsDisabled))
    }
    
    private fun buildNewSeat(wind: TableWinds, isDisabled: Boolean): Seat {
        val initialPosition = GameRoundsUtils.getPlayerInitialSeatByCurrentSeat(wind, mCurrentRound.roundId)
        val name = gameWithRounds.game.getPlayerNameByInitialPosition(initialPosition)
        val points = gameWithRounds.getPlayersTotalPoints()[initialPosition.code]
        val penaltyPoints = mCurrentRound.getPenaltyPointsFromInitialPlayerPosition(initialPosition)
        return Seat(wind, name, points, penaltyPoints, if (isDisabled) SeatStates.DISABLED else NORMAL)
    }
    
    internal fun savePlayersNames(name1: String, name2: String, name3: String, name4: String) {
        gameWithRounds.game.nameP1 = name1
        gameWithRounds.game.nameP2 = name2
        gameWithRounds.game.nameP3 = name3
        gameWithRounds.game.nameP4 = name4
        listNames.postValue(gameWithRounds.game.getPlayersNames())
        resetTable()
        disposables.add(
            updateGameUseCase.updateGame(gameWithRounds.game)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnEvent { _, _ -> progressState.postValue(HIDE) }
                .subscribe({}, { error.postValue(it) })
        )
    }
    
    //region SEATS ACTIONS
    fun onSeatClicked(seatPosition: TableWinds) {
        setSelectedPlayerSeat(seatPosition)
        dialogToShow.postValue(HAND_ACTION)
    }
    
    internal fun getSelectedPlayerState(): PlayerStates {
        val isPlayerPenalized = mCurrentRound.isPenalizedPlayer(selectedPlayerSeat.initialSeat)
        return if (isPlayerPenalized)
            PENALIZED
        else
            PlayerStates.SELECTED
    }
    
    private fun setSelectedPlayerSeat(seatPosition: TableWinds) {
        selectedPlayerSeat.setSelectedPlayer(seatPosition, mCurrentRound.roundId)
        setSeatSelected(seatPosition)
    }
    
    private fun setSeatSelected(seatPosition: TableWinds) {
        when (seatPosition) {
            NONE -> unselectAllSeats()
            EAST -> {
                unselectAllSeats()
                val eastSeatValue = eastSeat.value
                eastSeatValue?.let {
                    it.state = SELECTED
                    eastSeat.postValue(eastSeatValue)
                }
            }
            SOUTH -> {
                unselectAllSeats()
                val southSeatValue = southSeat.value
                southSeatValue?.let {
                    it.state = SELECTED
                    southSeat.postValue(southSeatValue)
                }
            }
            WEST -> {
                unselectAllSeats()
                val westSeatValue = westSeat.value
                westSeatValue?.let {
                    it.state = SELECTED
                    westSeat.postValue(westSeatValue)
                }
            }
            NORTH -> {
                unselectAllSeats()
                val northSeatValue = northSeat.value
                northSeatValue?.let {
                    it.state = SELECTED
                    northSeat.postValue(northSeatValue)
                }
            }
        }
    }
    
    private fun unselectAllSeats() {
        val eastSeatValue = eastSeat.value
        eastSeatValue?.let {
            it.state = NORMAL
            eastSeat.postValue(eastSeatValue)
        }
        val southSeatValue = southSeat.value
        southSeatValue?.let {
            it.state = NORMAL
            southSeat.postValue(southSeatValue)
        }
        val westSeatValue = westSeat.value
        westSeatValue?.let {
            it.state = NORMAL
            westSeat.postValue(westSeatValue)
        }
        val northSeatValue = northSeat.value
        northSeatValue?.let {
            it.state = NORMAL
            northSeat.postValue(northSeatValue)
        }
    }
    //endregion
    internal fun showDialog(dialogType: DialogType) {
        dialogToShow.postValue(dialogType)
    }
    
    internal fun showPage(page: GamePages) {
        currentPage.postValue(page)
    }
    
    internal fun resumeGame() {
        //ToDo
    }
    
    internal fun onBackPressed() {
        if (currentPage.value === LIST)
            currentPage.postValue(TABLE)
        else
            dialogToShow.postValue(EXIT)
    }
    
    internal fun endGame() {
        gameWithRounds.game.endDate = Date()
        disposables.add(
            updateGameUseCase.updateGame(gameWithRounds.game)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnEvent { _, _ -> progressState.postValue(HIDE) }
                .subscribe({ endGameState.postValue(true) }, { endGameState.postValue(false) })
        )
    }
    
    internal fun getRankingTable(): RankingTable? = RankingTableHelper.generateRankingTable(gameWithRounds)
    
    internal fun setCurrentPenaltyPoints(penaltyPoints: Int, isDivided: Boolean) {
        if (isDivided)
            mCurrentRound.setAllPlayersPenaltyPoints(selectedPlayerSeat.initialSeat, penaltyPoints)
        else
            mCurrentRound.setPlayerPenaltyPoints(selectedPlayerSeat.initialSeat, penaltyPoints)
        resetTable()
    }
}
//public void onRoundSwiped(int roundId) {
//    try {
//        mInteractor.deleteRound(mGame.getId(), roundId);
//        refreshFragment();
//        if(mView != null) {
//            mView.refreshSeats();
//        }
//    } catch(DBException e) {
//        if(mView != null) {
//            mView.showDialogFinalizeGame(e.getMessage());
//        }
//    }
//}
