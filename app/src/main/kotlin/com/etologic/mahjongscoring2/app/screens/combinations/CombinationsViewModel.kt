/*
 *     Copyright © 2024  Ernesto Vega de la Iglesia Soria
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.etologic.mahjongscoring2.app.screens.combinations

import androidx.lifecycle.viewModelScope
import com.etologic.mahjongscoring2.app.base.BaseViewModel
import com.etologic.mahjongscoring2.business.model.entities.Combination
import com.etologic.mahjongscoring2.data_source.repositories.CombinationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface CombinationsUiState {
    data object Loading : CombinationsUiState
    data class Loaded(val combinationsList: List<Combination>) : CombinationsUiState
    data object Empty : CombinationsUiState
}

@HiltViewModel
class CombinationsViewModel @Inject constructor(
    combinationsRepository: CombinationsRepository,
) : BaseViewModel() {

    private val combinationsFilter: MutableStateFlow<String> = MutableStateFlow("")

    val combinationsUiState: StateFlow<CombinationsUiState> =
        combine(
            flowOf(combinationsRepository.combinations),
            combinationsFilter,
        ) { combinations, filter ->
            val filteredCombinations = filterCombinations(combinations, filter)
            if (filteredCombinations.isEmpty()) {
                CombinationsUiState.Empty
            } else {
                CombinationsUiState.Loaded(filteredCombinations)
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, CombinationsUiState.Loading)

    private fun filterCombinations(
        allCombinations: Array<Combination>,
        filter: String,
    ): List<Combination> =
        allCombinations.filter { combination ->
            combination.combinationName.contains(filter, ignoreCase = true) ||
                    combination.combinationDescription?.contains(filter, ignoreCase = true) == true ||
                    combination.combinationPoints.toString().contains(filter, ignoreCase = true)
        }

    fun searchCombination(filter: String) {
        viewModelScope.launch { combinationsFilter.emit(filter) }
    }
}
