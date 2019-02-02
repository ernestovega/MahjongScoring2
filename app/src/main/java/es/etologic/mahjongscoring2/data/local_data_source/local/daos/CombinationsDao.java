package es.etologic.mahjongscoring2.data.local_data_source.local.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.sqlite.SQLiteConstraintException;

import java.util.List;

import es.etologic.mahjongscoring2.domain.entities.Combination;
import es.etologic.mahjongscoring2.domain.entities.Player;
import io.reactivex.Single;

@Dao
public interface CombinationsDao {

    @Insert
    void bulkInsert(List<Combination> combinations);

    @Query("SELECT * FROM Combinations " +
                   "WHERE combinationName LIKE :filter " +
                   "OR combinationDescription LIKE :filter " +
                   "ORDER BY combinationPoints, combinationName ASC")
    Single<List<Combination>> getFiltered(String filter);

    @Query("SELECT * FROM Combinations")
    Single<List<Combination>> getAll();
}
