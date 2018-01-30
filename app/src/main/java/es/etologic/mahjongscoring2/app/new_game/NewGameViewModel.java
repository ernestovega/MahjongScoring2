package es.etologic.mahjongscoring2.app.new_game;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import es.etologic.mahjongscoring2.app.base.BaseViewModel;
import es.etologic.mahjongscoring2.domain.entities.Player;
import es.etologic.mahjongscoring2.domain.threading.UseCase;
import es.etologic.mahjongscoring2.domain.threading.UseCaseHandler;
import es.etologic.mahjongscoring2.domain.use_cases.GetPlayersUseCase;

import static es.etologic.mahjongscoring2.app.model.ShowState.HIDE;
import static es.etologic.mahjongscoring2.app.model.ShowState.SHOW;

class NewGameViewModel extends BaseViewModel {

    private final GetPlayersUseCase getPlayersUseCase;
    private final CreatePlayerUseCase createPlayerUseCase;
    private final CreateGameUseCase createGameUseCase;
    private MutableLiveData<List<Player>> allPlayers = new MutableLiveData<List<Player>>() {};
    private MutableLiveData<Player> newPlayer = new MutableLiveData<Player>() {};
    private MutableLiveData<Integer> newGameId = new MutableLiveData<Integer>() {};

    NewGameViewModel(UseCaseHandler useCaseHandler, GetPlayersUseCase getPlayersUseCase,
                     CreatePlayerUseCase createPlayerUseCase, CreateGameUseCase createGameUseCase) {
        super(useCaseHandler);
        this.getPlayersUseCase = getPlayersUseCase;
        this.createPlayerUseCase = createPlayerUseCase;
        this.createGameUseCase = createGameUseCase;
    }

    LiveData<List<Player>> getAllPlayers() {
        return allPlayers;
    }
    LiveData<Player> getNewPlayer() {
        return newPlayer;
    }
    LiveData<Integer> getNewGameId() {
        return newGameId;
    }

    void loadAllPlayers() {
        progressState.setValue(SHOW);
        useCaseHandler.execute(getPlayersUseCase, null,
                new UseCase.UseCaseCallback<GetPlayersUseCase.ResponseValue>() {
                    @Override
                    public void onSuccess(GetPlayersUseCase.ResponseValue response) {
                        allPlayers.setValue(response.getPlayers());
                        progressState.setValue(HIDE);
                    }

                    @Override
                    public void onError(String ignored) {
                        allPlayers.setValue(new ArrayList<>());
                        progressState.setValue(HIDE);
                    }
                });
    }

    void createPlayer(String playerName) {
        progressState.setValue(SHOW);
        useCaseHandler.execute(createPlayerUseCase, createPlayerRequest,
                new UseCase.UseCaseCallback<CreatePlayerUseCase.ResponseValue>() {
                    @Override
                    public void onSuccess(CreatePlayerUseCase.ResponseValue response) {
                        newPlayer.setValue(response.getPlayer());
                        progressState.setValue(HIDE);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        snackbarMessage.setValue(errorMessage);
                        progressState.setValue(HIDE);
                    }
                });
    }

    void createGame(List<Player> newGamePlayers) {
        progressState.setValue(SHOW);
        useCaseHandler.execute(createGameUseCase, createGameRequest,
                new UseCase.UseCaseCallback<CreateGameUseCase.ResponseValue>() {

                    @Override
                    public void onSuccess(CreateGameUseCase.ResponseValue response) {
                        newGameId.setValue(response.getGameId());
                        progressState.setValue(HIDE);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        snackbarMessage.setValue(errorMessage);
                        progressState.setValue(HIDE);
                    }
                });
    }
}
