package es.etologic.mahjongscoring2.data.repository.local.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Update;

import es.etologic.mahjongscoring2.domain.entities.Round;

@Dao
public interface RoundsDao {

    @Update
    int updateOne(Round round);
}
