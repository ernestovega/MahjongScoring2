package es.etologic.mahjongscoring2.data.local_data_source.local.daos

import androidx.room.*
import es.etologic.mahjongscoring2.domain.model.Player

@Dao
interface PlayersDao {
    
    @Query("SELECT * FROM Players")
    fun getAll(): List<Player>
    
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(player: Player): Long
    
    @Query("SELECT * FROM Players WHERE playerId = :playerId")
    fun getOne(playerId: Long): Player
    
    @Query("SELECT * FROM Players WHERE playerName = :playerName")
    fun getOne(playerName: String): Player
    
    @Update
    fun updateOne(player: Player): Int
    
    @Query("DELETE FROM Players WHERE playerId = :playerId")
    fun deleteOne(playerId: Long): Int
    
    @Query("DELETE FROM Players")
    fun deleteAll(): Int
}
