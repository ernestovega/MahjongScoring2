package es.etologic.mahjongscoring2.app.old_games;

import android.arch.lifecycle.MutableLiveData;

import java.util.List;

import es.etologic.mahjongscoring2.app.base.BaseViewModel;
import es.etologic.mahjongscoring2.data.repositories.GamesRepository;
import es.etologic.mahjongscoring2.domain.entities.Game;

import static es.etologic.mahjongscoring2.app.model.ShowState.HIDE;
import static es.etologic.mahjongscoring2.app.model.ShowState.SHOW;

class OldGamesViewModel extends BaseViewModel {

    private GamesRepository gamesRepository;
    private MutableLiveData<List<Game>> oldGames = new MutableLiveData<List<Game>>() {};

    OldGamesViewModel(GamesRepository gamesRepository) {
        super(null);
        this.gamesRepository = gamesRepository; }

    MutableLiveData<List<Game>> getGames() { return oldGames; }

    void loadGames() {
        progressState.setValue(SHOW);
        oldGames.setValue(gamesRepository.getAll());
        progressState.setValue(HIDE);
    }

    void deleteGame(long gameId) {
        gamesRepository.deleteOne(gameId);
        loadGames();
    }
}
