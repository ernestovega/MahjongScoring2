package es.etologic.mahjongscoring2.app.game.activity;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import es.etologic.mahjongscoring2.app.base.BaseViewModel;
import es.etologic.mahjongscoring2.app.model.DialogType;
import es.etologic.mahjongscoring2.app.model.EnablingState;
import es.etologic.mahjongscoring2.app.model.ShowState;
import es.etologic.mahjongscoring2.app.model.ToolbarState;
import es.etologic.mahjongscoring2.domain.model.Game;
import es.etologic.mahjongscoring2.domain.model.GameRounds;
import es.etologic.mahjongscoring2.domain.model.Round;
import es.etologic.mahjongscoring2.domain.model.Seat;
import es.etologic.mahjongscoring2.domain.model.enums.FabMenuStates;
import es.etologic.mahjongscoring2.domain.model.enums.Winds;
import es.etologic.mahjongscoring2.domain.use_cases.GetGameUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetRoundsUseCase;
import io.reactivex.schedulers.Schedulers;

import static es.etologic.mahjongscoring2.app.model.EnablingState.DISABLED;
import static es.etologic.mahjongscoring2.app.model.EnablingState.ENABLED;
import static es.etologic.mahjongscoring2.app.model.ShowState.HIDE;
import static es.etologic.mahjongscoring2.app.model.ShowState.SHOW;
import static es.etologic.mahjongscoring2.domain.model.enums.FabMenuStates.*;
import static es.etologic.mahjongscoring2.domain.model.enums.FabMenuStates.PLAYER_PENALIZED;
import static es.etologic.mahjongscoring2.domain.model.enums.Winds.EAST;
import static es.etologic.mahjongscoring2.domain.model.enums.Winds.NORTH;
import static es.etologic.mahjongscoring2.domain.model.enums.Winds.SOUTH;
import static es.etologic.mahjongscoring2.domain.model.enums.Winds.WEST;

public class GameActivityViewModel extends BaseViewModel {

    //Fields
    private GetGameUseCase getGameUseCase;
    private GetRoundsUseCase getRoundsUseCase;
    //List
    private MutableLiveData<String[]> listNames = new MutableLiveData<>();
    private MutableLiveData<List<Round>> listRounds = new MutableLiveData<>();
    private MutableLiveData<String[]> listTotals = new MutableLiveData<>();
    private long gameId;
    private Game game;
    //Table;
    private MutableLiveData<EnablingState> viewPagerPagingState = new MutableLiveData<>();
    private MutableLiveData<DialogType> showDialog = new MutableLiveData<>();
    private MutableLiveData<ToolbarState> toolbarState = new MutableLiveData<>();
    private MutableLiveData<FabMenuStates> fabMenuState = new MutableLiveData<>();
    private MutableLiveData<ShowState> fabMenuOpenState = new MutableLiveData<>();
    private MutableLiveData<Seat[]> seats = new MutableLiveData<>();
    private MutableLiveData<Winds> selectedSeat = new MutableLiveData<>();
    private MutableLiveData<Winds> winnerLayerSeat = new MutableLiveData<>();
    private MutableLiveData<boolean[]> penaltyLayerSeats = new MutableLiveData<>();
    private MutableLiveData<Winds> penaltyIconSeat = new MutableLiveData<>();
    private MutableLiveData<Winds> ronIconSeat = new MutableLiveData<>();
    private MutableLiveData<Winds> tsumoIconSeat = new MutableLiveData<>();
    private Round mActualRound;
    private int mSelectedPlayerSeat;
    private int mSelectedPlayerInitialPosition;
    private int mHandPoints;
    private int mPenaltyPoints;
    private boolean mIsRequestingLooser;
    private boolean mIsRequestingHandPoints;
    private boolean mIsRequestingPenaltyPoints;
    private boolean mIsTsumo;

    //Constructor
    GameActivityViewModel(GetGameUseCase getGameUseCase,
                          GetRoundsUseCase getRoundsUseCase) {
        this.getGameUseCase = getGameUseCase;
        this.getRoundsUseCase = getRoundsUseCase;
    }

