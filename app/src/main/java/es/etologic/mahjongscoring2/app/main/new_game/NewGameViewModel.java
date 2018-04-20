package es.etologic.mahjongscoring2.app.main.new_game;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.List;

import es.etologic.mahjongscoring2.app.base.BaseViewModel;
import es.etologic.mahjongscoring2.app.model.ShowState;
import es.etologic.mahjongscoring2.domain.entities.Game;
import es.etologic.mahjongscoring2.domain.entities.Player;
import es.etologic.mahjongscoring2.domain.operation_objects.BaseError;
import es.etologic.mahjongscoring2.domain.operation_objects.OperationResult;
import es.etologic.mahjongscoring2.domain.use_cases.CreateGameUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.CreatePlayerUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetAllPlayersUseCase;

import static es.etologic.mahjongscoring2.app.model.ShowState.HIDE;
import static es.etologic.mahjongscoring2.app.model.ShowState.SHOW;
import static es.etologic.mahjongscoring2.domain.operation_objects.OperationState.SUCCESS;

class NewGameViewModel extends BaseViewModel {

    //FIELDS
    private GetAllPlayersUseCase getAllPlayersUseCase;
    private CreatePlayerUseCase createPlayerUseCase;
    private CreateGameUseCase createGameUseCase;
    private MutableLiveData<List<Player>> allPlayers = new MutableLiveData<>();
    private MutableLiveData<Player> newPlayer = new MutableLiveData<>();
    private MutableLiveData<Long> newGameId = new MutableLiveData<>();
    private MutableLiveData<ShowState> toolbarProgress = new MutableLiveData<>();

    //CONSTRUCTOR
    NewGameViewModel(GetAllPlayersUseCase getAllPlayersUseCase, CreatePlayerUseCase createPlayerUseCase, CreateGameUseCase createGameUseCase) {
        this.getAllPlayersUseCase = getAllPlayersUseCase;
        this.createPlayerUseCase = createPlayerUseCase;
        this.createGameUseCase = createGameUseCase;
    }

    //OBSERVABLES
    LiveData<List<Player>> getAllPlayers() { return allPlayers; }
    LiveData<Player> getNewPlayer() { return newPlayer; }
    LiveData<Long> getNewGameId() { return newGameId; }
    LiveData<ShowState> getToolbarProgress() { return toolbarProgress; }

    //METHODS
    void bindAllPlayers() { allPlayers.postValue(getAllPlayersUseCase.execute().getValue()); }
    void createPlayer(String playerName) {
        progressState.setValue(SHOW);
        OperationResult<Player, BaseError> operationResult = createPlayerUseCase.execute(playerName);
        progressState.setValue(HIDE);
        if(operationResult.getState() == SUCCESS) newPlayer.setValue(operationResult.getResponse());
        else snackbarMessage.setValue(operationResult.getError().getMessage());
    }
    void createGame(List<String> playersNames) {
        progressState.setValue(SHOW);
        OperationResult<Long, BaseError> operationResult = createGameUseCase.execute(playersNames);
        progressState.setValue(HIDE);
        if(operationResult.getState() == SUCCESS) newGameId.setValue(operationResult.getResponse());
        else snackbarMessage.setValue(operationResult.getError().getMessage());
    }
}
