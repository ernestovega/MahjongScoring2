package es.etologic.mahjongscoring2.app.game.activity;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.Date;
import java.util.List;

import es.etologic.mahjongscoring2.app.base.BaseViewModel;
import es.etologic.mahjongscoring2.app.game.game_table.RankingTableHelper;
import es.etologic.mahjongscoring2.app.model.DialogType;
import es.etologic.mahjongscoring2.app.model.EnablingState;
import es.etologic.mahjongscoring2.app.model.GamePages;
import es.etologic.mahjongscoring2.app.model.Seat;
import es.etologic.mahjongscoring2.app.model.SeatStates;
import es.etologic.mahjongscoring2.app.model.SelectedPlayerSeat;
import es.etologic.mahjongscoring2.app.model.ShowState;
import es.etologic.mahjongscoring2.app.model.TableStates;
import es.etologic.mahjongscoring2.app.model.ToolbarState;
import es.etologic.mahjongscoring2.app.utils.StringUtils;
import es.etologic.mahjongscoring2.domain.model.Game;
import es.etologic.mahjongscoring2.domain.model.GameWithRounds;
import es.etologic.mahjongscoring2.domain.model.RankingTable;
import es.etologic.mahjongscoring2.domain.model.Round;
import es.etologic.mahjongscoring2.domain.model.enums.FabMenuStates;
import es.etologic.mahjongscoring2.domain.model.enums.TableWinds;
import es.etologic.mahjongscoring2.domain.use_cases.CreateGameUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetGamesUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.UpdateGameUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.UpdateRoundsUseCase;
import io.reactivex.schedulers.Schedulers;

import static es.etologic.mahjongscoring2.app.model.EnablingState.ENABLED;
import static es.etologic.mahjongscoring2.app.model.GamePages.LIST;
import static es.etologic.mahjongscoring2.app.model.GamePages.TABLE;
import static es.etologic.mahjongscoring2.app.model.SeatStates.NORMAL;
import static es.etologic.mahjongscoring2.app.model.SeatStates.SELECTED;
import static es.etologic.mahjongscoring2.app.model.ShowState.HIDE;
import static es.etologic.mahjongscoring2.app.model.ShowState.SHOW;
import static es.etologic.mahjongscoring2.domain.model.enums.FabMenuStates.CANCEL_REQUEST_DISCARDER;
import static es.etologic.mahjongscoring2.domain.model.enums.FabMenuStates.PLAYER_PENALIZED;
import static es.etologic.mahjongscoring2.domain.model.enums.FabMenuStates.RANKING;
import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.EAST;
import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.NONE;
import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.NORTH;
import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.SOUTH;
import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.WEST;

public class GameActivityViewModel extends BaseViewModel {

    //CONSTANTS
    private static final Integer MCR_MIN_POINTS = 8;
    private static final int MAX_MCR_HANDS_PER_GAME = 16;

    //FIELDS
    //List Observables
    private MutableLiveData<String[]> listNames = new MutableLiveData<>();
    private MutableLiveData<List<Round>> listRounds = new MutableLiveData<>();
    private MutableLiveData<String[]> listTotals = new MutableLiveData<>();
    //Table Observables
    private MutableLiveData<ToolbarState> toolbarState = new MutableLiveData<>();
    private MutableLiveData<EnablingState> viewPagerPagingState = new MutableLiveData<>();
    private MutableLiveData<GamePages> currentViewPagerPage = new MutableLiveData<>();
    private MutableLiveData<FabMenuStates> fabMenuState = new MutableLiveData<>();
    private MutableLiveData<ShowState> fabMenuOpenState = new MutableLiveData<>();
    private MutableLiveData<Seat> eastSeat = new MutableLiveData<>();
    private MutableLiveData<Seat> southSeat = new MutableLiveData<>();
    private MutableLiveData<Seat> westSeat = new MutableLiveData<>();
    private MutableLiveData<Seat> northSeat = new MutableLiveData<>();
    private MutableLiveData<Integer> roundNumber = new MutableLiveData<>();
    private MutableLiveData<DialogType> showDialog = new MutableLiveData<>();
    private MutableLiveData<Boolean> endGameState = new MutableLiveData<>();
    //UseCases
    private CreateGameUseCase createGameUseCase;
    private GetGamesUseCase getGamesUseCase;
    private UpdateRoundsUseCase updateRoundsUseCase;
    private UpdateGameUseCase updateGameUseCase;
    //Table
    private GameWithRounds gameWithRounds;
    private Round mCurrentRound = new Round(0, 0);
    private TableStates tableState = TableStates.NORMAL;
    private SelectedPlayerSeat selectedPlayerSeat = new SelectedPlayerSeat();
    private TableWinds mDiscarderCurrentSeat = NONE;

