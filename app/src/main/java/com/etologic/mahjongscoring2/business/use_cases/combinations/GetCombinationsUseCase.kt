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
package com.etologic.mahjongscoring2.business.use_cases.combinations

import com.etologic.mahjongscoring2.business.model.entities.Combination
import com.etologic.mahjongscoring2.data_source.repositories.CombinationsRepository
import io.reactivex.Single
import javax.inject.Inject

class GetCombinationsUseCase @Inject
constructor(private val combinationsRepository: CombinationsRepository) {
    
    internal fun getAll(): Single<List<Combination>> = combinationsRepository.getAll()
    
    internal fun getSome(filter: String): Single<List<Combination>> = combinationsRepository.getFiltered(filter)
}
