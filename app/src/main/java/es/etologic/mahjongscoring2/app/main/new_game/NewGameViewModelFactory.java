package es.etologic.mahjongscoring2.app.main.new_game;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import es.etologic.mahjongscoring2.domain.use_cases.CreateGameUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.CreatePlayerUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetAllPlayersUseCase;

public class NewGameViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final GetAllPlayersUseCase getAllPlayersUseCase;
    private final CreatePlayerUseCase createPlayerUseCase;
    private final CreateGameUseCase createGameUseCase;

    public NewGameViewModelFactory(GetAllPlayersUseCase getAllPlayersUseCase, CreatePlayerUseCase createPlayerUseCase,
                                   CreateGameUseCase createGameUseCase) {
        this.getAllPlayersUseCase = getAllPlayersUseCase;
        this.createPlayerUseCase = createPlayerUseCase;
        this.createGameUseCase = createGameUseCase;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new NewGameViewModel(getAllPlayersUseCase, createPlayerUseCase, createGameUseCase);
    }
}
