package es.etologic.mahjongscoring2.domain.use_cases

import es.etologic.mahjongscoring2.data.repositories.CombinationsRepository
import es.etologic.mahjongscoring2.domain.model.Combination
import io.reactivex.Single
import javax.inject.Inject

class GetCombinationsUseCase @Inject
constructor(private val combinationsRepository: CombinationsRepository) {
    
    val all: Single<List<Combination>>
        get() = combinationsRepository.getAll()
    
    fun getSome(filter: String): Single<List<Combination>> {
        return combinationsRepository.getFiltered(filter)
    }
}