    //Constructor
    GameActivityViewModel(CreateGameUseCase createGameUseCase, GetGamesUseCase getGamesUseCase,
                          UpdateRoundsUseCase updateRoundsUseCase, UpdateGameUseCase updateGameUseCase) {
        this.createGameUseCase = createGameUseCase;
        this.getGamesUseCase = getGamesUseCase;
        this.updateRoundsUseCase = updateRoundsUseCase;
        this.updateGameUseCase = updateGameUseCase;
    }

    //Observables
    //List
    public LiveData<String[]> getListNames() {
        return listNames;
    }
    public LiveData<List<Round>> getListRounds() {
        return listRounds;
    }
    public LiveData<String[]> getListTotals() {
        return listTotals;
    }
    //Table
    LiveData<EnablingState> getViewPagerPagingState() {
        return viewPagerPagingState;
    }
    LiveData<ToolbarState> getToolbarState() {
        return toolbarState;
    }
    LiveData<GamePages> getCurrentViewPagerPage() {
        return currentViewPagerPage;
    }
    public LiveData<Seat> getEastSeat() {
        return eastSeat;
    }
    public LiveData<Seat> getSouthSeat() {
        return southSeat;
    }
    public LiveData<Seat> getWestSeat() {
        return westSeat;
    }
    public LiveData<Seat> getNorthSeat() {
        return northSeat;
    }
    public LiveData<Integer> getRoundNumber() {
        return roundNumber;
    }
    public LiveData<FabMenuStates> getFabMenuState() {
        return fabMenuState;
    }
    public LiveData<ShowState> getFabMenuOpenState() {
        return fabMenuOpenState;
    }
    public LiveData<DialogType> getShowDialog() {
        return showDialog;
    }
    public LiveData<Boolean> getEndGameState() {
        return endGameState;
    }

