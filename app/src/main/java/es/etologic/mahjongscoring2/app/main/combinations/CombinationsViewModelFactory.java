package es.etologic.mahjongscoring2.app.main.combinations;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import es.etologic.mahjongscoring2.domain.use_cases.GetCombinationsUseCase;

public class CombinationsViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final GetCombinationsUseCase getCombinationsUseCase;

    @Inject
    public CombinationsViewModelFactory(GetCombinationsUseCase getCombinationsUseCase) {
        this.getCombinationsUseCase = getCombinationsUseCase;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new CombinationsViewModel(getCombinationsUseCase);
    }
}
