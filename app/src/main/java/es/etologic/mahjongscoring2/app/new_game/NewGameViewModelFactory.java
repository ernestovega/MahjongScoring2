package es.etologic.mahjongscoring2.app.new_game;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import es.etologic.mahjongscoring2.app.base.BaseViewModelFactory;
import es.etologic.mahjongscoring2.domain.threading.UseCaseHandler;
import es.etologic.mahjongscoring2.domain.use_cases.GetPlayersUseCase;

public class NewGameViewModelFactory extends BaseViewModelFactory {

    private final GetPlayersUseCase getPlayersUseCase;

    public NewGameViewModelFactory(
            UseCaseHandler useCaseHandler,
            GetPlayersUseCase getPlayersUseCase) {
        super(useCaseHandler);
        this.getPlayersUseCase = getPlayersUseCase;
    }

    @SuppressWarnings ("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T)new NewGameViewModel(useCaseHandler, getPlayersUseCase);
    }
}
