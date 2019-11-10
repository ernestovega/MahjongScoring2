package es.etologic.mahjongscoring2.app.game.activity

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.etologic.mahjongscoring2.app.base.BaseViewModel
import es.etologic.mahjongscoring2.app.game.game_table.RankingTableHelper
import es.etologic.mahjongscoring2.app.model.*
import es.etologic.mahjongscoring2.app.model.DialogType.*
import es.etologic.mahjongscoring2.app.model.EnablingState.ENABLED
import es.etologic.mahjongscoring2.app.model.GamePages.LIST
import es.etologic.mahjongscoring2.app.model.GamePages.TABLE
import es.etologic.mahjongscoring2.app.model.HandActions.HU
import es.etologic.mahjongscoring2.app.model.HandActions.PENALTY
import es.etologic.mahjongscoring2.app.model.SeatStates.NORMAL
import es.etologic.mahjongscoring2.app.model.SeatStates.SELECTED
import es.etologic.mahjongscoring2.app.model.ShowState.HIDE
import es.etologic.mahjongscoring2.app.model.ShowState.SHOW
import es.etologic.mahjongscoring2.app.utils.StringUtils
import es.etologic.mahjongscoring2.domain.model.GameRounds
import es.etologic.mahjongscoring2.domain.model.GameWithRounds
import es.etologic.mahjongscoring2.domain.model.RankingTable
import es.etologic.mahjongscoring2.domain.model.Round
import es.etologic.mahjongscoring2.domain.model.enums.PlayerStates
import es.etologic.mahjongscoring2.domain.model.enums.PlayerStates.PENALIZED
import es.etologic.mahjongscoring2.domain.model.enums.TableWinds
import es.etologic.mahjongscoring2.domain.model.enums.TableWinds.*
import es.etologic.mahjongscoring2.domain.model.enums.TableWinds.NONE
import es.etologic.mahjongscoring2.domain.use_cases.CreateGameUseCase
import es.etologic.mahjongscoring2.domain.use_cases.GetGamesUseCase
import es.etologic.mahjongscoring2.domain.use_cases.UpdateGameUseCase
import es.etologic.mahjongscoring2.domain.use_cases.UpdateRoundsUseCase
import io.reactivex.schedulers.Schedulers
import java.util.*

