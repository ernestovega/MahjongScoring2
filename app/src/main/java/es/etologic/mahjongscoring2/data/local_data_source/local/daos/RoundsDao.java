package es.etologic.mahjongscoring2.data.local_data_source.local.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.sqlite.SQLiteConstraintException;

import java.util.List;

import es.etologic.mahjongscoring2.domain.entities.Round;

@Dao
public interface RoundsDao {

    @Insert(onConflict = OnConflictStrategy.FAIL)
    long insertOne(Round round) throws SQLiteConstraintException; //TODO: probar con tests. ¿Hace falta la exception o devolvería un 0?

    @Insert(onConflict = OnConflictStrategy.FAIL)
    long[] bulkInsert(List<Round> round) throws SQLiteConstraintException; //TODO: probar con tests. ¿Hace falta la exception o devolvería un 0?

    @Query("SELECT * FROM Rounds WHERE gameId = :gameId AND roundId = :roundId")
    LiveData<Round> getOne(long gameId, long roundId);

    @Query("SELECT * FROM Rounds WHERE gameId = :gameId")
    LiveData<List<Round>> getAllByGame(long gameId);

    @Query("SELECT * FROM Rounds")
    LiveData<List<Round>> getAll();

    @Update
    int updateOne(Round round);

    @Query("DELETE FROM Rounds WHERE gameId = :gameId AND roundId = :roundId")
    int deleteOne(long gameId, long roundId);

    @Query("DELETE FROM Rounds")
    int deleteAll();
}
