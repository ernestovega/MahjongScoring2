package es.etologic.mahjongscoring2.app.main.old_games;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import es.etologic.mahjongscoring2.data.repositories.GamesRepository;

public class OldGamesViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private GamesRepository gamesRepository;

    public OldGamesViewModelFactory(GamesRepository gamesRepository) {
        this.gamesRepository = gamesRepository;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new OldGamesViewModel(gamesRepository);
    }
}
