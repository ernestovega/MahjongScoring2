package com.etologic.mahjongscoring2.business.use_cases.current_game

import com.etologic.mahjongscoring2.business.model.entities.Table
import com.etologic.mahjongscoring2.data_source.repositories.CurrentGameRepository
import io.reactivex.Single
import javax.inject.Inject

class GetCurrentGameUseCase @Inject
constructor(
    private val currentGameRepository: CurrentGameRepository
) {
    
    internal fun getCurrentGameWithRounds(): Single<Table> = currentGameRepository.get()
}
