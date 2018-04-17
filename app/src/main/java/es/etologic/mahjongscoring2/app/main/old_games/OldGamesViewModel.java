package es.etologic.mahjongscoring2.app.main.old_games;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import es.etologic.mahjongscoring2.data.repositories.GamesRepository;
import es.etologic.mahjongscoring2.domain.entities.Game;

class OldGamesViewModel extends ViewModel {

    //FIELDS
    private GamesRepository gamesRepository;
    private MutableLiveData<List<Game>> oldGames = new MutableLiveData<>();

    //CONSTRUCTOR
    OldGamesViewModel(GamesRepository gamesRepository) {
        this.gamesRepository = gamesRepository;
    }

    //OBSERVABLES
    MutableLiveData<List<Game>> getGames() {
        return oldGames;
    }

    //METHODS
    void deleteGame(long gameId) {
        gamesRepository.deleteOne(gameId);
        loadGames();
    }
    void loadGames() {
//        progressState.setValue(SHOW);
        oldGames.setValue(gamesRepository.getAll());
//        progressState.setValue(HIDE);
    }
}
