package es.etologic.mahjongscoring2.data.local_data_source.local.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.sqlite.SQLiteConstraintException;

import java.util.List;

import es.etologic.mahjongscoring2.domain.entities.Combination;

@Dao
public interface CombinationsDao {

    @Insert(onConflict = OnConflictStrategy.FAIL)
    long insertOne(Combination combination) throws SQLiteConstraintException; //TODO: probar con tests. ¿Hace falta la exception o devolvería un 0?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void bulkInsert(List<Combination> combinations);

    @Query("SELECT * FROM Combinations WHERE combinationId = :combinationId")
    LiveData<Combination> getOne(long combinationId);

    @Query("SELECT * FROM Combinations " +
                   "WHERE combinationName LIKE :filter " +
                   "OR combinationDescription LIKE :filter " +
                   "ORDER BY combinationPoints")
    LiveData<List<Combination>> getFilteredCombinations(String filter);

    @Query("SELECT * FROM Combinations ORDER BY combinationPoints, combinationName ASC")
    LiveData<List<Combination>> getAllOrderedAscByPoints();

    @Update
    int updateOne(Combination combination);

    @Query("DELETE FROM Combinations WHERE combinationId = :combinationId")
    int deleteOne(long combinationId);

    @Query("DELETE FROM Combinations")
    int deleteAll();
}
