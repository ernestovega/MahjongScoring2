/*
    Copyright (C) 2020  Ernesto Vega de la Iglesia Soria

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.etologic.mahjongscoring2.app.main.combinations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.etologic.mahjongscoring2.app.base.BaseViewModel
import com.etologic.mahjongscoring2.business.model.entities.Combination
import com.etologic.mahjongscoring2.business.use_cases.combinations.GetCombinationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class CombinationsViewModel @Inject constructor(
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
                .subscribe(filteredCombinations::postValue, this::showError)
        )
    }

    fun searchCombination(filter: String) {
        disposables.add(
            getCombinationsUseCase.getSome(filter)
                .subscribeOn(Schedulers.io())
                .subscribe(filteredCombinations::postValue, this::showError)
        )
    }
}
