package es.etologic.mahjongscoring2.app.game.activity;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import es.etologic.mahjongscoring2.data.repositories.GamesRepository;
import es.etologic.mahjongscoring2.data.repositories.RoundsRepository;
import es.etologic.mahjongscoring2.domain.entities.Game;

public class GameActivityViewModel extends ViewModel {

    //region Fields

    private final GamesRepository gamesRepository;
    private final RoundsRepository roundsRepository;
    private MutableLiveData<Game> game = new MutableLiveData<>();
    private MutableLiveData<Boolean> gameFinished = new MutableLiveData<>();

    //endregion

    //region Constructor

    GameActivityViewModel(GamesRepository gamesRepository, RoundsRepository roundsRepository) {
        this.gamesRepository = gamesRepository;
        this.roundsRepository = roundsRepository;
    }

    //endregion

    //region Observables

    public LiveData<Game> getGame() {
        return game;
    }

    LiveData<Boolean> isGameFinished() {
        return gameFinished;
    }

    //endregion

    //region Public

    void loadGame(long gameId) {
//        progressState.setValue(SHOW);
        game.setValue(gamesRepository.getOne(gameId));
//        progressState.setValue(HIDE);
    }

    void endGame() {
    }

    //endregion

    //region Private

    //endregion
}
