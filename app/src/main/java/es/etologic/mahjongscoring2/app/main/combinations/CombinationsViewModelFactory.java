package es.etologic.mahjongscoring2.app.main.combinations;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import es.etologic.mahjongscoring2.domain.use_cases.GetAllCombinationsUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetFilteredCombinationsUseCase;

public class CombinationsViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final GetAllCombinationsUseCase getAllCombinationsUseCase;
    private final GetFilteredCombinationsUseCase getFilteredCombinationsUseCase;

    public CombinationsViewModelFactory(GetAllCombinationsUseCase getAllCombinationsUseCase,
                                        GetFilteredCombinationsUseCase getFilteredCombinationsUseCase) {
        this.getAllCombinationsUseCase = getAllCombinationsUseCase;
        this.getFilteredCombinationsUseCase = getFilteredCombinationsUseCase;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new CombinationsViewModel(getAllCombinationsUseCase, getFilteredCombinationsUseCase);
    }
}
