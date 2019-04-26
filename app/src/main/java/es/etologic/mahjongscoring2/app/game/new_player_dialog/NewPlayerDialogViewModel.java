package es.etologic.mahjongscoring2.app.game.new_player_dialog;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.List;

import es.etologic.mahjongscoring2.app.base.BaseViewModel;
import es.etologic.mahjongscoring2.app.model.ShowState;
import es.etologic.mahjongscoring2.domain.model.Player;
import es.etologic.mahjongscoring2.domain.use_cases.CreateGameUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.CreatePlayerUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetAllPlayersUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetPlayerUseCase;
import io.reactivex.schedulers.Schedulers;

class NewPlayerDialogViewModel extends BaseViewModel {

    //FIELDS
    private GetAllPlayersUseCase getAllPlayersUseCase;
    private CreatePlayerUseCase createPlayerUseCase;
    private GetPlayerUseCase getPlayerUseCase;
    private MutableLiveData<List<Player>> allPlayers = new MutableLiveData<>();
    private MutableLiveData<Player> newPlayer = new MutableLiveData<>();

    //CONSTRUCTOR
    NewPlayerDialogViewModel(GetAllPlayersUseCase getAllPlayersUseCase, CreatePlayerUseCase createPlayerUseCase,
                             GetPlayerUseCase getPlayerUseCase) {
        this.getAllPlayersUseCase = getAllPlayersUseCase;
        this.createPlayerUseCase = createPlayerUseCase;
        this.getPlayerUseCase = getPlayerUseCase;
    }

    //OBSERVABLES
    LiveData<List<Player>> getAllPlayers() { return allPlayers; }
    LiveData<Player> getNewPlayer() { return newPlayer; }

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
}
