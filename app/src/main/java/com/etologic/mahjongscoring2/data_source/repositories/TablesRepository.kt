package com.etologic.mahjongscoring2.data_source.repositories

import com.etologic.mahjongscoring2.business.model.entities.Table
import com.etologic.mahjongscoring2.data_source.local_data_source.local.daos.TableDao
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TablesRepository
@Inject constructor() {
    
    @Inject lateinit var tableDao: TableDao
    
    internal fun getTable(gameId: Long): Single<Table> =
        tableDao.getTablesOrderedByDateDesc(gameId)
    
    internal fun getAllTables(): Single<List<Table>> =
        tableDao.getAllTables()
}
