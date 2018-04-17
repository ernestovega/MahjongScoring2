package es.etologic.mahjongscoring2.app.game.activity;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import es.etologic.mahjongscoring2.data.repositories.GamesRepository;
import es.etologic.mahjongscoring2.data.repositories.RoundsRepository;

public class GameActivityViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final GamesRepository gamesRepository;
    private final RoundsRepository roundsRepository;

    public GameActivityViewModelFactory(GamesRepository gamesRepository, RoundsRepository roundsRepository) {
        this.gamesRepository = gamesRepository;
        this.roundsRepository = roundsRepository;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new GameActivityViewModel(gamesRepository, roundsRepository);
    }
}
