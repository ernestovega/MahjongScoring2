package es.etologic.mahjongscoring2.app.game.activity;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.List;

import es.etologic.mahjongscoring2.app.base.BaseViewModel;
import es.etologic.mahjongscoring2.app.model.GamePages;
import es.etologic.mahjongscoring2.app.model.Seat;
import es.etologic.mahjongscoring2.app.model.DialogType;
import es.etologic.mahjongscoring2.app.model.EnablingState;
import es.etologic.mahjongscoring2.app.model.SelectedPlayerSeat;
import es.etologic.mahjongscoring2.app.model.ShowState;
import es.etologic.mahjongscoring2.app.model.TableStates;
import es.etologic.mahjongscoring2.app.model.ToolbarState;
import es.etologic.mahjongscoring2.domain.model.Game;
import es.etologic.mahjongscoring2.domain.model.GameWithRounds;
import es.etologic.mahjongscoring2.domain.model.Round;
import es.etologic.mahjongscoring2.domain.model.enums.FabMenuStates;
import es.etologic.mahjongscoring2.domain.model.enums.TableWinds;
import es.etologic.mahjongscoring2.domain.use_cases.CreateGameUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetGamesUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.UpdateRoundsUseCase;
import io.reactivex.schedulers.Schedulers;

import static es.etologic.mahjongscoring2.app.model.SeatStates.NORMAL;
import static es.etologic.mahjongscoring2.app.model.SeatStates.SELECTED;
import static es.etologic.mahjongscoring2.app.model.EnablingState.DISABLED;
import static es.etologic.mahjongscoring2.app.model.EnablingState.ENABLED;
import static es.etologic.mahjongscoring2.app.model.ShowState.HIDE;
import static es.etologic.mahjongscoring2.app.model.ShowState.SHOW;
import static es.etologic.mahjongscoring2.domain.model.enums.FabMenuStates.CANCEL;
import static es.etologic.mahjongscoring2.domain.model.enums.FabMenuStates.HIDDEN;
import static es.etologic.mahjongscoring2.domain.model.enums.FabMenuStates.PLAYER_PENALIZED;
import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.EAST;
import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.NONE;
import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.NORTH;
import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.SOUTH;
import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.WEST;

public class GameActivityViewModel extends BaseViewModel {

    //CONSTANTS
    private static final Integer MCR_MIN_POINTS = 8;
    public static final int MAX_MCR_HANDS_PER_GAME = 16;

    //FIELDS
    //List Observables
    private MutableLiveData<String[]> listNames = new MutableLiveData<>();
    private MutableLiveData<List<Round>> listRounds = new MutableLiveData<>();
    private MutableLiveData<String[]> listTotals = new MutableLiveData<>();
    //Table Observables
    private MutableLiveData<ToolbarState> toolbarState = new MutableLiveData<>();
    private MutableLiveData<EnablingState> viewPagerPagingState = new MutableLiveData<>();
    private MutableLiveData<GamePages> viewPagerPageToSee = new MutableLiveData<>();
    private MutableLiveData<FabMenuStates> fabMenuState = new MutableLiveData<>();
    private MutableLiveData<ShowState> fabMenuOpenState = new MutableLiveData<>();
    private MutableLiveData<Seat> eastSeat = new MutableLiveData<>();
    private MutableLiveData<Seat> southSeat = new MutableLiveData<>();
    private MutableLiveData<Seat> westSeat = new MutableLiveData<>();
    private MutableLiveData<Seat> northSeat = new MutableLiveData<>();
    private MutableLiveData<Integer> roundNumber = new MutableLiveData<>();
    private MutableLiveData<DialogType> showDialog = new MutableLiveData<>();
    //UseCases

    private CreateGameUseCase createGameUseCase;
    private GetGamesUseCase getGamesUseCase;
    private UpdateRoundsUseCase updateRoundsUseCase;
    //Common
    private GameWithRounds gameWithRounds;
    //Table variables
    private Round mCurrentRound = new Round(0, 0);
    private TableStates tableState = TableStates.NORMAL;
    private SelectedPlayerSeat selectedPlayerSeat = new SelectedPlayerSeat();
    private TableWinds mDiscarderCurrentSeat = NONE;

