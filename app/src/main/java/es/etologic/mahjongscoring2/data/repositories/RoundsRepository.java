package es.etologic.mahjongscoring2.data.repositories;

import android.content.Context;

import java.util.List;

import javax.inject.Inject;

import es.etologic.mahjongscoring2.data.local_data_source.local.daos.RoundsDao;
import es.etologic.mahjongscoring2.domain.entities.Round;
import io.reactivex.Single;

public class RoundsRepository extends BaseRepository { /*TODO ¿Qué pasa con posibles errores? Hacer pruebas forzando datos (tests unitarios!)*/

    private final RoundsDao roundsDao;

    @Inject
    public RoundsRepository(Context context) {
        super(context);
        roundsDao = database.getRoundsDao();
    }

    public Single<Long> insertOne(Round round) {
        return Single.fromCallable(() -> roundsDao.insertOne(round));
    }

    public Single<Round> getOne(long gameId, long roundId) { return Single.fromCallable(() -> roundsDao.getOne(gameId, roundId)); }

    public Single<List<Round>> getAll() { return Single.fromCallable(roundsDao::getAll); }

    public Single<List<Round>> getRoundsByGame(long gameId) { return Single.fromCallable(() -> roundsDao.getAllByGame(gameId)); }

    public Single<Boolean> updateOne(Round round) { return Single.fromCallable(() -> roundsDao.updateOne(round) == 1); }

    public Single<Boolean> deleteOne(long gameId, long roundId) { return Single.fromCallable(() -> roundsDao.deleteOne(gameId, roundId) == 1); }

    public Single<Boolean> deleteAll() { return Single.fromCallable(() -> roundsDao.deleteAll() >= 0); } //TODO: ¿Cómo saber si ha habido algún error?
}
