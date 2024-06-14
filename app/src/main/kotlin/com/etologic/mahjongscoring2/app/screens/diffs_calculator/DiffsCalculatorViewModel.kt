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
package com.etologic.mahjongscoring2.app.screens.diffs_calculator

import androidx.lifecycle.viewModelScope
import com.etologic.mahjongscoring2.app.base.BaseViewModel
import com.etologic.mahjongscoring2.app.model.Diff
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DiffsCalculatorUiState {
    data object Loading : DiffsCalculatorUiState
    data class Loaded(val diffsList: List<Diff>) : DiffsCalculatorUiState
}

@HiltViewModel
class DiffsCalculatorViewModel @Inject constructor() : BaseViewModel() {

    private val _diffsListFlow: MutableStateFlow<List<Diff>> = MutableStateFlow(getFirstInterval())

    val diffsCalculatorUiState: StateFlow<DiffsCalculatorUiState> =
        _diffsListFlow
            .map(DiffsCalculatorUiState::Loaded)
            .stateIn(viewModelScope, SharingStarted.Lazily, DiffsCalculatorUiState.Loading)

    private fun getFirstInterval(): List<Diff> =
        mutableListOf<Diff>().apply {
            for (i in MIN_POINTS_NEEDED..<MIN_POINTS_NEEDED + NUM_CALCS_INTERVAL) {
                add(Diff(i))
            }
        }

    fun loadNextInterval() {

        fun getNextInterval(): List<Diff> =
            mutableListOf<Diff>().apply {
                val numCurrentIntervals = _diffsListFlow.value.size / NUM_CALCS_INTERVAL
                if (numCurrentIntervals < MAX_INTERVALS) {
                    val nextFirstPointsNeeded = NUM_CALCS_INTERVAL * numCurrentIntervals + MIN_POINTS_NEEDED
                    for (i in nextFirstPointsNeeded..<nextFirstPointsNeeded + NUM_CALCS_INTERVAL) {
                        add(Diff(i))
                    }
                }
            }

        viewModelScope.launch {
            if (_diffsListFlow.value.size < MAX_ITEMS) {
                val diffListCopy = _diffsListFlow.value.toMutableList()
                val nextInterval = getNextInterval()
                diffListCopy.addAll(nextInterval)
                _diffsListFlow.emit(diffListCopy)
            }
        }
    }

    companion object {
        private const val MIN_POINTS_NEEDED: Int = 32
        const val MAX_INTERVALS: Int = 10
        const val NUM_CALCS_INTERVAL: Int = 200
        const val MAX_ITEMS: Int = NUM_CALCS_INTERVAL * MAX_INTERVALS
    }
}
