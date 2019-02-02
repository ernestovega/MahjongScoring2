package es.etologic.mahjongscoring2.app.main.old_games;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.List;

import es.etologic.mahjongscoring2.app.base.BaseViewModel;
import es.etologic.mahjongscoring2.app.model.ShowState;
import es.etologic.mahjongscoring2.domain.entities.Game;
import es.etologic.mahjongscoring2.domain.use_cases.DeleteGameUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetAllGamesUseCase;
import io.reactivex.schedulers.Schedulers;

import static es.etologic.mahjongscoring2.app.model.ShowState.*;

class OldGamesViewModel extends BaseViewModel {

    //FIELDS
    private GetAllGamesUseCase getAllGamesUseCase;
    private DeleteGameUseCase deleteGameUseCase;
    private MutableLiveData<List<Game>> allGames = new MutableLiveData<>();

    //CONSTRUCTOR
    OldGamesViewModel(GetAllGamesUseCase getAllGamesUseCase, DeleteGameUseCase deleteGameUseCase) {
        this.getAllGamesUseCase = getAllGamesUseCase;
        this.deleteGameUseCase = deleteGameUseCase;
    }

    //OBSERVABLES
    LiveData<List<Game>> getGames() { return allGames; }

    //METHODS
    void getAllGames() {
        disposables.add(
                getAllGamesUseCase.getAll()
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(observer -> progressState.postValue(SHOW))
                        .doOnEvent((observer, throwable) -> progressState.postValue(HIDE))
                        .subscribe(allGames::postValue, error::postValue));
    }
    void deleteGame(long gameId) {
        disposables.add(
                deleteGameUseCase.deleteGame(gameId)
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(observer -> progressState.postValue(SHOW))
                        .doOnEvent((observer, throwable) -> progressState.postValue(HIDE))
                        .subscribe(result -> getAllGames(), error::postValue));
    }
}
