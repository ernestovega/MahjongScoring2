package es.etologic.mahjongscoring2.data.local_data_source.local.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import java.util.List;

import es.etologic.mahjongscoring2.domain.model.GameWithRounds;

@Dao
public interface GameWithRoundsDao {

    @Transaction
    @Query("SELECT * from Games")
    List<GameWithRounds> getAllGamesWithRounds();

    @Transaction
    @Query("SELECT * from Games WHERE gameId = :gameId ORDER BY creationDate")
    GameWithRounds getGameWithRoundsOrderedByDateDesc(long gameId);
}
