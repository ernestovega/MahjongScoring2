package com.etologic.mahjongscoring2.data_source.local_data_source.local.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.etologic.mahjongscoring2.business.model.entities.GameWithRounds
import io.reactivex.Single

@Dao
interface GameWithRoundsDao {
    
    @Transaction
    @Query("SELECT * from Games")
    fun getAllGamesWithRounds(): Single<List<GameWithRounds>>
    
    @Transaction
    @Query("SELECT * from Games WHERE gameId = :gameId ORDER BY startDate")
    fun getGameWithRoundsOrderedByDateDesc(gameId: Long): Single<GameWithRounds>
}
