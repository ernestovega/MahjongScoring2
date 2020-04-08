package com.etologic.mahjongscoring2.business.use_cases.current_game

import com.etologic.mahjongscoring2.business.model.entities.Table
import com.etologic.mahjongscoring2.data_source.repositories.CurrentTableRepository
import io.reactivex.Single
import javax.inject.Inject

class GetCurrentGameUseCase @Inject
constructor(
    private val currentTableRepository: CurrentTableRepository
) {
    
    internal fun getCurrentGameWithRounds(): Single<Table> =
        currentTableRepository.get()
}
