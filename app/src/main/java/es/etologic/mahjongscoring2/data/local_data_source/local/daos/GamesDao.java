package es.etologic.mahjongscoring2.data.local_data_source.local.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.sqlite.SQLiteConstraintException;

import java.util.List;

import es.etologic.mahjongscoring2.domain.entities.Game;

@Dao
public interface GamesDao {

    @Insert(onConflict = OnConflictStrategy.FAIL)
    long insertOne(Game game) throws SQLiteConstraintException; //TODO: probar con tests. ¿Hace falta la exception o devolvería un 0?

    @Insert(onConflict = OnConflictStrategy.FAIL)
    long[] bulkInsert(List<Game> game) throws SQLiteConstraintException; //TODO: probar con tests. ¿Hace falta la exception o devolvería un 0?

    @Query("SELECT * FROM Games WHERE gameId = :gameId")
    LiveData<Game> getOne(long gameId);

    @Query("SELECT * FROM Games")
    LiveData<List<Game>> getAll();

    @Update
    int updateOne(Game game);

    @Query("DELETE FROM Games WHERE gameId = :gameId")
    int deleteOne(long gameId);

    @Query("DELETE FROM Games")
    int deleteAll();
}
