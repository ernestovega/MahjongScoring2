package com.etologic.mahjongscoring2.business.use_cases.current_game

import com.etologic.mahjongscoring2.business.model.entities.Table
import com.etologic.mahjongscoring2.data_source.repositories.CurrentTableRepository
import com.etologic.mahjongscoring2.data_source.repositories.TablesRepository
import io.reactivex.Single
import javax.inject.Inject

class SetCurrentGameUseCase
@Inject constructor(
    private val currentTableRepository: CurrentTableRepository,
    private val tablesRepository: TablesRepository
) {
    
    internal fun setCurrentGame(gameId: Long): Single<Table> =
        tablesRepository.getTable(gameId)
            .flatMap { currentTableRepository.set(it) }
}
