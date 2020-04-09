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
package com.etologic.mahjongscoring2.data_source.repositories

import android.content.Context
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.R.drawable
import com.etologic.mahjongscoring2.R.string
import com.etologic.mahjongscoring2.business.model.entities.Combination
import com.etologic.mahjongscoring2.data_source.local_data_source.local.daos.CombinationsDao
import io.reactivex.Single
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CombinationsRepository
@Inject constructor(private val context: Context) {
    
    @Inject
    lateinit var combinationsDao: CombinationsDao
    
    internal fun getAll(): Single<List<Combination>> {
        return combinationsDao.getAll()
            .flatMap {
                if (it.isEmpty()) {
                    combinationsDao.bulkInsert(getHardcodedCombinations())
                    getAll()
                } else
                    Single.just(it)
            }
    }
    
    internal fun getFiltered(filter: String): Single<List<Combination>> {
        return combinationsDao.getFiltered(String.format("%%%s%%", filter))
    }
    
    private fun getHardcodedCombinations(): List<Combination> {
        val combinations = ArrayList<Combination>()
        combinations.add(
            Combination(
                1,
                0,
                context.getString(string.pure_double_chow),
                drawable.pure_double_chow
            )
        )
        combinations.add(
            Combination(
                1,
                0,
                context.getString(string.mixed_double_chow),
                drawable.mixed_double_chow
            )
        )
        combinations.add(
            Combination(
                1,
                0,
                context.getString(string.short_straight),
                drawable.short_straight
            )
        )
        combinations.add(
            Combination(
                1,
                0,
                context.getString(string.two_terminal_chows),
                drawable.two_terminal_chows
            )
        )
        combinations.add(
            Combination(
                1,
                0,
                context.getString(string.pung_of_terminals_or_honors),
                drawable.pung_of_terminal_or_honors
            )
        )
        combinations.add(
            Combination(
                1,
                0,
                context.getString(string.melded_kong),
                drawable.melded_kong
            )
        )
        combinations.add(
            Combination(
                1,
                0,
                context.getString(string.one_voided_suit),
                drawable.one_voided_suit
            )
        )
        combinations.add(
            Combination(
                1,
                0,
                context.getString(string.no_honors),
                drawable.no_honors
            )
        )
        combinations.add(
            Combination(
                1,
                0,
                context.getString(string.edge_wait),
                context.getString(string.description_edge_wait)
            )
        )
        combinations.add(
            Combination(
                1,
                0,
                context.getString(string.closed_wait),
                context.getString(string.description_closed_wait)
            )
        )
        combinations.add(
            Combination(
                1,
                0,
                context.getString(string.single_waiting),
                context.getString(string.description_single_waiting)
            )
        )
        combinations.add(
            Combination(
                1,
                0,
                context.getString(string.self_drawn),
                context.getString(string.description_self_drawn)
            )
        )
        combinations.add(
            Combination(
                1,
                0,
                context.getString(string.flower_tiles),
                drawable.flower_tiles
            )
        )
        combinations.add(
            Combination(
                2,
                0,
                context.getString(string.dragon_pung),
                drawable.dragon_pung
            )
        )
        combinations.add(
            Combination(
                2,
                0,
                context.getString(string.prevalent_wind),
                drawable.prevalent_wind
            )
        )
        combinations.add(
            Combination(
                2,
                0,
                context.getString(string.seat_wind),
                drawable.seat_wind
            )
        )
        combinations.add(
            Combination(
                2,
                0,
                context.getString(string.concealed_hand),
                context.getString(string.description_concealed_hand)
            )
        )
        combinations.add(
            Combination(
                2,
                0,
                context.getString(string.all_chows),
                drawable.all_chows
            )
        )
        combinations.add(
            Combination(
                2,
                0,
                context.getString(string.tile_hog),
                drawable.tile_hog
            )
        )
        combinations.add(
            Combination(
                2,
                0,
                context.getString(string.double_pungs),
                drawable.double_pungs
            )
        )
        combinations.add(
            Combination(
                2,
                0,
                context.getString(string.two_concealed_pungs),
                drawable.two_concealed_pungs
            )
        )
        combinations.add(
            Combination(
                2,
                0,
                context.getString(string.concealed_kong),
                drawable.concealed_kong
            )
        )
        combinations.add(
            Combination(
                2,
                0,
                context.getString(string.all_simples),
                drawable.all_simples
            )
        )
        combinations.add(
            Combination(
                4,
                0,
                context.getString(string.outside_hand),
                drawable.outside_hand
            )
        )
        combinations.add(
            Combination(
                4,
                0,
                context.getString(string.fully_concealed_hand),
                context.getString(string.description_fully_concealed_hand)
            )
        )
        combinations.add(
            Combination(
                4,
                0,
                context.getString(string.two_melded_kongs),
                drawable.two_melded_kongs
            )
        )
        combinations.add(
            Combination(
                4,
                0,
                context.getString(string.last_tile),
                context.getString(string.description_last_tile)
            )
        )
        combinations.add(
            Combination(
                6,
                0,
                context.getString(string.all_pungs),
                drawable.all_pungs
            )
        )
        combinations.add(
            Combination(
                6,
                0,
                context.getString(string.half_flush),
                drawable.half_flush
            )
        )
        combinations.add(
            Combination(
                6,
                0,
                context.getString(string.mixed_shifted_chows),
                drawable.mixed_shifted_chows
            )
        )
        combinations.add(
            Combination(
                6,
                0,
                context.getString(string.all_types),
                drawable.all_types
            )
        )
        combinations.add(
            Combination(
                6,
                0,
                context.getString(string.melded_hand),
                context.getString(string.description_melded_hand)
            )
        )
        combinations.add(
            Combination(
                6,
                0,
                context.getString(string.two_dragon_pungs),
                drawable.two_dragon_pungs
            )
        )
        combinations.add(
            Combination(
                8,
                0,
                context.getString(string.mixed_straight),
                drawable.mixed_straight
            )
        )
        combinations.add(
            Combination(
                8,
                0,
                context.getString(string.reversible_tiles),
                drawable.reversible_tiles
            )
        )
        combinations.add(
            Combination(
                8,
                0,
                context.getString(string.mixed_triple_chow),
                drawable.mixed_triple_chow
            )
        )
        combinations.add(
            Combination(
                8,
                0,
                context.getString(string.mixed_shifted_pungs),
                drawable.mixed_shifted_pungs
            )
        )
        combinations.add(
            Combination(
                8,
                0,
                context.getString(string.chicken_hand),
                drawable.chicken_hand
            )
        )
        combinations.add(
            Combination(
                8,
                0,
                context.getString(string.last_tile_draw),
                context.getString(string.description_last_tile_draw)
            )
        )
        combinations.add(
            Combination(
                8,
                0,
                context.getString(string.last_tile_claim),
                context.getString(string.description_last_tile_claim)
            )
        )
        combinations.add(
            Combination(
                8,
                0,
                context.getString(string.out_with_replacement_tile),
                context.getString(string.description_out_with_replacement_tile)
            )
        )
        combinations.add(
            Combination(
                8,
                0,
                context.getString(string.robbing_the_kong),
                context.getString(string.description_robbing_the_kong)
            )
        )
        combinations.add(
            Combination(
                8,
                0,
                context.getString(string.two_concealed_kongs),
                drawable.two_concealed_kongs
            )
        )
        combinations.add(
            Combination(
                12,
                0,
                context.getString(string.lesser_honors_and_knitted_tiles),
                drawable.lesser_honors_and_knitted_tiles
            )
        )
        combinations.add(
            Combination(
                12,
                0,
                context.getString(string.knitted_straight),
                drawable.knitted_straight
            )
        )
        combinations.add(
            Combination(
                12,
                0,
                context.getString(string.upper_four),
                drawable.upper_four
            )
        )
        combinations.add(
            Combination(
                12,
                0,
                context.getString(string.lower_four),
                drawable.lower_four
            )
        )
        combinations.add(
            Combination(
                12,
                0,
                context.getString(string.big_three_winds),
                drawable.big_three_winds
            )
        )
        combinations.add(
            Combination(
                16,
                0,
                context.getString(string.pure_straight),
                drawable.pure_straight
            )
        )
        combinations.add(
            Combination(
                16,
                0,
                context.getString(string.three_suited_terminal_chows),
                drawable.three_suited_terminal_chows
            )
        )
        combinations.add(
            Combination(
                16,
                0,
                context.getString(string.pure_shifted_chows),
                drawable.pure_shifted_chows
            )
        )
        combinations.add(
            Combination(
                16,
                0,
                context.getString(string.all_fives),
                drawable.all_fives
            )
        )
        combinations.add(
            Combination(
                16,
                0,
                context.getString(string.triple_pungs),
                drawable.triple_pungs
            )
        )
        combinations.add(
            Combination(
                16,
                0,
                context.getString(string.three_concealed_pungs),
                drawable.three_concealed_pungs
            )
        )
        combinations.add(
            Combination(
                24,
                0,
                context.getString(string.seven_pairs),
                drawable.seven_pairs
            )
        )
        combinations.add(
            Combination(
                24,
                0,
                context.getString(string.greater_honors_and_knitted_tiles),
                drawable.greater_honors_and_knitted_tiles
            )
        )
        combinations.add(
            Combination(
                24,
                0,
                context.getString(string.all_even_pungs),
                drawable.all_even_pungs
            )
        )
        combinations.add(
            Combination(
                24,
                0,
                context.getString(string.full_flush),
                drawable.full_flush
            )
        )
        combinations.add(
            Combination(
                24,
                0,
                context.getString(string.pure_triple_chow),
                drawable.pure_triple_chow
            )
        )
        combinations.add(
            Combination(
                24,
                0,
                context.getString(string.pure_shifted_pungs),
                drawable.pure_shifted_pungs
            )
        )
        combinations.add(
            Combination(
                24,
                0,
                context.getString(string.upper_tiles),
                drawable.upper_tiles
            )
        )
        combinations.add(
            Combination(
                24,
                0,
                context.getString(string.medium_tiles),
                drawable.medium_tiles
            )
        )
        combinations.add(
            Combination(
                24,
                0,
                context.getString(string.lower_tiles),
                drawable.lower_tiles
            )
        )
        combinations.add(
            Combination(
                32,
                0,
                context.getString(string.four_pure_shifted_chows),
                drawable.four_pure_shifted_chows
            )
        )
        combinations.add(
            Combination(
                32,
                0,
                context.getString(string.three_kongs),
                drawable.three_kongs
            )
        )
        combinations.add(
            Combination(
                32,
                0,
                context.getString(string.all_terminals_and_honors),
                drawable.all_terminals_and_honors
            )
        )
        combinations.add(
            Combination(
                48,
                0,
                context.getString(string.quadruple_chow),
                drawable.quadruple_chow
            )
        )
        combinations.add(
            Combination(
                48,
                0,
                context.getString(string.four_pure_shifted_pungs),
                drawable.four_pure_shifted_pungs
            )
        )
        combinations.add(
            Combination(
                64,
                0,
                context.getString(string.all_terminals),
                drawable.all_terminals
            )
        )
        combinations.add(
            Combination(
                64,
                0,
                context.getString(string.all_honors),
                drawable.all_honors
            )
        )
        combinations.add(
            Combination(
                64,
                0,
                context.getString(string.little_four_winds),
                drawable.little_four_winds
            )
        )
        combinations.add(
            Combination(
                64,
                0,
                context.getString(string.little_three_dragons),
                drawable.little_three_dragons
            )
        )
        combinations.add(
            Combination(
                64,
                0,
                context.getString(string.four_concealed_pungs),
                drawable.four_concealed_pungs
            )
        )
        combinations.add(
            Combination(
                64,
                0,
                context.getString(string.pure_terminal_chows),
                drawable.pure_terminal_chows
            )
        )
        combinations.add(
            Combination(
                88,
                0,
                context.getString(string.thirteen_orphans),
                drawable.thirteen_orphans
            )
        )
        combinations.add(
            Combination(
                88,
                0,
                context.getString(string.big_three_dragons),
                drawable.big_three_dragons
            )
        )
        combinations.add(
            Combination(
                88,
                0,
                context.getString(string.big_four_winds),
                drawable.big_four_winds
            )
        )
        combinations.add(
            Combination(
                88,
                0,
                context.getString(string.all_green),
                drawable.all_green
            )
        )
        combinations.add(
            Combination(
                88,
                0,
                context.getString(string.nine_gates),
                drawable.nine_gates
            )
        )
        combinations.add(
            Combination(
                88,
                0,
                context.getString(string.four_kongs),
                drawable.four_kongs
            )
        )
        combinations.add(
            Combination(
                88,
                0,
                context.getString(string.seven_shifted_pairs),
                drawable.seven_shifted_pairs
            )
        )
        return combinations
    }
}