class GameActivityViewModel internal constructor(
    private val createGameUseCase: CreateGameUseCase,
    private val getGamesUseCase: GetGamesUseCase,
    private val updateRoundsUseCase: UpdateRoundsUseCase,
    private val updateGameUseCase: UpdateGameUseCase
) : BaseViewModel() {
    
    companion object {
        private const val MAX_MCR_HANDS_PER_GAME = 16
    }
    
    //List
    private val listNames = MutableLiveData<Array<String>>()
    private val listRounds = MutableLiveData<List<Round>>()
    private val listTotals = MutableLiveData<Array<String>>()
    //Table
    private val toolbarState = MutableLiveData<ToolbarState>()
    private val viewPagerPagingState = MutableLiveData<EnablingState>()
    private val currentGameViewPagerPage = MutableLiveData<GamePages>()
    private val currentWiningHandStepsViewPagerPage = MutableLiveData<WiningHandStepsPages>()
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
    private var gameId: Long = -1
    private var selectedAction: HandActions? = null
    
    //Observables
    internal fun getViewPagerPagingState(): LiveData<EnablingState> = viewPagerPagingState
    
    internal fun getToolbarState(): LiveData<ToolbarState> = toolbarState
    internal fun getCurrentViewPagerPage(): LiveData<GamePages> = currentGameViewPagerPage
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
    
    internal fun getSelectedHandAction(): HandActions? = selectedAction
    internal fun setGameId(gameId: Long) {
        this.gameId = gameId
    }
    
    //METHODS
    internal fun initGame() {
        if (gameId < 0)
            createGame()
        else
            loadGame()
    }
    
    private fun loadGame() {
        disposables.add(
            getGamesUseCase.getGame(gameId)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnEvent { _, _ -> progressState.postValue(HIDE) }
                .subscribe(this::getGameSuccess, error::postValue))
    }
    
    private fun createGame() {
        disposables.add(
            createGameUseCase.createGame()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnEvent { _, _ -> progressState.postValue(HIDE) }
                .subscribe({ this.createGameSuccess(it) }, { error.postValue(it) })
        )
    }
    
    private fun createGameSuccess(gameWithRounds: GameWithRounds) {
        getGameSuccess(gameWithRounds)
        dialogToShow.postValue(PLAYERS)
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
        val rounds = gameWithRounds.rounds
        if (rounds.size < MAX_MCR_HANDS_PER_GAME) {
            newRoundId = rounds.size + 1
            mCurrentRound = Round(gameId, newRoundId)
            resetTable()
        } else {
            mCurrentRound = rounds[rounds.size - 1]
            resetTable(true)
            dialogToShow.postValue(RANKING)
        }
    }
    
    private fun resetTable(seatsDisabled: Boolean = false) {
        selectedPlayerSeat.clear()
        mDiscarderCurrentSeat = NONE
        
        toolbarState.postValue(ToolbarState.NORMAL)
        viewPagerPagingState.postValue(ENABLED)
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
        val initialPosition = GameRounds.getPlayerInitialSeatByCurrentSeat(wind, mCurrentRound.roundId)
        val name = gameWithRounds.game.getPlayerNameByInitialPosition(initialPosition)
        val points = gameWithRounds.getPlayersTotalPoints()[initialPosition.code]
        val penaltyPoints = mCurrentRound.getPenaltyPointsFromInitialPlayerPosition(initialPosition)
        return Seat(wind, name, points, penaltyPoints, if (isDisabled) SeatStates.DISABLED else NORMAL)
    }
    //region DIALOGS
    //Players names
    internal fun savePlayersNames(tiet1Text: Editable?, tiet2Text: Editable?, tiet3Text: Editable?, tiet4Text: Editable?) {
        val name1 = tiet1Text?.toString() ?: ""
        val name2 = tiet2Text?.toString() ?: ""
        val name3 = tiet3Text?.toString() ?: ""
        val name4 = tiet4Text?.toString() ?: ""
        if (!StringUtils.isEmpty(name1) && !StringUtils.isEmpty(name2) && !StringUtils.isEmpty(name3) && !StringUtils.isEmpty(name4)) {
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
        } else {
            dialogToShow.postValue(PLAYERS)
        }
    }
    
    //RequestHandPoints  Dialog Responses
    
    //RequestPenaltyPoints Dialog Responses
    internal fun onRequestPenaltyResponse(newPenaltyPointsInput: CharSequence?, isDividedEqually: Boolean) {
        val penaltyPointsInput = newPenaltyPointsInput ?: "0"
        val penaltyPoints = convertInputValue(penaltyPointsInput.toString())
        if (penaltyPoints == null || penaltyPoints <= 0) {
            dialogToShow.postValue(WINING_HAND)
        } else {
            if (isDividedEqually) {
                if (penaltyPoints % 3 == 0) {
                    mCurrentRound.setAllPlayersPenaltyPoints(selectedPlayerSeat.initialSeat, penaltyPoints)
                    resetTable()
                } else {
                    dialogToShow.postValue(WINING_HAND)
                }
            } else {
                mCurrentRound.setPlayerPenaltyPoints(selectedPlayerSeat.initialSeat, penaltyPoints)
                resetTable()
            }
        }
    }
    
    private fun convertInputValue(penaltyPointsInput: String): Int? {
        try {
            return Integer.valueOf(penaltyPointsInput)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        
    }
    //endregion
    
    //Seats
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
    
    //Actions
    fun onPenaltyCancelClicked() {
        mCurrentRound.cancelAllPlayersPenalties()
        resetTable()
    }
    
    fun onPenaltyClicked() {
        selectedAction = PENALTY
        dialogToShow.postValue(WINING_HAND)
    }
    
    fun onHuClicked() {
        selectedAction = HU
        dialogToShow.postValue(WINING_HAND)
    }
    
    fun onWashoutClicked() {
        saveCurrentRoundAndStartNext()
    }
    
    private fun saveCurrentRoundAndStartNext() {
        mCurrentRound.applyAllPlayersPenalties()
        //        mCurrentRound.setRoundDuration();
        disposables.add(
            updateRoundsUseCase.addRound(mCurrentRound)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnEvent { _, _ -> progressState.postValue(HIDE) }
                .subscribe({ this.getGameSuccess(it) }, { error.postValue(it) })
        )
    }
    
    fun diceClicked() {
        dialogToShow.postValue(DICE)
    }
    
    fun rankingClicked() {
        dialogToShow.postValue(RANKING)
    }
    
    internal fun saveRonRound(requestedPoints: Int) {
        mCurrentRound.handPoints = requestedPoints
        mCurrentRound.winnerInitialPosition = selectedPlayerSeat.initialSeat
        val discarderInitialSeat = GameRounds.getPlayerInitialSeatByCurrentSeat(mDiscarderCurrentSeat, mCurrentRound.roundId)
        mCurrentRound.discarderInitialPosition = discarderInitialSeat
        mCurrentRound.setAllPlayersRonPoints(selectedPlayerSeat.initialSeat, requestedPoints, discarderInitialSeat)
        saveCurrentRoundAndStartNext()
    }
    
    internal fun saveTsumoRound(requestedPoints: Int) {
        mCurrentRound.handPoints = requestedPoints
        mCurrentRound.winnerInitialPosition = selectedPlayerSeat.initialSeat
        mCurrentRound.setAllPlayersTsumoPoints(selectedPlayerSeat.initialSeat, requestedPoints)
        saveCurrentRoundAndStartNext()
    }
    
    //Others
    fun setCurrentGameViewPagerPage(page: GamePages) {
        currentGameViewPagerPage.postValue(page)
    }
    
    fun setCurrentWiningHandStepsViewPagerPage(page: WiningHandStepsPages) {
        currentWiningHandStepsViewPagerPage.postValue(page)
    }
    
    fun resumeGame() {
        //ToDo
    }
    
    fun onBackPressed() {
        if (currentGameViewPagerPage.value === LIST)
            currentGameViewPagerPage.postValue(TABLE)
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
    
    fun getRankingTable(): RankingTable? = RankingTableHelper.generateRankingTable(gameWithRounds)
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
