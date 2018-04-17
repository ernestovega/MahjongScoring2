package es.etologic.mahjongscoring2.data.repositories;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;

import java.util.List;

import es.etologic.mahjongscoring2.data.local_data_source.local.daos.RoundsDao;
import es.etologic.mahjongscoring2.domain.entities.Round;

public class RoundsRepository extends BaseRepository<Round> { /*TODO Hacer pruebas forzando datos (tests unitarios!)*/

    private final RoundsDao roundsDao;

    public RoundsRepository(Context context) {
        super(context);
        roundsDao = database.getRoundsDao();
    }

    @Override
    public long insertOne(Round round) {
        try {
            return roundsDao.insertOne(round);
        } catch(SQLiteConstraintException exception) {
            return 0;
        }
    }

    @Override
    public Round getOne(long roundId) {
        return roundsDao.getOne(roundId);
    }

    @Override
    public List<Round> getAll() {
        return roundsDao.getAll();
    }

    @Override
    public boolean updateOne(Round round) {
        return roundsDao.updateOne(round) == 1;
    }

    @Override
    public boolean deleteOne(long roundId) {
        return roundsDao.deleteOne(roundId) == 1;
    }

    @Override
    public long deleteAll() {
        return roundsDao.deleteAll();
    }
}
