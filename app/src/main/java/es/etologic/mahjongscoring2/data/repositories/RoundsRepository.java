package es.etologic.mahjongscoring2.data.repositories;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;

import java.util.List;

import es.etologic.mahjongscoring2.data.local_data_source.local.daos.RoundsDao;
import es.etologic.mahjongscoring2.domain.entities.Round;

public class RoundsRepository extends BaseRepository<Round> { /*TODO ¿Qué pasa con posibles errores? Hacer pruebas forzando datos (tests unitarios!)*/

    private final RoundsDao roundsDao;

    public RoundsRepository(Context context) {
        super(context);
        roundsDao = database.getRoundsDao();
    }

    @Override
    public long insertOne(Round round) {
        return roundsDao.insertOne(round);
    }

    public LiveData<Round> getOne(long gameId, long roundId) { return roundsDao.getOne(gameId, roundId); }

    @Override
    public LiveData<List<Round>> getAll() { return roundsDao.getAll(); }

    public LiveData<List<Round>> getRoundsByGame(long gameId) { return roundsDao.getAllByGame(gameId); }

    @Override
    public boolean updateOne(Round round) { return roundsDao.updateOne(round) == 1; }

    public boolean deleteOne(long gameId, long roundId) { return roundsDao.deleteOne(gameId, roundId) == 1; }

    @Override
    public boolean deleteAll() { return roundsDao.deleteAll() >= 0; } //TODO: ¿Cómo saber si ha habido algún error?
}
