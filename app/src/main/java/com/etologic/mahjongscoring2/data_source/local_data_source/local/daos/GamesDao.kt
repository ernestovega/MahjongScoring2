package com.etologic.mahjongscoring2.data_source.local_data_source.local.daos

import android.database.sqlite.SQLiteConstraintException
import androidx.room.*
import com.etologic.mahjongscoring2.business.model.entities.Game
import io.reactivex.Single

@Dao
interface GamesDao {
    
    @Insert(onConflict = OnConflictStrategy.ABORT)
    @Throws(SQLiteConstraintException::class)
    fun insertOne(game: Game): Single<Long>  //TODO: probar con tests. ¿Hace falta la exception o devolvería un 0?
    
    @Update
    fun updateOne(game: Game): Single<Int>
    
    @Query("DELETE FROM Games WHERE gameId = :gameId")
    fun deleteOne(gameId: Long): Single<Int>
}
