package es.etologic.mahjongscoring2.data.local_data_source.local.daos

import android.database.sqlite.SQLiteConstraintException
import androidx.room.*
import es.etologic.mahjongscoring2.domain.model.Game

@Dao
interface GamesDao {
    
    @Query("SELECT * FROM Games ORDER BY startDate DESC")
    fun getAll(): List<Game>
    
    @Insert(onConflict = OnConflictStrategy.ABORT)
    @Throws(SQLiteConstraintException::class)
    fun insertOne(game: Game): Long  //TODO: probar con tests. ¿Hace falta la exception o devolvería un 0?
    
    @Query("SELECT * FROM Games WHERE gameId = :gameId")
    fun getOne(gameId: Long): Game
    
    @Update
    fun updateOne(game: Game): Int
    
    @Query("DELETE FROM Games WHERE gameId = :gameId")
    fun deleteOne(gameId: Long): Int
    
    @Query("DELETE FROM Games")
    fun deleteAll(): Int
}
