package com.etologic.mahjongscoring2.data_source.local_data_source.local.daos

import android.database.sqlite.SQLiteConstraintException
import androidx.room.*
import com.etologic.mahjongscoring2.business.model.entities.Round
import io.reactivex.Single

@Dao
interface RoundsDao {
    
    @Query("SELECT * FROM Rounds")
    fun getAll(): Single<List<Round>>
    
    @Insert(onConflict = OnConflictStrategy.ABORT)
    @Throws(SQLiteConstraintException::class)
    fun insertOne(round: Round): Single<Long>  //TODO: probar con tests. ¿Hace falta la exception o devolvería un 0?
    
    @Query("SELECT * FROM Rounds WHERE gameId = :gameId AND roundId = :roundId")
    fun getOne(gameId: Long, roundId: Long): Single<Round>
    
    @Query("SELECT * FROM Rounds WHERE gameId = :gameId")
    fun getAllByGame(gameId: Long): Single<List<Round>>
    
    @Update
    fun updateOne(round: Round): Single<Int>
    
    @Query("DELETE FROM Rounds WHERE gameId = :gameId AND roundId = :roundId")
    fun deleteOne(gameId: Long, roundId: Long): Single<Int>
    
    @Query("DELETE FROM Rounds WHERE gameId = :gameId")
    fun deleteByGame(gameId: Long): Single<Int>
    
    @Query("DELETE FROM Rounds")
    fun deleteAll(): Single<Int>
}
