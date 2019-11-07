package es.etologic.mahjongscoring2.app.game.activity

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.etologic.mahjongscoring2.app.base.BaseViewModel
import es.etologic.mahjongscoring2.app.game.game_table.RankingTableHelper
import es.etologic.mahjongscoring2.app.model.*
import es.etologic.mahjongscoring2.app.model.EnablingState.ENABLED
import es.etologic.mahjongscoring2.app.model.GamePages.LIST
import es.etologic.mahjongscoring2.app.model.GamePages.TABLE
import es.etologic.mahjongscoring2.app.model.SeatStates.NORMAL
import es.etologic.mahjongscoring2.app.model.SeatStates.SELECTED
import es.etologic.mahjongscoring2.app.model.ShowState.HIDE
import es.etologic.mahjongscoring2.app.model.ShowState.SHOW
import es.etologic.mahjongscoring2.app.utils.StringUtils
import es.etologic.mahjongscoring2.domain.model.GameRounds
import es.etologic.mahjongscoring2.domain.model.GameWithRounds
import es.etologic.mahjongscoring2.domain.model.RankingTable
import es.etologic.mahjongscoring2.domain.model.Round
import es.etologic.mahjongscoring2.domain.model.enums.FabMenuStates
import es.etologic.mahjongscoring2.domain.model.enums.FabMenuStates.*
import es.etologic.mahjongscoring2.domain.model.enums.TableWinds
import es.etologic.mahjongscoring2.domain.model.enums.TableWinds.*
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
        private const val MCR_MIN_POINTS = 8
        private const val MAX_MCR_HANDS_PER_GAME = 16
    }
    
    //List
    val listNames = MutableLiveData<Array<String>>()
    val listRounds = MutableLiveData<List<Round>>()
    val listTotals = MutableLiveData<Array<String>>()
    //Table
    val toolbarState = MutableLiveData<ToolbarState>()
    val viewPagerPagingState = MutableLiveData<EnablingState>()
    val currentViewPagerPage = MutableLiveData<GamePages>()
    val fabMenuState = MutableLiveData<FabMenuStates>()
    val fabMenuOpenState = MutableLiveData<ShowState>()
    val eastSeat = MutableLiveData<Seat>()
    val southSeat = MutableLiveData<Seat>()
    val westSeat = MutableLiveData<Seat>()
    val northSeat = MutableLiveData<Seat>()
    val roundNumber = MutableLiveData<Int>()
    val showDialog = MutableLiveData<DialogType>()
    val endGameState = MutableLiveData<Boolean>()
    private lateinit var gameWithRounds: GameWithRounds
    private var mCurrentRound = Round(0, 0)
    private var tableState = TableStates.NORMAL
    private var selectedPlayerSeat = SelectedPlayerSeat()
    private var mDiscarderCurrentSeat = NONE
    
    //Observables
    fun getListNames(): LiveData<Array<String>> = listNames
    
    internal fun getListRounds(): LiveData<List<Round>> = listRounds
    internal fun getListTotals(): LiveData<Array<String>> = listTotals
    internal fun getViewPagerPagingState(): LiveData<EnablingState> = viewPagerPagingState
    internal fun getToolbarState(): LiveData<ToolbarState> = toolbarState
    internal fun getCurrentViewPagerPage(): LiveData<GamePages> = currentViewPagerPage
    internal fun getEastSeat(): LiveData<Seat> = eastSeat
    internal fun getSouthSeat(): LiveData<Seat> = southSeat
    internal fun getWestSeat(): LiveData<Seat> = westSeat
    internal fun getNorthSeat(): LiveData<Seat> = northSeat
    internal fun getRoundNumber(): LiveData<Int> = roundNumber
    internal fun getFabMenuState(): LiveData<FabMenuStates> = fabMenuState
    internal fun getFabMenuOpenState(): LiveData<ShowState> = fabMenuOpenState
    internal fun getShowDialog(): LiveData<DialogType> = showDialog
    internal fun getEndGameState(): LiveData<Boolean> = endGameState
    
    //METHODS
    internal fun createGame() {
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
        showDialog.postValue(DialogType.PLAYERS_NAMES)
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
            fabMenuState.postValue(RANKING)
            showDialog.postValue(DialogType.SHOW_RANKING)
        }
    }
    
    private fun resetTable(seatsDisabled: Boolean = false) {
        tableState = TableStates.NORMAL
        selectedPlayerSeat.clear()
        mDiscarderCurrentSeat = NONE
        
        toolbarState.postValue(ToolbarState.NORMAL)
        viewPagerPagingState.postValue(ENABLED)
        fabMenuState.postValue(FabMenuStates.NORMAL)
        fabMenuOpenState.postValue(HIDE)
        showDialog.postValue(DialogType.NONE)
        
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
    
    internal fun loadGame(gameId: Long) {
        disposables.add(
            getGamesUseCase.getGame(gameId)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnEvent { _, _ -> progressState.postValue(HIDE) }
                .subscribe({ this.getGameSuccess(it) }, { error.postValue(it) })
        )
    }
    
    //PlayersDialogFragment
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
            showDialog.postValue(DialogType.PLAYERS_NAMES)
        }
    }
    
    //RequestHandPoints  Dialog Responses
    internal fun onRequestHandPointsResponse(newHandPointsInput: CharSequence?) {
        val handPointsInput: CharSequence? = newHandPointsInput ?: "0"
        val handPoints = convertInputValue(handPointsInput.toString())
        if (handPoints == null || handPoints < MCR_MIN_POINTS) showDialog.postValue(DialogType.REQUEST_HAND_POINTS)
        else if (tableState === TableStates.REQUESTING_RON_POINTS) saveRonRound(handPoints)
        else if (tableState === TableStates.REQUESTING_TSUMO_POINTS) saveTsumoRound(handPoints)
        else onRequestHandPointsCancel()
    }
    
    internal fun onRequestHandPointsCancel() = resetTable()
    
    private fun saveTsumoRound(requestedPoints: Int) {
        mCurrentRound.handPoints = requestedPoints
        mCurrentRound.winnerInitialPosition = selectedPlayerSeat.initialSeat
        mCurrentRound.setAllPlayersTsumoPoints(selectedPlayerSeat.initialSeat, requestedPoints)
        saveCurrentRoundAndStartNext()
    }
    
    //RequestPenaltyPoints Dialog Responses
    internal fun onRequestPenaltyPointsResponse(newPenaltyPointsInput: CharSequence?, isDividedEqually: Boolean) {
        val penaltyPointsInput = newPenaltyPointsInput ?: "0"
        val penaltyPoints = convertInputValue(penaltyPointsInput.toString())
        if (penaltyPoints == null || penaltyPoints <= 0) {
            showDialog.postValue(DialogType.REQUEST_PENALTY_POINTS)
        } else {
            if (isDividedEqually) {
                if (penaltyPoints % 3 == 0) {
                    mCurrentRound.setAllPlayersPenaltyPoints(selectedPlayerSeat.initialSeat, penaltyPoints)
                    resetTable()
                } else {
                    showDialog.postValue(DialogType.REQUEST_PENALTY_POINTS)
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
    
    internal fun onRequestPenaltyPointsCancel() = resetTable()
    
    //Seats
    fun onSeatClicked(seatPosition: TableWinds) {
        when (tableState) {
            TableStates.NORMAL -> {
                setSelectedPlayerSeat(seatPosition)
                val isPlayerPenalized = mCurrentRound.isPenalizedPlayer(selectedPlayerSeat.initialSeat)
                val newMenuState = if (isPlayerPenalized) PLAYER_PENALIZED else PLAYER_SELECTED
                fabMenuState.postValue(newMenuState)
                fabMenuOpenState.postValue(SHOW)
            }
            TableStates.REQUESTING_DISCARDER -> if (seatPosition !== selectedPlayerSeat.currentSeat) {
                mDiscarderCurrentSeat = seatPosition
                requestRonPoints()
            }
            TableStates.REQUESTING_RON_POINTS, TableStates.REQUESTING_TSUMO_POINTS, TableStates.REQUESTING_PENALTY_POINTS -> {
            }
        }
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
    
    private fun requestRonPoints() {
        tableState = TableStates.REQUESTING_RON_POINTS
        showDialog.postValue(DialogType.REQUEST_HAND_POINTS)
    }
    
    private fun saveRonRound(requestedPoints: Int) {
        mCurrentRound.handPoints = requestedPoints
        mCurrentRound.winnerInitialPosition = selectedPlayerSeat.initialSeat
        val discarderInitialSeat = GameRounds.getPlayerInitialSeatByCurrentSeat(mDiscarderCurrentSeat, mCurrentRound.roundId)
        mCurrentRound.discarderInitialPosition = discarderInitialSeat
        mCurrentRound.setAllPlayersRonPoints(selectedPlayerSeat.initialSeat, requestedPoints, discarderInitialSeat)
        saveCurrentRoundAndStartNext()
    }
    
    //Fabs
    fun onFabCancelRequestingLooserClicked() {
        resetTable()
    }
    
    fun onToggleFabMenu(isOpened: Boolean) {
        if (!isOpened && tableState === TableStates.NORMAL) {
            setSelectedPlayerSeat(NONE)
            setSeatSelected(NONE)
            fabMenuState.postValue(FabMenuStates.NORMAL)
            fabMenuOpenState.postValue(HIDE)
        }
    }
    
    fun onFabGamePenaltyCancelClicked() {
        mCurrentRound.cancelAllPlayersPenalties()
        resetTable()
    }
    
    fun onFabGamePenaltyClicked() {
        tableState = TableStates.REQUESTING_PENALTY_POINTS
        showDialog.postValue(DialogType.REQUEST_PENALTY_POINTS)
    }
    
    fun onFabGameWashoutClicked() {
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
    
    fun onFabGameTsumoClicked() {
        requestTsumoPoints()
    }
    
    private fun requestTsumoPoints() {
        tableState = TableStates.REQUESTING_TSUMO_POINTS
        showDialog.postValue(DialogType.REQUEST_HAND_POINTS)
    }
    
    fun onFabGameRonClicked() {
        requestDiscarder()
    }
    
    private fun requestDiscarder() {
        tableState = TableStates.REQUESTING_DISCARDER
        viewPagerPagingState.postValue(EnablingState.DISABLED)
        toolbarState.postValue(ToolbarState.REQUEST_DISCARDER)
        fabMenuState.postValue(CANCEL_REQUEST_DISCARDER)
    }
    
    fun onFabRankingClicked() {
        fabMenuState.postValue(RANKING)
        showDialog.postValue(DialogType.SHOW_RANKING)
    }
    
    //Others
    fun setCurrentViewPagerPage(page: GamePages) {
        currentViewPagerPage.postValue(page)
    }
    
    fun resumeGame() {
        //ToDo
    }
    
    fun onBackPressed() {
        if (currentViewPagerPage.value === LIST) {
            currentViewPagerPage.postValue(TABLE)
            
        } else if (fabMenuOpenState.value === SHOW) {
            fabMenuOpenState.postValue(HIDE)
            
        } else {
            showDialog.postValue(DialogType.EXIT)
        }
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
    
    fun famMenuDiceClicked() {
        showDialog.postValue(DialogType.ROLL_DICE)
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
