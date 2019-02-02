package es.etologic.mahjongscoring2.data.repositories;

import android.content.Context;

import java.util.List;

import javax.inject.Inject;

import es.etologic.mahjongscoring2.data.local_data_source.local.daos.PlayersDao;
import es.etologic.mahjongscoring2.domain.entities.Player;
import io.reactivex.Single;

public class PlayersRepository extends BaseRepository { /*TODO ¿Qué pasa con posibles errores? Hacer pruebas forzando datos (tests unitarios!)*/

    private final PlayersDao playersDao;

    @Inject
    public PlayersRepository(Context context) {
        super(context);
        playersDao = database.getPlayersDao();
    }

    public Single<Long> insertOne(Player player) {
        return Single.fromCallable(() -> playersDao.insert(player));
    }

    public Single<Player> getOne(long playerId) { return Single.fromCallable(() -> playersDao.getOne(playerId)); }

    public Single<Player> getOne(String playerName) { return Single.fromCallable(() -> playersDao.getOne(playerName)); }

    public Single<List<Player>> getAllPlayers() { return Single.fromCallable(playersDao::getAll); }

    public Single<Boolean> updateOne(Player player) { return Single.fromCallable(() -> playersDao.updateOne(player) == 1); }

    public Single<Boolean> deleteOne(long gameId) { return Single.fromCallable(() -> playersDao.deleteOne(gameId) == 1); }

    public Single<Boolean> deleteAll() { return Single.fromCallable(() -> playersDao.deleteAll() >= 0); } //TODO: ¿Cómo saber si ha habido algún error?
}