    //Observables
    public LiveData<EnablingState> getViewPagerPagingState() {
        return viewPagerPagingState;
    }
    public LiveData<String[]> getListNames() {
        return listNames;
    }
    public LiveData<List<Round>> getListRounds() {
        return listRounds;
    }
    public LiveData<String[]> getListTotals() {
        return listTotals;
    }
    public LiveData<ToolbarState> getToolbarState() {
        return toolbarState;
    }
    public LiveData<DialogType> getShowDialog() {
        return showDialog;
    }
    public LiveData<Seat[]> getSeats() {
        return seats;
    }
    public LiveData<Winds> getSelectedSeat() {
        return selectedSeat;
    }
    public LiveData<FabMenuStates> getFabMenuState() {
        return fabMenuState;
    }
    public LiveData<ShowState> getFabMenuOpenState() {
        return fabMenuOpenState;
    }
    public LiveData<Winds> getWinnerLayerSeat() {
        return winnerLayerSeat;
    }
    public LiveData<boolean[]> getPenaltyLayerSeats() {
        return penaltyLayerSeats;
    }
    public LiveData<Winds> getPenaltyIconSeat() {
        return penaltyIconSeat;
    }
    public LiveData<Winds> getRonIconSeat() {
        return ronIconSeat;
    }
    public LiveData<Winds> getTsumoIconSeat() {
        return tsumoIconSeat;
    }
    //Getters
    //Setters
    void setGameId(long gameId) {
        this.gameId = gameId;
    }

