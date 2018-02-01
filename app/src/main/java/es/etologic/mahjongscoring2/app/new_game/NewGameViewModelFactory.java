package es.etologic.mahjongscoring2.app.new_game;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import es.etologic.mahjongscoring2.app.base.BaseViewModelFactory;
import es.etologic.mahjongscoring2.domain.threading.UseCaseHandler;
import es.etologic.mahjongscoring2.domain.use_cases.CreateGameUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.CreatePlayerUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetPlayersUseCase;

public class NewGameViewModelFactory extends BaseViewModelFactory {

    private final GetPlayersUseCase getPlayersUseCase;
    private final CreatePlayerUseCase createPlayerUseCase;
    private final CreateGameUseCase createGameUseCase;

    public NewGameViewModelFactory(UseCaseHandler useCaseHandler,
                                   GetPlayersUseCase getPlayersUseCase,
                                   CreatePlayerUseCase createPlayerUseCase,
                                   CreateGameUseCase createGameUseCase) {
        super(useCaseHandler);
        this.getPlayersUseCase = getPlayersUseCase;
        this.createPlayerUseCase = createPlayerUseCase;
        this.createGameUseCase = createGameUseCase;
    }

    @SuppressWarnings ("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T)new NewGameViewModel(useCaseHandler, getPlayersUseCase, createPlayerUseCase,
                createGameUseCase);
    }
}
