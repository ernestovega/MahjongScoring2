package es.etologic.mahjongscoring2.data.local_data_source.local.daos

import androidx.room.*
import es.etologic.mahjongscoring2.domain.model.Player
import io.reactivex.Single

@Dao
interface PlayersDao {
    
    @Query("SELECT * FROM Players")
    fun getAll(): Single<List<Player>>
    
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(player: Player): Single<Long>
    
    @Query("SELECT * FROM Players WHERE playerId = :playerId")
    fun getOne(playerId: Long): Single<Player>
    
    @Query("SELECT * FROM Players WHERE playerName = :playerName")
    fun getOne(playerName: String): Single<Player>
    
    @Update
    fun updateOne(player: Player): Single<Int>
    
    @Query("DELETE FROM Players WHERE playerId = :playerId")
    fun deleteOne(playerId: Long): Single<Int>
    
    @Query("DELETE FROM Players")
    fun deleteAll(): Single<Int>
}
