/*
    Copyright (C) 2023  Ernesto Vega de la Iglesia Soria

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

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.base.BaseViewModel
import com.etologic.mahjongscoring2.business.model.entities.Combination
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

@HiltViewModel
class CombinationsViewModel @Inject constructor(
    @ApplicationContext context: Context,
) : BaseViewModel() {

    private val combinations: Array<Combination> = arrayOf(
            Combination(
                combinationPoints = 1,
                combinationName = context.getString(R.string.pure_double_chow),
                combinationImage = R.drawable.pure_double_chow
            ),
            Combination(
                combinationPoints = 1,
                combinationName = context.getString(R.string.mixed_double_chow),
                combinationImage = R.drawable.mixed_double_chow
            ),
            Combination(
                combinationPoints = 1,
                combinationName = context.getString(R.string.short_straight),
                combinationImage = R.drawable.short_straight
            ),
            Combination(
                combinationPoints = 1,
                combinationName = context.getString(R.string.two_terminal_chows),
                combinationImage = R.drawable.two_terminal_chows
            ),
            Combination(
                combinationPoints = 1,
                combinationName = context.getString(R.string.pung_of_terminals_or_honors),
                combinationImage = R.drawable.pung_of_terminal_or_honors
            ),
            Combination(
                combinationPoints = 1,
                combinationName = context.getString(R.string.melded_kong),
                combinationImage = R.drawable.melded_kong
            ),
            Combination(
                combinationPoints = 1,
                combinationName = context.getString(R.string.one_voided_suit),
                combinationImage = R.drawable.one_voided_suit
            ),
            Combination(
                combinationPoints = 1,
                combinationName = context.getString(R.string.no_honors),
                combinationImage = R.drawable.no_honors
            ),
            Combination(
                combinationPoints = 1,
                combinationName = context.getString(R.string.edge_wait),
                combinationDescription = context.getString(R.string.description_edge_wait)
            ),
            Combination(
                combinationPoints = 1,
                combinationName = context.getString(R.string.closed_wait),
                combinationDescription = context.getString(R.string.description_closed_wait)
            ),
            Combination(
                combinationPoints = 1,
                combinationName = context.getString(R.string.single_waiting),
                combinationDescription = context.getString(R.string.description_single_waiting)
            ),
            Combination(
                combinationPoints = 1,
                combinationName = context.getString(R.string.self_drawn),
                combinationDescription = context.getString(R.string.description_self_drawn)
            ),
            Combination(
                combinationPoints = 1,
                combinationName = context.getString(R.string.flower_tiles),
                combinationImage = R.drawable.flower_tiles
            ),
            Combination(
                combinationPoints = 2,
                combinationName = context.getString(R.string.dragon_pung),
                combinationImage = R.drawable.dragon_pung
            ),
            Combination(
                combinationPoints = 2,
                combinationName = context.getString(R.string.prevalent_wind),
                combinationImage = R.drawable.prevalent_wind
            ),
            Combination(
                combinationPoints = 2,
                combinationName = context.getString(R.string.seat_wind),
                combinationImage = R.drawable.seat_wind
            ),
            Combination(
                combinationPoints = 2,
                combinationName = context.getString(R.string.concealed_hand),
                combinationDescription = context.getString(R.string.description_concealed_hand)
            ),
            Combination(
                combinationPoints = 2,
                combinationName = context.getString(R.string.all_chows),
                combinationImage = R.drawable.all_chows
            ),
            Combination(
                combinationPoints = 2,
                combinationName = context.getString(R.string.tile_hog),
                combinationImage = R.drawable.tile_hog
            ),
            Combination(
                combinationPoints = 2,
                combinationName = context.getString(R.string.double_pungs),
                combinationImage = R.drawable.double_pungs
            ),
            Combination(
                combinationPoints = 2,
                combinationName = context.getString(R.string.two_concealed_pungs),
                combinationImage = R.drawable.two_concealed_pungs
            ),
            Combination(
                combinationPoints = 2,
                combinationName = context.getString(R.string.concealed_kong),
                combinationImage = R.drawable.concealed_kong
            ),
            Combination(
                combinationPoints = 2,
                combinationName = context.getString(R.string.all_simples),
                combinationImage = R.drawable.all_simples
            ),
            Combination(
                combinationPoints = 4,
                combinationName = context.getString(R.string.outside_hand),
                combinationImage = R.drawable.outside_hand
            ),
            Combination(
                combinationPoints = 4,
                combinationName = context.getString(R.string.fully_concealed_hand),
                combinationDescription = context.getString(R.string.description_fully_concealed_hand)
            ),
            Combination(
                combinationPoints = 4,
                combinationName = context.getString(R.string.two_melded_kongs),
                combinationImage = R.drawable.two_melded_kongs
            ),
            Combination(
                combinationPoints = 4,
                combinationName = context.getString(R.string.last_tile),
                combinationDescription = context.getString(R.string.description_last_tile)
            ),
            Combination(
                combinationPoints = 6,
                combinationName = context.getString(R.string.all_pungs),
                combinationImage = R.drawable.all_pungs
            ),
            Combination(
                combinationPoints = 6,
                combinationName = context.getString(R.string.half_flush),
                combinationImage = R.drawable.half_flush
            ),
            Combination(
                combinationPoints = 6,
                combinationName = context.getString(R.string.mixed_shifted_chows),
                combinationImage = R.drawable.mixed_shifted_chows
            ),
            Combination(
                combinationPoints = 6,
                combinationName = context.getString(R.string.all_types),
                combinationImage = R.drawable.all_types
            ),
            Combination(
                combinationPoints = 6,
                combinationName = context.getString(R.string.melded_hand),
                combinationDescription = context.getString(R.string.description_melded_hand)
            ),
            Combination(
                combinationPoints = 6,
                combinationName = context.getString(R.string.two_dragon_pungs),
                combinationImage = R.drawable.two_dragon_pungs
            ),
            Combination(
                combinationPoints = 8,
                combinationName = context.getString(R.string.mixed_straight),
                combinationImage = R.drawable.mixed_straight
            ),
            Combination(
                combinationPoints = 8,
                combinationName = context.getString(R.string.reversible_tiles),
                combinationImage = R.drawable.reversible_tiles
            ),
            Combination(
                combinationPoints = 8,
                combinationName = context.getString(R.string.mixed_triple_chow),
                combinationImage = R.drawable.mixed_triple_chow
            ),
            Combination(
                combinationPoints = 8,
                combinationName = context.getString(R.string.mixed_shifted_pungs),
                combinationImage = R.drawable.mixed_shifted_pungs
            ),
            Combination(
                combinationPoints = 8,
                combinationName = context.getString(R.string.chicken_hand),
                combinationImage = R.drawable.chicken_hand
            ),
            Combination(
                combinationPoints = 8,
                combinationName = context.getString(R.string.last_tile_draw),
                combinationDescription = context.getString(R.string.description_last_tile_draw)
            ),
            Combination(
                combinationPoints = 8,
                combinationName = context.getString(R.string.last_tile_claim),
                combinationDescription = context.getString(R.string.description_last_tile_claim)
            ),
            Combination(
                combinationPoints = 8,
                combinationName = context.getString(R.string.out_with_replacement_tile),
                combinationDescription = context.getString(R.string.description_out_with_replacement_tile)
            ),
            Combination(
                combinationPoints = 8,
                combinationName = context.getString(R.string.robbing_the_kong),
                combinationDescription = context.getString(R.string.description_robbing_the_kong)
            ),
            Combination(
                combinationPoints = 8,
                combinationName = context.getString(R.string.two_concealed_kongs),
                combinationImage = R.drawable.two_concealed_kongs
            ),
            Combination(
                combinationPoints = 12,
                combinationName = context.getString(R.string.lesser_honors_and_knitted_tiles),
                combinationImage = R.drawable.lesser_honors_and_knitted_tiles
            ),
            Combination(
                combinationPoints = 12,
                combinationName = context.getString(R.string.knitted_straight),
                combinationImage = R.drawable.knitted_straight
            ),
            Combination(
                combinationPoints = 12,
                combinationName = context.getString(R.string.upper_four),
                combinationImage = R.drawable.upper_four
            ),
            Combination(
                combinationPoints = 12,
                combinationName = context.getString(R.string.lower_four),
                combinationImage = R.drawable.lower_four
            ),
            Combination(
                combinationPoints = 12,
                combinationName = context.getString(R.string.big_three_winds),
                combinationImage = R.drawable.big_three_winds
            ),
            Combination(
                combinationPoints = 16,
                combinationName = context.getString(R.string.pure_straight),
                combinationImage = R.drawable.pure_straight
            ),
            Combination(
                combinationPoints = 16,
                combinationName = context.getString(R.string.three_suited_terminal_chows),
                combinationImage = R.drawable.three_suited_terminal_chows
            ),
            Combination(
                combinationPoints = 16,
                combinationName = context.getString(R.string.pure_shifted_chows),
                combinationImage = R.drawable.pure_shifted_chows
            ),
            Combination(
                combinationPoints = 16,
                combinationName = context.getString(R.string.all_fives),
                combinationImage = R.drawable.all_fives
            ),
            Combination(
                combinationPoints = 16,
                combinationName = context.getString(R.string.triple_pungs),
                combinationImage = R.drawable.triple_pungs
            ),
            Combination(
                combinationPoints = 16,
                combinationName = context.getString(R.string.three_concealed_pungs),
                combinationImage = R.drawable.three_concealed_pungs
            ),
            Combination(
                combinationPoints = 24,
                combinationName = context.getString(R.string.seven_pairs),
                combinationImage = R.drawable.seven_pairs
            ),
            Combination(
                combinationPoints = 24,
                combinationName = context.getString(R.string.greater_honors_and_knitted_tiles),
                combinationImage = R.drawable.greater_honors_and_knitted_tiles
            ),
            Combination(
                combinationPoints = 24,
                combinationName = context.getString(R.string.all_even_pungs),
                combinationImage = R.drawable.all_even_pungs
            ),
            Combination(
                combinationPoints = 24,
                combinationName = context.getString(R.string.full_flush),
                combinationImage = R.drawable.full_flush
            ),
            Combination(
                combinationPoints = 24,
                combinationName = context.getString(R.string.pure_triple_chow),
                combinationImage = R.drawable.pure_triple_chow
            ),
            Combination(
                combinationPoints = 24,
                combinationName = context.getString(R.string.pure_shifted_pungs),
                combinationImage = R.drawable.pure_shifted_pungs
            ),
            Combination(
                combinationPoints = 24,
                combinationName = context.getString(R.string.upper_tiles),
                combinationImage = R.drawable.upper_tiles
            ),
            Combination(
                combinationPoints = 24,
                combinationName = context.getString(R.string.medium_tiles),
                combinationImage = R.drawable.medium_tiles
            ),
            Combination(
                combinationPoints = 24,
                combinationName = context.getString(R.string.lower_tiles),
                combinationImage = R.drawable.lower_tiles
            ),
            Combination(
                combinationPoints = 32,
                combinationName = context.getString(R.string.four_pure_shifted_chows),
                combinationImage = R.drawable.four_pure_shifted_chows
            ),
            Combination(
                combinationPoints = 32,
                combinationName = context.getString(R.string.three_kongs),
                combinationImage = R.drawable.three_kongs
            ),
            Combination(
                combinationPoints = 32,
                combinationName = context.getString(R.string.all_terminals_and_honors),
                combinationImage = R.drawable.all_terminals_and_honors
            ),
            Combination(
                combinationPoints = 48,
                combinationName = context.getString(R.string.quadruple_chow),
                combinationImage = R.drawable.quadruple_chow
            ),
            Combination(
                combinationPoints = 48,
                combinationName = context.getString(R.string.four_pure_shifted_pungs),
                combinationImage = R.drawable.four_pure_shifted_pungs
            ),
            Combination(
                combinationPoints = 64,
                combinationName = context.getString(R.string.all_terminals),
                combinationImage = R.drawable.all_terminals
            ),
            Combination(
                combinationPoints = 64,
                combinationName = context.getString(R.string.all_honors),
                combinationImage = R.drawable.all_honors
            ),
            Combination(
                combinationPoints = 64,
                combinationName = context.getString(R.string.little_four_winds),
                combinationImage = R.drawable.little_four_winds
            ),
            Combination(
                combinationPoints = 64,
                combinationName = context.getString(R.string.little_three_dragons),
                combinationImage = R.drawable.little_three_dragons
            ),
            Combination(
                combinationPoints = 64,
                combinationName = context.getString(R.string.four_concealed_pungs),
                combinationImage = R.drawable.four_concealed_pungs
            ),
            Combination(
                combinationPoints = 64,
                combinationName = context.getString(R.string.pure_terminal_chows),
                combinationImage = R.drawable.pure_terminal_chows
            ),
            Combination(
                combinationPoints = 88,
                combinationName = context.getString(R.string.thirteen_orphans),
                combinationImage = R.drawable.thirteen_orphans
            ),
            Combination(
                combinationPoints = 88,
                combinationName = context.getString(R.string.big_three_dragons),
                combinationImage = R.drawable.big_three_dragons
            ),
            Combination(
                combinationPoints = 88,
                combinationName = context.getString(R.string.big_four_winds),
                combinationImage = R.drawable.big_four_winds
            ),
            Combination(
                combinationPoints = 88,
                combinationName = context.getString(R.string.all_green),
                combinationImage = R.drawable.all_green
            ),
            Combination(
                combinationPoints = 88,
                combinationName = context.getString(R.string.nine_gates),
                combinationImage = R.drawable.nine_gates
            ),
            Combination(
                combinationPoints = 88,
                combinationName = context.getString(R.string.four_kongs),
                combinationImage = R.drawable.four_kongs
            ),
            Combination(
                combinationPoints = 88,
                combinationName = context.getString(R.string.seven_shifted_pairs),
                combinationImage = R.drawable.seven_shifted_pairs
            )
        )

    private val combinationsFilter: MutableStateFlow<String> = MutableStateFlow("")

    val combinationsState: SharedFlow<List<Combination>> =
        combine(flowOf(combinations), combinationsFilter) { combinations, filter ->
            filterCombinations(combinations, filter)
        }.shareIn(viewModelScope, SharingStarted.Lazily, replay = 1)

    private fun filterCombinations(
        combinations: Array<Combination>,
        filter: String
    ) = combinations.filter { combination ->
            combination.combinationName.contains(filter, ignoreCase = true) ||
                combination.combinationDescription?.contains(filter, ignoreCase = true) == true ||
                combination.combinationPoints.toString().contains(filter, ignoreCase = true)
    }

    fun searchCombination(filter: String) {
        combinationsFilter.tryEmit(filter)
    }
}
