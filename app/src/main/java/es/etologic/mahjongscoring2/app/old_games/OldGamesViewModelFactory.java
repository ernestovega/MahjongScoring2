package es.etologic.mahjongscoring2.app.old_games;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import es.etologic.mahjongscoring2.app.base.BaseViewModelFactory;
import es.etologic.mahjongscoring2.domain.threading.UseCaseHandler;
import es.etologic.mahjongscoring2.domain.use_cases.GetOldGamesUseCase;

public class OldGamesViewModelFactory extends BaseViewModelFactory {

    private final GetOldGamesUseCase getOldGamesUseCase;

    public OldGamesViewModelFactory(
            UseCaseHandler useCaseHandler,
            GetOldGamesUseCase getOldGamesUseCase) {
        super(useCaseHandler);
        this.getOldGamesUseCase = getOldGamesUseCase;
    }

    @SuppressWarnings ("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T)new OldGamesViewModel(useCaseHandler, getOldGamesUseCase);
    }
}
