package es.etologic.mahjongscoring2.app.main.combinations;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import es.etologic.mahjongscoring2.data.repositories.CombinationsRepository;
import es.etologic.mahjongscoring2.domain.entities.Combination;

class CombinationsViewModel extends ViewModel {

    //FIELDS
    private final CombinationsRepository combinationsRepository;
    private MutableLiveData<List<Combination>> combinations = new MutableLiveData<>();

    //CONSTRUCTOR
    CombinationsViewModel(CombinationsRepository combinationsRepository) {
        this.combinationsRepository = combinationsRepository;
    }

    //OBSERVABLES
    MutableLiveData<List<Combination>> getCombinations() {
        return combinations;
    }

    void loadCombinations() {
//        progressState.setValue(SHOW);
        combinations.setValue(combinationsRepository.getAll());
//        progressState.setValue(HIDE);
    }

    void searchCombination(String text) {
//        progressState.setValue(SHOW);
        combinations.setValue(combinationsRepository.getFilteredCombinations(text));
//        progressState.setValue(HIDE);
    }
}
