package es.etologic.mahjongscoring2.app.combinations;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import es.etologic.mahjongscoring2.app.base.BaseViewModelFactory;
import es.etologic.mahjongscoring2.domain.threading.UseCaseHandler;
import es.etologic.mahjongscoring2.domain.use_cases.GetCombinationsUseCase;

public class CombinationsViewModelFactory extends BaseViewModelFactory {

    private final GetCombinationsUseCase getCombinationsUseCase;

    public CombinationsViewModelFactory(
            UseCaseHandler useCaseHandler,
            GetCombinationsUseCase getCombinationsUseCase) {
        super(useCaseHandler);
        this.getCombinationsUseCase = getCombinationsUseCase;
    }

    @SuppressWarnings ("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T)new CombinationsViewModel(useCaseHandler, getCombinationsUseCase);
    }
}
