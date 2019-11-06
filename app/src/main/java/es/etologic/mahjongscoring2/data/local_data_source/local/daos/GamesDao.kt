package es.etologic.mahjongscoring2.data.local_data_source.local.daos

import android.database.sqlite.SQLiteConstraintException
import androidx.room.*
import es.etologic.mahjongscoring2.domain.model.Game
import io.reactivex.Single

@Dao
interface GamesDao {
    
    @Query("SELECT * FROM Games ORDER BY startDate DESC")
    fun getAll(): Single<List<Game>>
    
    @Insert(onConflict = OnConflictStrategy.ABORT)
    @Throws(SQLiteConstraintException::class)
    fun insertOne(game: Game): Single<Long>  //TODO: probar con tests. ¿Hace falta la exception o devolvería un 0?
    
    @Query("SELECT * FROM Games WHERE gameId = :gameId")
    fun getOne(gameId: Long): Single<Game>
    
    @Update
    fun updateOne(game: Game): Single<Int>
    
    @Query("DELETE FROM Games WHERE gameId = :gameId")
    fun deleteOne(gameId: Long): Single<Int>
    
    @Query("DELETE FROM Games")
    fun deleteAll(): Single<Int>
}
