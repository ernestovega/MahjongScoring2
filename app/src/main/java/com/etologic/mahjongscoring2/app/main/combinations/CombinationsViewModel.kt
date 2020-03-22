package com.etologic.mahjongscoring2.app.main.combinations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.etologic.mahjongscoring2.app.base.BaseViewModel
import com.etologic.mahjongscoring2.app.model.ShowState.HIDE
import com.etologic.mahjongscoring2.app.model.ShowState.SHOW
import com.etologic.mahjongscoring2.business.model.entities.Combination
import com.etologic.mahjongscoring2.business.use_cases.combinations.GetCombinationsUseCase
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
                .doOnSuccess(filteredCombinations::postValue)
                .subscribe({ progressState.postValue(HIDE) }, this::showError)
        )
    }
    
    fun searchCombination(filter: String) {
        disposables.add(
            getCombinationsUseCase.getSome(filter)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnSuccess(filteredCombinations::postValue)
                .subscribe({ progressState.postValue(HIDE) }, this::showError)
        )
    }
}
