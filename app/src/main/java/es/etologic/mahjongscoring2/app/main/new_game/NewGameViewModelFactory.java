package es.etologic.mahjongscoring2.app.main.new_game;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import es.etologic.mahjongscoring2.domain.use_cases.CreateGameUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.CreatePlayerUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetAllPlayersUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetPlayerUseCase;

public class NewGameViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final GetAllPlayersUseCase getAllPlayersUseCase;
    private final CreatePlayerUseCase createPlayerUseCase;
    private final CreateGameUseCase createGameUseCase;
    private GetPlayerUseCase getPlayerUseCase;

    @Inject
    public NewGameViewModelFactory(GetAllPlayersUseCase getAllPlayersUseCase, CreatePlayerUseCase createPlayerUseCase,
                                   GetPlayerUseCase getPlayerUseCase, CreateGameUseCase createGameUseCase) {
        this.getAllPlayersUseCase = getAllPlayersUseCase;
        this.createPlayerUseCase = createPlayerUseCase;
        this.getPlayerUseCase = getPlayerUseCase;
        this.createGameUseCase = createGameUseCase;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new NewGameViewModel(getAllPlayersUseCase, createPlayerUseCase, getPlayerUseCase, createGameUseCase);
    }
}
