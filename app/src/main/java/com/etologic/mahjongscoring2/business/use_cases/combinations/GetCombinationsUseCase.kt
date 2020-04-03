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
