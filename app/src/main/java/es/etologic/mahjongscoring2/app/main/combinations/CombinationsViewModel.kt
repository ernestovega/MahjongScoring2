package es.etologic.mahjongscoring2.app.main.combinations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.etologic.mahjongscoring2.app.base.BaseViewModel
import es.etologic.mahjongscoring2.app.model.ShowState.HIDE
import es.etologic.mahjongscoring2.app.model.ShowState.SHOW
import es.etologic.mahjongscoring2.domain.model.Combination
import es.etologic.mahjongscoring2.domain.use_cases.GetCombinationsUseCase
import io.reactivex.schedulers.Schedulers

internal class CombinationsViewModel(
    private val getCombinationsUseCase: GetCombinationsUseCase
) : BaseViewModel() {
    
    private val filteredCombinations = MutableLiveData<List<Combination>>()
    
    fun getFilteredCombinations(): LiveData<List<Combination>> {
        return filteredCombinations
    }
    
    fun getAll() {
        disposables.add(
            getCombinationsUseCase.getAll()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnEvent { _, _ -> progressState.postValue(HIDE) }
                .subscribe(filteredCombinations::postValue, error::postValue)
        )
    }
    
    fun searchCombination(filter: String) {
        disposables.add(
            getCombinationsUseCase.getSome(filter)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnEvent { _, _ -> progressState.postValue(HIDE) }
                .subscribe(filteredCombinations::postValue, error::postValue)
        )
    }
}
