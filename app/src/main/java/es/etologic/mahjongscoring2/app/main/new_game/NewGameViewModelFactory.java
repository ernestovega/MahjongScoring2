package es.etologic.mahjongscoring2.app.main.new_game;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import es.etologic.mahjongscoring2.data.repositories.GamesRepository;
import es.etologic.mahjongscoring2.data.repositories.PlayersRepository;

public class NewGameViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final PlayersRepository playersRepository;
    private final GamesRepository gamesRepository;

    public NewGameViewModelFactory(PlayersRepository playersRepository, GamesRepository gamesRepository) {
        this.playersRepository = playersRepository;
        this.gamesRepository = gamesRepository;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new NewGameViewModel(playersRepository, gamesRepository);
    }
}
