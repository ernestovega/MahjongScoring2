package es.etologic.mahjongscoring2.data.repository.local.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.database.sqlite.SQLiteConstraintException;

import java.util.List;

import es.etologic.mahjongscoring2.domain.entities.Game;

@Dao
public interface GamesDao {

    @Insert (onConflict = OnConflictStrategy.ROLLBACK)
    void insertOne(Game game) throws SQLiteConstraintException;

    @Query("SELECT * FROM Games")
    List<Game> getAll();
}
