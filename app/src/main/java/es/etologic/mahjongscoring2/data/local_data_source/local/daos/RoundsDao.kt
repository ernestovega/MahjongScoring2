package es.etologic.mahjongscoring2.data.local_data_source.local.daos

import android.database.sqlite.SQLiteConstraintException
import androidx.room.*
import es.etologic.mahjongscoring2.domain.model.Round

@Dao
interface RoundsDao {
    
    @Query("SELECT * FROM Rounds")
    fun getAll(): List<Round>
    
    @Insert(onConflict = OnConflictStrategy.ABORT)
    @Throws(SQLiteConstraintException::class)
    fun insertOne(round: Round): Long  //TODO: probar con tests. ¿Hace falta la exception o devolvería un 0?
    
    @Query("SELECT * FROM Rounds WHERE gameId = :gameId AND roundId = :roundId")
    fun getOne(gameId: Long, roundId: Long): Round
    
    @Query("SELECT * FROM Rounds WHERE gameId = :gameId")
    fun getAllByGame(gameId: Long): List<Round>
    
    @Update
    fun updateOne(round: Round): Int
    
    @Query("DELETE FROM Rounds WHERE gameId = :gameId AND roundId = :roundId")
    fun deleteOne(gameId: Long, roundId: Long): Int
    
    @Query("DELETE FROM Rounds WHERE gameId = :gameId")
    fun deleteByGame(gameId: Long): Int
    
    @Query("DELETE FROM Rounds")
    fun deleteAll(): Int
}
