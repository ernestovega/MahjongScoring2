package com.etologic.mahjongscoring2.data_source.local_data_source.local.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.etologic.mahjongscoring2.business.model.entities.Table
import io.reactivex.Single

@Dao
interface TableDao {
    
    @Transaction
    @Query("SELECT * from Games ORDER BY startDate DESC")
    fun getAllTables(): Single<List<Table>>
    
    @Transaction
    @Query("SELECT * from Games WHERE gameId = :gameId ORDER BY startDate")
    fun getTablesOrderedByDateDesc(gameId: Long): Single<Table>
}