    //Methods
    void loadGame() {
        disposables.add(
                getGameUseCase.getGame(gameId)
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(disposable -> progressState.postValue(SHOW))
                        .doOnEvent((combinations, throwable) -> progressState.postValue(HIDE))
                        .subscribe(game -> {
                            this.game = game;
                            loadGameRounds();
                        }, error::postValue));
    }
    private void loadGameRounds() {
        disposables.add(
                getRoundsUseCase.getGameRounds(game.getGameId())
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(disposable -> progressState.postValue(SHOW))
                        .doOnEvent((combinations, throwable) -> progressState.postValue(HIDE))
                        .subscribe(this::loadGameRoundsSuccess, error::postValue));
    }
    private void loadGameRoundsSuccess(List<Round> rounds) {
        this.listRounds.postValue(rounds);
        game.setRounds(rounds);
        fillList();
        fillTable();
    }
    private void fillList() {
        listNames.postValue(game.getPlayersNames());
        listRounds.postValue(game.getRounds());
        listTotals.postValue(game.getPlayersTotalPoints());
    }
    private void fillTable() {
        this.seats.postValue(getActualSeats());
        //TODO

    }
    private void saveGame() {
        disposables.add(
                getRoundsUseCase.getGameRounds(game.getGameId())
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(disposable -> progressState.postValue(SHOW))
                        .doOnEvent((combinations, throwable) -> progressState.postValue(HIDE))
                        .subscribe(rounds -> {
                            this.listRounds.postValue(rounds);
                            game.setRounds(rounds);
                            fillList();
                            fillTable();
                        }, error::postValue));
    }
    private void saveActualRound() {
        disposables.add(
                getRoundsUseCase.addRound(mActualRound)
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(disposable -> progressState.postValue(SHOW))
                        .doOnEvent((combinations, throwable) -> progressState.postValue(HIDE))
                        .subscribe(this::loadGameRoundsSuccess, error::postValue));
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

    private void startNewRound() {
        if(listRounds.getValue().size() < 16) {
            newActualRound();
        } else {
            saveGame();
        }
    }
    private void newActualRound() {
        resetCurrentRound();
        mActualRound = new Round(game.getGameId(), listRounds.getValue().size() + 1);
        fillSeats();
    }
    private void resetCurrentRound() {
        resetStates();
        resetVariables();
        fillSeats();
    }
    private void resetStates() {
        fabMenuState.postValue(NORMAL);
        hidePlayersLayers();
        viewPagerPagingState.postValue(ENABLED);
        toolbarState.postValue(ToolbarState.NORMAL);
    }
    private void resetVariables() {
        mSelectedPlayerSeat = 0;
        mSelectedPlayerInitialPosition = 0;
        mIsTsumo = false;
        mHandPoints = 0;
        mPenaltyPoints = 0;
        mIsRequestingLooser = false;
        mIsRequestingHandPoints = false;
        mIsRequestingPenaltyPoints = false;
    }
    private void fillSeats() {
        Seat[] seats = getActualSeats();
        this.seats.postValue(seats);
    }
    private Seat[] getActualSeats() {
        Seat[] seats = new Seat[4];
        seats[Winds.EAST.getCode()] = getSeat(Round.getEastSeatPlayerByRound(mActualRound.getRoundId()));
        seats[Winds.SOUTH.getCode()] = getSeat(Round.getSouthSeatPlayerByRound(mActualRound.getRoundId()));
        seats[Winds.WEST.getCode()] = getSeat(Round.getWestSeatPlayerByRound(mActualRound.getRoundId()));
        seats[Winds.NORTH.getCode()] = getSeat(Round.getNorthSeatPlayerByRound(mActualRound.getRoundId()));
        return seats;
    }
    private Seat getSeat(int player) {
        assert listRounds.getValue() != null;
        if(EAST.getCode() == player) {
            return new Seat(game.getNameP1(),
                    mActualRound.isPenalizedPlayer(EAST.getCode()),
                    String.valueOf(GameRounds.getTotalScoreP1(listRounds.getValue())));
        } else if(SOUTH.getCode() == player) {
            return new Seat(game.getNameP2(),
                    mActualRound.isPenalizedPlayer(SOUTH.getCode()),
                    String.valueOf(GameRounds.getTotalScoreP2(listRounds.getValue())));
        } else if(WEST.getCode() == player) {
            return new Seat(game.getNameP3(),
                    mActualRound.isPenalizedPlayer(WEST.getCode()),
                    String.valueOf(GameRounds.getTotalScoreP3(listRounds.getValue())));
        } else {
            return new Seat(game.getNameP4(),
                    mActualRound.isPenalizedPlayer(NORTH.getCode()),
                    String.valueOf(GameRounds.getTotalScoreP4(listRounds.getValue())));
        }
    }

    public void onRequestHandPointsResponse(String handPointsInput) {
        if(checkAndConvertHandPoints(handPointsInput)) {
            mIsRequestingHandPoints = false;
            if(mIsTsumo) {
                saveTsumoRound();
            } else {
                requestLooser();
            }
        }
    }
    private boolean checkAndConvertHandPoints(String handPointsInput) {
        int handPoints;
        try {
            handPoints = Integer.valueOf(handPointsInput);
        } catch(NumberFormatException nfe) {
            showDialog.postValue(DialogType.REQUEST_POINTS_HAND);
            return false;
        }
        if(handPoints < 8) {
            showDialog.postValue(DialogType.REQUEST_POINTS_HAND);
            return false;
        }
        //TODO: CONTROLAR MÁXIMA PUNTUACIÓN POSIBLE
        mHandPoints = handPoints;
        return true;
    }
    private void saveTsumoRound() {
        mActualRound.setHandPoints(mHandPoints);
        mActualRound.setWinnerInitialPosition(mSelectedPlayerInitialPosition);
        mActualRound.setAllPlayersTsumoPoints(mSelectedPlayerInitialPosition, mHandPoints);
        saveActualRoundAndStartANewOneIfProceed();
    }
    private void requestLooser() {
        mIsRequestingLooser = true;
        fabMenuState.postValue(CANCEL);
        viewPagerPagingState.postValue(DISABLED);
        toolbarState.postValue(ToolbarState.REQUEST_LOOSER);
        hidePlayersLayers();
        winnerLayerSeat.postValue(Winds.getFromCode(mSelectedPlayerSeat));
    }
    private void hidePlayersLayers() {
        winnerLayerSeat.postValue(Winds.NONE);
        penaltyLayerSeats.postValue(new boolean[] { false, false, false, false });
    }
    public void onRequestHandPointsCancel() {
        resetCurrentRound();
    }
    public void onRequestPenaltyPointsResponse(String penaltyPointsInput) {
        if(checkAndConvertPenaltyPoints(penaltyPointsInput)) {
            mIsRequestingPenaltyPoints = false;
            mActualRound.setAllPlayersPointsByPenalty(mSelectedPlayerInitialPosition, mPenaltyPoints);
            resetCurrentRound();
        }
    }
    private boolean checkAndConvertPenaltyPoints(String penaltyPointsInput) {
        int penaltyPoints;
        try {
            penaltyPoints = Integer.valueOf(penaltyPointsInput);
        } catch(NumberFormatException nfe) {
            showDialog.postValue(DialogType.REQUEST_POINTS_PENALTI);
            return false;
        }
        if(penaltyPoints <= 0) {
            showDialog.postValue(DialogType.REQUEST_POINTS_PENALTI);
            return false;
        }
        //TODO: CONTROLAR MÁXIMA PUNTUACIÓN POSIBLE
        mPenaltyPoints = penaltyPoints;
        return true;
    }
    public void onRequestPenaltyPointsCancel() {
        resetCurrentRound();
    }

    public void onSeatClicked(Winds seatPosition) {
        if(mIsRequestingLooser) {
            saveRonRound(Round.getPlayerInitialPositionBySeat(seatPosition.getCode(), mActualRound.getRoundId()));
        } else {
            setSelectedPlayerSeatAndInitialPosition(seatPosition.getCode());
            winnerLayerSeat.postValue(Winds.getFromCode(mSelectedPlayerSeat));
            fabMenuState.postValue(mActualRound.isPenalizedPlayer(mSelectedPlayerInitialPosition) ? PLAYER_PENALIZED : PLAYER_SELECTED);
        }
    }
    private void saveRonRound(int looserInitialPosition) {
        mActualRound.setHandPoints(mHandPoints);
        mActualRound.setWinnerInitialPosition(mSelectedPlayerInitialPosition);
        mActualRound.setLooserInitialPosition(looserInitialPosition);
        mActualRound.setAllPlayersRonPoints(mSelectedPlayerInitialPosition, mHandPoints, looserInitialPosition);
        saveActualRoundAndStartANewOneIfProceed();
    }
    private void saveActualRoundAndStartANewOneIfProceed() {
        saveActualRound();
        startNewRound();
    }
    private void setSelectedPlayerSeatAndInitialPosition(int seatPosition) {
        mSelectedPlayerSeat = seatPosition;
        mSelectedPlayerInitialPosition = Round.getPlayerInitialPositionBySeat(mSelectedPlayerSeat, mActualRound.getRoundId());
    }
    public void onSeatLayerClicked(Winds seatPosition) {
        int playerInitialPositionBySeat = Round.getPlayerInitialPositionBySeat(seatPosition.getCode(), mActualRound.getRoundId());
        if(!mIsRequestingLooser && mActualRound.isPenalizedPlayer(playerInitialPositionBySeat)) {
            setSelectedPlayerSeatAndInitialPosition(seatPosition.getCode());
            fabMenuState.postValue(PLAYER_PENALIZED);
        }
    }
    public void onFamClosed(boolean isOpened) {
        if(!isOpened) {
            fabMenuState.postValue(NORMAL);
            if(mSelectedPlayerSeat != 0 &&
                    !mIsRequestingLooser &&
                    !mIsRequestingHandPoints &&
                    !mIsRequestingPenaltyPoints &&
                    !mActualRound.isPenalizedPlayer(mSelectedPlayerInitialPosition)) {
                selectedSeat.postValue(Winds.getFromCode(mSelectedPlayerSeat));
                mSelectedPlayerSeat = 0;
            }
        }
    }
    public void onFabGamePenaltyCancelClicked() {
        mActualRound.setAllPlayersPointsByPenaltyCancellation(mSelectedPlayerInitialPosition);
        resetCurrentRound();
    }
    public void onFabGamePenaltyClicked() {
        fabMenuState.postValue(HIDDEN);
        hidePlayersLayers();
        penaltyIconSeat.postValue(Winds.getFromCode(mSelectedPlayerSeat));
        mIsRequestingPenaltyPoints = true;
        showDialog.postValue(DialogType.REQUEST_POINTS_PENALTI);
    }
    public void onFabGameWashoutClicked() {
        saveActualRoundAndStartANewOneIfProceed();
        loadGameRounds();
    }
    public void onFabGameTsumoClicked() {
        fabMenuState.postValue(HIDDEN);
        tsumoIconSeat.postValue(Winds.getFromCode(mSelectedPlayerSeat));
        mIsTsumo = true;
        mIsRequestingHandPoints = true;
        showDialog.postValue(DialogType.REQUEST_POINTS_HAND);
    }
    public void onFabGameRonClicked() {
        fabMenuState.postValue(HIDDEN);
        ronIconSeat.postValue(Winds.getFromCode(mSelectedPlayerSeat));
        mIsTsumo = false;
        mIsRequestingHandPoints = true;
        showDialog.postValue(DialogType.REQUEST_POINTS_HAND);
    }
    public void onFabCancelRequestingLooserClicked() {
        resetCurrentRound();
    }
    public void toggleViewPagerPagingState(EnablingState state) {
        viewPagerPagingState.postValue(state);
    }
    public void setToolbarState(ToolbarState state) {
        toolbarState.postValue(state);
    }
    public void setFabMenuState(FabMenuStates state) {
        fabMenuState.postValue(state);
    }
    public void toggleFabMenuOpenState(ShowState showState) {
        fabMenuOpenState.postValue(showState);
    }
}
