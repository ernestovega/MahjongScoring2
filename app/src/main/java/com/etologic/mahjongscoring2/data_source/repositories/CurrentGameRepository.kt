package com.etologic.mahjongscoring2.data_source.repositories

import com.etologic.mahjongscoring2.business.model.entities.GameWithRounds
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentGameRepository
@Inject constructor(private val memoryDataSource: CurrentGameMemoryDataSource) {
    
    internal fun get(): Single<GameWithRounds> =
        memoryDataSource.get()
    
    internal fun set(gameWithRounds: GameWithRounds): Single<GameWithRounds> =
        memoryDataSource.set(gameWithRounds)
    
    internal fun invalidate(): Single<Boolean> =
        memoryDataSource.invalidate()
}