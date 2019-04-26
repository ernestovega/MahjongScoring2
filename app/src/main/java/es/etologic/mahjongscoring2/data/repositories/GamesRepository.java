package es.etologic.mahjongscoring2.data.repositories;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import es.etologic.mahjongscoring2.data.local_data_source.local.daos.GameWithRoundsDao;
import es.etologic.mahjongscoring2.data.local_data_source.local.daos.GamesDao;
import es.etologic.mahjongscoring2.data.local_data_source.local.daos.RoundsDao;
import es.etologic.mahjongscoring2.domain.model.Game;
import es.etologic.mahjongscoring2.domain.model.GameWithRounds;
import io.reactivex.Single;

public class GamesRepository extends BaseRepository { /*TODO: ¿Qué pasa con posibles errores? Hacer pruebas forzando datos (tests unitarios!)*/

    private final GamesDao gamesDao;
    private final RoundsDao roundsDao;
    private final GameWithRoundsDao gameWithRoundsDao;

    @Inject
    public GamesRepository(Context context) {
        super(context);
        gamesDao = database.getGamesDao();
        roundsDao = database.getRoundsDao();
        gameWithRoundsDao = database.getGameWithRoundsDao();
    }

    public Single<Long> insertOne(Game game) {
        return Single.fromCallable(() -> {
            long l = gamesDao.insertOne(game);
            return l;
        });
    }

    public Single<Boolean> updateOne(Game game) {
        return Single.fromCallable(() -> gamesDao.updateOne(game) == 1);
    }

    public Single<Boolean> deleteOne(long gameId) {
        return Single.fromCallable(() -> {
            roundsDao.deleteByGame(gameId);
            return gamesDao.deleteOne(gameId) == 1;
        });
    }

    public Single<GameWithRounds> getOneWithRounds(long gameId) {
        return Single.fromCallable(() -> {
            GameWithRounds gameWithRoundsOrderedByDateDesc = gameWithRoundsDao.getGameWithRoundsOrderedByDateDesc(gameId);
            return gameWithRoundsOrderedByDateDesc;
        });
    }

    public Single<List<GameWithRounds>> getAllWithRounds() {
        return Single.fromCallable(gameWithRoundsDao::getAllGamesWithRounds);
    }
}
