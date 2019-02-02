package es.etologic.mahjongscoring2.app.main.combinations;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.List;

import es.etologic.mahjongscoring2.app.base.BaseViewModel;
import es.etologic.mahjongscoring2.domain.entities.Combination;
import es.etologic.mahjongscoring2.domain.use_cases.GetCombinationsUseCase;
import io.reactivex.schedulers.Schedulers;

import static es.etologic.mahjongscoring2.app.model.ShowState.HIDE;
import static es.etologic.mahjongscoring2.app.model.ShowState.SHOW;

class CombinationsViewModel extends BaseViewModel {

    //FIELDS
    private final GetCombinationsUseCase getCombinationsUseCase;
    private MutableLiveData<List<Combination>> filteredCombinations = new MutableLiveData<>();

    //CONSTRUCTOR
    CombinationsViewModel(GetCombinationsUseCase getCombinationsUseCase) {
        this.getCombinationsUseCase = getCombinationsUseCase;

    }

    //OBSERVABLES
    LiveData<List<Combination>> getFilteredCombinations() {
        return filteredCombinations;
    }

    //METHODS
    void getAll() {
        disposables.add(
                getCombinationsUseCase.getAll()
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(disposable -> progressState.postValue(SHOW))
                        .doOnEvent((combinations, throwable) -> progressState.postValue(HIDE))
                        .subscribe(filteredCombinations::postValue, error::postValue));
    }
    void searchCombination(String filter) {
        disposables.add(
                getCombinationsUseCase.getSome(filter)
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(disposable -> progressState.postValue(SHOW))
                        .doOnEvent((combinations, throwable) -> progressState.postValue(HIDE))
                        .subscribe(filteredCombinations::postValue, error::postValue));
    }
}