    //METHODS
    void createGame() {
        disposables.add(
                createGameUseCase.createGame()
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(disposable -> progressState.postValue(SHOW))
                        .doOnEvent((combinations, throwable) -> progressState.postValue(HIDE))
                        .subscribe(this::createGameSuccess, error::postValue));
    }
    private void createGameSuccess(GameWithRounds gameWithRounds) {
        getGameSuccess(gameWithRounds);
        showDialog.postValue(DialogType.PLAYERS_NAMES);
    }
    private void getGameSuccess(GameWithRounds gameWithRounds) {
        this.gameWithRounds = gameWithRounds;
        listNames.postValue(gameWithRounds.getGame().getPlayersNames());
        listRounds.postValue(gameWithRounds.getRoundsWithBestHand());
        listTotals.postValue(gameWithRounds.getPlayersTotalPointsString());
        createRoundOrFinish(gameWithRounds);
    }
    private void createRoundOrFinish(GameWithRounds gameWithRounds) {
        int gameId = gameWithRounds.getGame().getGameId();
        int newRoundId;
        List<Round> rounds = gameWithRounds.getRounds();
        if (rounds.size() < MAX_MCR_HANDS_PER_GAME) {
            newRoundId = rounds.size() + 1;
            mCurrentRound = new Round(gameId, newRoundId);
            resetTable();
        } else {
            mCurrentRound = rounds.get(rounds.size() - 1);
            resetTable(true);
            fabMenuState.postValue(RANKING);
            showDialog.postValue(DialogType.SHOW_RANKING);
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
    private void resetTable() {
        resetTable(false);
    }
    private void resetTable(boolean seatsDisabled) {
        tableState = TableStates.NORMAL;
        selectedPlayerSeat.clear();
        mDiscarderCurrentSeat = NONE;

        toolbarState.postValue(ToolbarState.NORMAL);
        viewPagerPagingState.postValue(ENABLED);
        fabMenuState.postValue(FabMenuStates.NORMAL);
        fabMenuOpenState.postValue(HIDE);
        showDialog.postValue(DialogType.NONE);

        roundNumber.postValue(mCurrentRound.getRoundId());
        resetSeats(seatsDisabled);
    }
    private void resetSeats(boolean seatsDisabled) {
        eastSeat.postValue(buildNewSeat(EAST, seatsDisabled));
        southSeat.postValue(buildNewSeat(SOUTH, seatsDisabled));
        westSeat.postValue(buildNewSeat(WEST, seatsDisabled));
        northSeat.postValue(buildNewSeat(NORTH, seatsDisabled));
    }
    private Seat buildNewSeat(TableWinds wind, boolean isDisabled) {
        TableWinds initialPosition = Game.getPlayerInitialSeatByCurrentSeat(wind, mCurrentRound.getRoundId());
        String name = gameWithRounds.getGame().getPlayerNameByInitialPosition(initialPosition);
        int points = gameWithRounds.getPlayersTotalPoints()[initialPosition.getIndex()];
        int penaltyPoints = mCurrentRound.getPlayersPenalties()[initialPosition.getIndex()];
        return new Seat(wind, name, points, penaltyPoints, isDisabled ? SeatStates.DISABLED : NORMAL);
    }
    void loadGame(long gameId) {
        disposables.add(
                getGamesUseCase.getGame(gameId)
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(disposable -> progressState.postValue(SHOW))
                        .doOnEvent((combinations, throwable) -> progressState.postValue(HIDE))
                        .subscribe(this::getGameSuccess, error::postValue));
    }
    //RequestPlayers  Dialog Responses
    void onRequestPlayersResponse(CharSequence tiet1Text, CharSequence tiet2Text, CharSequence tiet3Text, CharSequence tiet4Text) {
        String name1 = tiet1Text.toString();
        String name2 = tiet2Text.toString();
        String name3 = tiet3Text.toString();
        String name4 = tiet4Text.toString();
        if (!StringUtils.isEmpty(name1) && !StringUtils.isEmpty(name2) && !StringUtils.isEmpty(name3) && !StringUtils.isEmpty(name4)) {
            gameWithRounds.getGame().setNameP1(name1);
            gameWithRounds.getGame().setNameP2(name2);
            gameWithRounds.getGame().setNameP3(name3);
            gameWithRounds.getGame().setNameP4(name4);
            listNames.postValue(gameWithRounds.getGame().getPlayersNames());
            resetTable();
            disposables.add(
                    updateGameUseCase.updateGame(gameWithRounds.game)
                            .subscribeOn(Schedulers.io())
                            .doOnSubscribe(disposable -> progressState.postValue(SHOW))
                            .doOnEvent((combinations, throwable) -> progressState.postValue(HIDE))
                            .subscribe(success -> {}, error::postValue));
        } else {
            showDialog.postValue(DialogType.PLAYERS_NAMES);
        }
    }
    //RequestHandPoints  Dialog Responses
    void onRequestHandPointsResponse(CharSequence handPointsInput) {

        if (handPointsInput == null) handPointsInput = "0";
        Integer handPoints = convertInputValue(handPointsInput.toString());

        if (handPoints == null || handPoints < MCR_MIN_POINTS) {
            showDialog.postValue(DialogType.REQUEST_HAND_POINTS);

        } else if (tableState == TableStates.REQUESTING_RON_POINTS) {
            saveRonRound(handPoints);

        } else if (tableState == TableStates.REQUESTING_TSUMO_POINTS) {
            saveTsumoRound(handPoints);

        } else {
            onRequestHandPointsCancel();
        }
    }
    void onRequestHandPointsCancel() {
        resetTable();
    }
    private void saveTsumoRound(int requestedPoints) {
        mCurrentRound.setHandPoints(requestedPoints);
        mCurrentRound.setWinnerInitialPosition(selectedPlayerSeat.getInitialSeat());
        mCurrentRound.setAllPlayersTsumoPoints(selectedPlayerSeat.getInitialSeat(), requestedPoints);
        saveCurrentRoundAndStartNext();
    }
    //RequestPenaltyPoints Dialog Responses
    void onRequestPenaltyPointsResponse(CharSequence penaltyPointsInput, boolean isDividedEqually) {
        if (penaltyPointsInput == null) { penaltyPointsInput = "0"; }
        Integer penaltyPoints = convertInputValue(penaltyPointsInput.toString());
        if (penaltyPoints == null || penaltyPoints <= 0) {
            showDialog.postValue(DialogType.REQUEST_PENALTY_POINTS);
        } else {
            if (isDividedEqually) {
                if (penaltyPoints%3 == 0) {
                    mCurrentRound.setAllPlayersPenaltyPoints(selectedPlayerSeat.getInitialSeat(), penaltyPoints);
                    resetTable();
                } else {
                    showDialog.postValue(DialogType.REQUEST_PENALTY_POINTS);
                }
            } else {
                mCurrentRound.setPlayerPenaltyPoints(selectedPlayerSeat.getInitialSeat(), penaltyPoints);
                resetTable();
            }
        }
    }
    private Integer convertInputValue(String penaltyPointsInput) {
        try {
            return Integer.valueOf(penaltyPointsInput);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    void onRequestPenaltyPointsCancel() {
        resetTable();
    }
    //Seats
    public void onSeatClicked(TableWinds seatPosition) {
        switch (tableState) {
            case NORMAL:
                setSelectedPlayerSeat(seatPosition);
                boolean isPlayerPenalized = mCurrentRound.isPenalizedPlayer(selectedPlayerSeat.getInitialSeat());
                FabMenuStates newMenuState = isPlayerPenalized ? PLAYER_PENALIZED : FabMenuStates.PLAYER_SELECTED;
                fabMenuState.postValue(newMenuState);
                fabMenuOpenState.postValue(SHOW);
                break;
            case REQUESTING_DISCARDER:
                if (seatPosition != selectedPlayerSeat.getCurrentSeat()) {
                    mDiscarderCurrentSeat = seatPosition;
                    requestRonPoints();
                }
                break;
            case REQUESTING_RON_POINTS:
            case REQUESTING_TSUMO_POINTS:
            case REQUESTING_PENALTY_POINTS:
                break;
        }
    }
    private void setSelectedPlayerSeat(TableWinds seatPosition) {
        selectedPlayerSeat.setSelectedPlayer(seatPosition, mCurrentRound.getRoundId());
        setSeatSelected(seatPosition);
    }
    private void setSeatSelected(TableWinds seatPosition) {
        switch (seatPosition) {
            default:
            case NONE:
                unselectAllSeats();
                break;
            case EAST:
                unselectAllSeats();
                Seat eastSeatValue = eastSeat.getValue();
                assert eastSeatValue != null;
                eastSeatValue.setState(SELECTED);
                eastSeat.postValue(eastSeatValue);
                break;
            case SOUTH:
                unselectAllSeats();
                Seat southSeatValue = southSeat.getValue();
                assert southSeatValue != null;
                southSeatValue.setState(SELECTED);
                southSeat.postValue(southSeatValue);
                break;
            case WEST:
                unselectAllSeats();
                Seat westSeatValue = westSeat.getValue();
                assert westSeatValue != null;
                westSeatValue.setState(SELECTED);
                westSeat.postValue(westSeatValue);
                break;
            case NORTH:
                unselectAllSeats();
                Seat northSeatValue = northSeat.getValue();
                assert northSeatValue != null;
                northSeatValue.setState(SELECTED);
                northSeat.postValue(northSeatValue);
                break;
        }
    }
    private void unselectAllSeats() {
        Seat eastSeatValue = eastSeat.getValue();
        assert eastSeatValue != null;
        eastSeatValue.setState(NORMAL);
        eastSeat.postValue(eastSeatValue);
        Seat southSeatValue = southSeat.getValue();
        assert southSeatValue != null;
        southSeatValue.setState(NORMAL);
        southSeat.postValue(southSeatValue);
        Seat westSeatValue = westSeat.getValue();
        assert westSeatValue != null;
        westSeatValue.setState(NORMAL);
        westSeat.postValue(westSeatValue);
        Seat northSeatValue = northSeat.getValue();
        assert northSeatValue != null;
        northSeatValue.setState(NORMAL);
        northSeat.postValue(northSeatValue);
    }
    private void requestRonPoints() {
        tableState = TableStates.REQUESTING_RON_POINTS;
        showDialog.postValue(DialogType.REQUEST_HAND_POINTS);
    }
    private void saveRonRound(int requestedPoints) {
        mCurrentRound.setHandPoints(requestedPoints);
        mCurrentRound.setWinnerInitialPosition(selectedPlayerSeat.getInitialSeat());
        mCurrentRound.setDiscarderInitialPosition(Game.getPlayerInitialSeatByCurrentSeat(mDiscarderCurrentSeat, mCurrentRound.getRoundId()));
        mCurrentRound.setAllPlayersRonPoints(selectedPlayerSeat.getInitialSeat(), requestedPoints, mDiscarderCurrentSeat);
        saveCurrentRoundAndStartNext();
    }
    //Fabs
    public void onFabCancelRequestingLooserClicked() {
        resetTable();
    }
    public void onToggleFabMenu(boolean isOpened) {
        if (!isOpened && tableState == TableStates.NORMAL) {
            setSelectedPlayerSeat(NONE);
            setSeatSelected(NONE);
            fabMenuState.postValue(FabMenuStates.NORMAL);
            fabMenuOpenState.postValue(HIDE);
        }
    }
    public void onFabGamePenaltyCancelClicked() {
        mCurrentRound.cancelAllPlayersPenalties();
        resetTable();
    }
    public void onFabGamePenaltyClicked() {
        tableState = TableStates.REQUESTING_PENALTY_POINTS;
        showDialog.postValue(DialogType.REQUEST_PENALTY_POINTS);
    }
    public void onFabGameWashoutClicked() {
        saveCurrentRoundAndStartNext();
    }
    private void saveCurrentRoundAndStartNext() {
        mCurrentRound.applyAllPlayersPenalties();
        //        mCurrentRound.setRoundDuration();
        disposables.add(
                updateRoundsUseCase.addRound(mCurrentRound)
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(disposable -> progressState.postValue(SHOW))
                        .doOnEvent((combinations, throwable) -> progressState.postValue(HIDE))
                        .subscribe(this::getGameSuccess, error::postValue));
    }
    public void onFabGameTsumoClicked() {
        requestTsumoPoints();
    }
    private void requestTsumoPoints() {
        tableState = TableStates.REQUESTING_TSUMO_POINTS;
        showDialog.postValue(DialogType.REQUEST_HAND_POINTS);
    }
    public void onFabGameRonClicked() {
        requestDiscarder();
    }
    private void requestDiscarder() {
        tableState = TableStates.REQUESTING_DISCARDER;
        viewPagerPagingState.postValue(EnablingState.DISABLED);
        toolbarState.postValue(ToolbarState.REQUEST_DISCARDER);
        fabMenuState.postValue(CANCEL_REQUEST_DISCARDER);
    }
    public void onFabRankingClicked() {
        fabMenuState.postValue(FabMenuStates.RANKING);
        showDialog.postValue(DialogType.SHOW_RANKING);
    }
    //Others
    public void setCurrentViewPagerPage(GamePages page) {
        currentViewPagerPage.postValue(page);
    }
    public void resumeGame() {
        //ToDo
    }
    public void onBackPressed() {
        if (currentViewPagerPage.getValue() == LIST) {
            currentViewPagerPage.postValue(TABLE);

        } else if (fabMenuOpenState.getValue() == SHOW) {
            fabMenuOpenState.postValue(HIDE);

        } else {
            showDialog.postValue(DialogType.EXIT);
        }
    }
    public RankingTable getRankingTable() {
        return RankingTableHelper.generateRankingTable(gameWithRounds);
    }
    void endGame() {
        gameWithRounds.getGame().setEndDate(new Date());
        disposables.add(
                updateGameUseCase.updateGame(gameWithRounds.getGame())
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(disposable -> progressState.postValue(SHOW))
                        .doOnEvent((combinations, throwable) -> progressState.postValue(HIDE))
                        .subscribe(g -> endGameState.postValue(true), t -> endGameState.postValue(false)));
    }
    public void famMenuDiceClicked() {
        showDialog.postValue(DialogType.ROLL_DICE);
    }
}
