package com.etologic.mahjongscoring2.data_source.repositories

import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentGameRepository
@Inject constructor(private val memoryDataSource: CurrentGameMemoryDataSource) {
    
    internal fun get(): Single<Long> =
        memoryDataSource.get()
    
    internal fun set(gameId: Long): Single<Boolean> =
        memoryDataSource.set(gameId)
    
    internal fun invalidate(): Single<Boolean> =
        memoryDataSource.invalidate()
}