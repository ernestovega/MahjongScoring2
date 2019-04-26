package es.etologic.mahjongscoring2.app.main.old_games;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.List;

import es.etologic.mahjongscoring2.app.base.BaseViewModel;
import es.etologic.mahjongscoring2.domain.model.GameWithRounds;
import es.etologic.mahjongscoring2.domain.use_cases.DeleteGameUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetGamesUseCase;
import io.reactivex.schedulers.Schedulers;

import static es.etologic.mahjongscoring2.app.model.ShowState.*;

class OldGamesViewModel extends BaseViewModel {

    //FIELDS
    private GetGamesUseCase getGamesUseCase;
    private DeleteGameUseCase deleteGameUseCase;
    private MutableLiveData<List<GameWithRounds>> allGames = new MutableLiveData<>();

    //CONSTRUCTOR
    OldGamesViewModel(GetGamesUseCase getGamesUseCase, DeleteGameUseCase deleteGameUseCase) {
        this.getGamesUseCase = getGamesUseCase;
        this.deleteGameUseCase = deleteGameUseCase;
    }

    //OBSERVABLES
    LiveData<List<GameWithRounds>> getGames() { return allGames; }

    //METHODS
    void getAllGames() {
        disposables.add(
                getGamesUseCase.getAllWithRounds()
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
