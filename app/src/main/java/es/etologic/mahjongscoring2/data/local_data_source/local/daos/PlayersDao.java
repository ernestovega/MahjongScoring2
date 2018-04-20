package es.etologic.mahjongscoring2.data.local_data_source.local.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import es.etologic.mahjongscoring2.domain.entities.Player;

@Dao
public interface PlayersDao {

    @Insert(onConflict = OnConflictStrategy.FAIL)
    long insert(Player player);

    @Query("SELECT * FROM Players WHERE playerId = :playerId")
    Player getOne(long playerId);

    @Query("SELECT * FROM Players WHERE playerName = :playerName")
    LiveData<Player> getOne(String playerName);

    @Query("SELECT * FROM Players")
    LiveData<List<Player>> getAll();

    @Update
    int updateOne(Player player);

    @Query("DELETE FROM Players WHERE playerId = :playerId")
    int deleteOne(long playerId);

    @Query("DELETE FROM Players")
    int deleteAll();
}