    //Constructor
    GameActivityViewModel(CreateGameUseCase createGameUseCase,
                          GetGamesUseCase getGamesUseCase,
                          UpdateRoundsUseCase updateRoundsUseCase) {
        this.createGameUseCase = createGameUseCase;
        this.getGamesUseCase = getGamesUseCase;
        this.updateRoundsUseCase = updateRoundsUseCase;
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
    LiveData<DialogType> getShowDialog() {
        return showDialog;
    }
    LiveData<GamePages> getPageToSee() {
        return viewPagerPageToSee;
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
    //Setters
    public void setToolbarState(ToolbarState state) {
        toolbarState.postValue(state);
    }

    //METHODS
    void createGame() {
        disposables.add(
                createGameUseCase.createGame()
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(disposable -> progressState.postValue(SHOW))
                        .doOnEvent((combinations, throwable) -> progressState.postValue(HIDE))
                        .subscribe(this::getGameSuccess, error::postValue));
    }
    void loadGame(long gameId) {
        disposables.add(
                getGamesUseCase.getGame(gameId)
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(disposable -> progressState.postValue(SHOW))
                        .doOnEvent((combinations, throwable) -> progressState.postValue(HIDE))
                        .subscribe(this::getGameSuccess, error::postValue));
    }
    private void getGameSuccess(GameWithRounds gameWithRounds) {
        this.gameWithRounds = gameWithRounds;
        listNames.postValue(gameWithRounds.getGame().getPlayersNames());
        listRounds.postValue(gameWithRounds.getRounds());
        listTotals.postValue(gameWithRounds.getPlayersTotalPointsString());
        if (gameWithRounds.getRounds().size() < MAX_MCR_HANDS_PER_GAME) {
            mCurrentRound = new Round(gameWithRounds.getGame().getGameId(), gameWithRounds.getRounds().size() + 1);
        } else {
            showDialog.postValue(DialogType.SHOW_RANKING);
        }
        resetTable();
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
        tableState = TableStates.NORMAL;
        selectedPlayerSeat.clear();
        mDiscarderCurrentSeat = NONE;

        toolbarState.postValue(ToolbarState.NORMAL);
        viewPagerPagingState.postValue(ENABLED);
        fabMenuOpenState.postValue(HIDE);
        fabMenuState.postValue(FabMenuStates.NORMAL);
        showDialog.postValue(DialogType.NONE);

        roundNumber.postValue(mCurrentRound.getRoundId());

        int[] playersPoints = gameWithRounds.getPlayersTotalPoints();
        eastSeat.postValue(buildNewSeat(EAST, playersPoints));
        southSeat.postValue(buildNewSeat(SOUTH, playersPoints));
        westSeat.postValue(buildNewSeat(WEST, playersPoints));
        northSeat.postValue(buildNewSeat(NORTH, playersPoints));
    }
    private Seat buildNewSeat(TableWinds wind, int[] playersTotalsPoints) {
        TableWinds initialPosition = Game.getPlayerInitialSeatByCurrentSeat(wind, mCurrentRound.getRoundId());
        String name = gameWithRounds.getGame().getPlayerNameByInitialPosition(initialPosition);
        int points = playersTotalsPoints[initialPosition.getIndex()];
        return new Seat(wind, name, points, NORMAL);
    }
    //RequestHandPoints  Dialog Responses
    public void onRequestHandPointsCancel() {
        resetTable();
    }
    public void onRequestHandPointsResponse(String handPointsInput) {

        Integer handPoints = convertInputValue(handPointsInput);

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
    private Integer convertInputValue(String penaltyPointsInput) {
        try {
            return Integer.valueOf(penaltyPointsInput);
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            return null;
        }
    }
    private void saveTsumoRound(int requestedPoints) {
        mCurrentRound.setHandPoints(requestedPoints);
        mCurrentRound.setWinnerInitialPosition(selectedPlayerSeat.getInitialSeat());
        mCurrentRound.setAllPlayersTsumoPoints(selectedPlayerSeat.getInitialSeat(), requestedPoints);
        saveCurrentRoundAndStartNext();
    }
    private void saveCurrentRoundAndStartNext() {
        disposables.add(
                updateRoundsUseCase.addRound(mCurrentRound)
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(disposable -> progressState.postValue(SHOW))
                        .doOnEvent((combinations, throwable) -> progressState.postValue(HIDE))
                        .subscribe(this::getGameSuccess, error::postValue));
    }
    //RequestPenaltyPoints Dialog Responses
    public void onRequestPenaltyPointsCancel() {
        resetTable();
    }
    public void onRequestPenaltyPointsResponse(String penaltyPointsInput) {

        Integer penaltyPoints = convertInputValue(penaltyPointsInput);

        if (penaltyPoints == null || penaltyPoints <= 0) {
            showDialog.postValue(DialogType.REQUEST_PENALTY_POINTS);
        } else {
            mCurrentRound.setAllPlayersPointsByPenalty(selectedPlayerSeat.getInitialSeat(), penaltyPoints);
            resetTable();
        }
    }
    public void onSeatClicked(TableWinds seatPosition) {
        switch (tableState) {
            case NORMAL:
                setSelectedPlayerSeat(seatPosition);
                FabMenuStates newMenuState = mCurrentRound.isPenalizedPlayer(selectedPlayerSeat.getInitialSeat()) ? PLAYER_PENALIZED : FabMenuStates.PLAYER_SELECTED;
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
    //Fabs
    public void onFabCancelRequestingLooserClicked() {
        resetTable();
    }
    public void onToggleFabMenu(boolean isOpened) {
        if (!isOpened && tableState == TableStates.NORMAL) {
            setSelectedPlayerSeat(NONE);
            setSeatSelected(NONE);
            fabMenuState.postValue(FabMenuStates.NORMAL);
        }
    }
    public void onFabGamePenaltyCancelClicked() {
        mCurrentRound.setAllPlayersPointsByPenaltyCancellation(selectedPlayerSeat.getInitialSeat());
        resetTable();
    }
    public void onFabGamePenaltyClicked() {
        fabMenuState.postValue(HIDDEN);
        tableState = TableStates.REQUESTING_PENALTY_POINTS;
        showDialog.postValue(DialogType.REQUEST_PENALTY_POINTS);
    }
    public void onFabGameWashoutClicked() {
        fabMenuState.postValue(HIDDEN);
        saveCurrentRoundAndStartNext();
    }
    public void onFabGameTsumoClicked() {
        fabMenuState.postValue(HIDDEN);
        requestTsumoPoints();
    }
    private void requestTsumoPoints() {
        tableState = TableStates.REQUESTING_TSUMO_POINTS;
        showDialog.postValue(DialogType.REQUEST_HAND_POINTS);
    }
    public void onFabGameRonClicked() {
        fabMenuState.postValue(HIDDEN);
        requestDiscarder();
    }
    private void requestDiscarder() {
        tableState = TableStates.REQUESTING_DISCARDER;
        viewPagerPagingState.postValue(DISABLED);
        toolbarState.postValue(ToolbarState.REQUEST_LOOSER);
        fabMenuState.postValue(CANCEL);
    }
    //Others
    public void toggleViewPagerPagingState(EnablingState state) {
        viewPagerPagingState.postValue(state);
    }
    public void toggleFabMenuOpenState(ShowState showState) {
        fabMenuOpenState.postValue(showState);
    }
    public void seeListPage() {
        viewPagerPageToSee.postValue(GamePages.LIST);
    }
    public void resumeGame() {
        //ToDo
    }
    public void exit() {
        //ToDo
    }
}
