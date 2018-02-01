package es.etologic.mahjongscoring2.data.repository.local.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.database.sqlite.SQLiteConstraintException;

import java.util.List;

import es.etologic.mahjongscoring2.domain.entities.Combination;
import es.etologic.mahjongscoring2.domain.entities.Game;
import es.etologic.mahjongscoring2.domain.entities.Player;

@Dao
public interface CombinationsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void bulkInsert(List<Combination> combinations);

    @Query("SELECT * FROM Combinations")
    List<Combination> getAll();
}
