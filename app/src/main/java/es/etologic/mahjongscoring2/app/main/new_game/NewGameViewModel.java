package es.etologic.mahjongscoring2.app.main.new_game;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.List;

import es.etologic.mahjongscoring2.app.base.BaseViewModel;
import es.etologic.mahjongscoring2.app.model.ShowState;
import es.etologic.mahjongscoring2.domain.entities.Player;
import es.etologic.mahjongscoring2.domain.use_cases.CreateGameUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.CreatePlayerUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetAllPlayersUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetPlayerUseCase;
import io.reactivex.schedulers.Schedulers;

class NewGameViewModel extends BaseViewModel {

    //FIELDS
    private GetAllPlayersUseCase getAllPlayersUseCase;
    private CreatePlayerUseCase createPlayerUseCase;
    private GetPlayerUseCase getPlayerUseCase;
    private CreateGameUseCase createGameUseCase;
    private MutableLiveData<List<Player>> allPlayers = new MutableLiveData<>();
    private MutableLiveData<Player> newPlayer = new MutableLiveData<>();
    private MutableLiveData<Long> newGameId = new MutableLiveData<>();
    private MutableLiveData<ShowState> toolbarProgress = new MutableLiveData<>();

    //CONSTRUCTOR
    NewGameViewModel(GetAllPlayersUseCase getAllPlayersUseCase, CreatePlayerUseCase createPlayerUseCase,
                     GetPlayerUseCase getPlayerUseCase, CreateGameUseCase createGameUseCase) {
        this.getAllPlayersUseCase = getAllPlayersUseCase;
        this.createPlayerUseCase = createPlayerUseCase;
        this.getPlayerUseCase = getPlayerUseCase;
        this.createGameUseCase = createGameUseCase;
    }

    //OBSERVABLES
    LiveData<List<Player>> getAllPlayers() { return allPlayers; }
    LiveData<Player> getNewPlayer() { return newPlayer; }
    LiveData<Long> getNewGameId() { return newGameId; }
    LiveData<ShowState> getToolbarProgress() { return toolbarProgress; }

    //METHODS
    void bindAllPlayers() {
        disposables.add(
                getAllPlayersUseCase.getAll()
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(observer -> progressState.postValue(ShowState.SHOW))
                        .doOnEvent((observer, throwable) -> progressState.postValue(ShowState.HIDE))
                        .subscribe(allPlayers::postValue, error::postValue));
    }
    void createPlayer(String playerName) {
        disposables.add(
                createPlayerUseCase.createPlayer(playerName)
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(observer -> progressState.postValue(ShowState.SHOW))
                        .doOnEvent((observer, throwable) -> progressState.postValue(ShowState.HIDE))
                        .flatMap(newPlayerId -> getPlayerUseCase.getPlayer(newPlayerId))
                        .subscribe(newPlayer::postValue, throwable -> snackbarMessage.postValue(throwable.getMessage())));
    }
    void createGame(List<String> playersNames) {
        disposables.add(
                createGameUseCase.createGame(playersNames)
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(observer -> progressState.postValue(ShowState.SHOW))
                        .doOnEvent((observer, throwable) -> progressState.postValue(ShowState.HIDE))
                        .subscribe(newGameId::postValue, throwable -> snackbarMessage.postValue(throwable.getMessage())));
    }
}
