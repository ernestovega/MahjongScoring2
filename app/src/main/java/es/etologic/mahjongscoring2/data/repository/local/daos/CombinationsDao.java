package es.etologic.mahjongscoring2.data.repository.local.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import es.etologic.mahjongscoring2.domain.entities.Combination;

@Dao
public interface CombinationsDao {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void bulkInsert(List<Combination> combinations);

    @Query("SELECT * FROM Combinations ORDER BY combinationPoints, combinationName")
    List<Combination> getAllOrderedByPoints();

    @Query("SELECT * FROM Combinations " +
            "WHERE combinationName LIKE :filter " +
            "OR combinationDescription LIKE :filter " +
            "ORDER BY combinationPoints")
    List<Combination> getFilteredCombinations(String filter);
}
