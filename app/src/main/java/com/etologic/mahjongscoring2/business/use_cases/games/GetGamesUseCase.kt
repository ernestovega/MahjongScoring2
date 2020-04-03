package com.etologic.mahjongscoring2.business.use_cases.games

import com.etologic.mahjongscoring2.business.model.entities.Table
import com.etologic.mahjongscoring2.data_source.repositories.TablesRepository
import io.reactivex.Single
import javax.inject.Inject

class GetGamesUseCase @Inject
constructor(private val tablesRepository: TablesRepository) {
    
    fun getAllWithRounds(): Single<List<Table>> = tablesRepository.getAllTables()
}
