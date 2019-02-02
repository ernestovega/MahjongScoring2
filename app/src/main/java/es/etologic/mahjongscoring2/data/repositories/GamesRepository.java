package es.etologic.mahjongscoring2.data.repositories;

import android.content.Context;

import java.util.List;

import javax.inject.Inject;

import es.etologic.mahjongscoring2.data.local_data_source.local.daos.GamesDao;
import es.etologic.mahjongscoring2.domain.entities.Game;
import io.reactivex.Single;

public class GamesRepository extends BaseRepository { /*TODO: ¿Qué pasa con posibles errores? Hacer pruebas forzando datos (tests unitarios!)*/

    private final GamesDao gamesDao;

    @Inject
    public GamesRepository(Context context) {
        super(context);
        gamesDao = database.getGamesDao();
    }

    public Single<Long> insertOne(Game game) {
        return Single.fromCallable(() -> gamesDao.insertOne(game));
    }

    public Single<Game> getOne(long gameId) {
        return Single.fromCallable(() -> gamesDao.getOne(gameId));
    }

    public Single<List<Game>> getAll() {
        return Single.fromCallable(gamesDao::getAll);
    }

    public Single<Boolean> updateOne(Game game) {
        return Single.fromCallable(() -> gamesDao.updateOne(game) == 1);
    }

    public Single<Boolean> deleteOne(long gameId) {
        return Single.fromCallable(() -> gamesDao.deleteOne(gameId) == 1);
    }

    public Single<Boolean> deleteAll() {
        return Single.fromCallable(() -> gamesDao.deleteAll() >= 0);
    } //TODO: ¿Cómo saber si ha habido algún error?
}
