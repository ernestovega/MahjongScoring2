package es.etologic.mahjongscoring2.data.repository.local.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import es.etologic.mahjongscoring2.domain.entities.Player;

@Dao
public interface PlayersDao {

    @Insert(onConflict = OnConflictStrategy.ROLLBACK)
    void insert(Player player);

    @Query("SELECT * FROM Players WHERE playerName = :playerName")
    Player getOne(String playerName);

    @Query("SELECT * FROM Players")
    List<Player> getAll();
}
