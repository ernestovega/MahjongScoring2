package com.etologic.mahjongscoring2.data_source.repositories

import com.etologic.mahjongscoring2.business.model.entities.Table
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

class CurrentTableRepository
@Inject constructor(private val memoryDataSource: CurrentTableMemoryDataSource) {
    
    internal fun get(): Single<Table> =
        memoryDataSource.get()
    
    internal fun set(table: Table): Single<Table> =
        memoryDataSource.set(table)
    
    internal fun invalidate(): Single<Boolean> =
        memoryDataSource.invalidate()
}