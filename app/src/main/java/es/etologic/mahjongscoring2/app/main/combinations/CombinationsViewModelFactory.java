package es.etologic.mahjongscoring2.app.main.combinations;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import es.etologic.mahjongscoring2.data.repositories.CombinationsRepository;

public class CombinationsViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final CombinationsRepository combinationsRepository;

    public CombinationsViewModelFactory(CombinationsRepository combinationsRepository) {
        this.combinationsRepository = combinationsRepository;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new CombinationsViewModel(combinationsRepository);
    }
}
