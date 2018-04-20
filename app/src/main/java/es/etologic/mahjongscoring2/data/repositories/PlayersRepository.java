package es.etologic.mahjongscoring2.data.repositories;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import java.util.List;

import es.etologic.mahjongscoring2.data.local_data_source.local.AppDatabase;
import es.etologic.mahjongscoring2.data.local_data_source.local.daos.PlayersDao;
import es.etologic.mahjongscoring2.domain.entities.Player;

public class PlayersRepository extends BaseRepository<Player> { /*TODO ¿Qué pasa con posibles errores? Hacer pruebas forzando datos (tests unitarios!)*/

    private final PlayersDao playersDao;

    public PlayersRepository(Context context) {
        super(context);
        playersDao = database.getPlayersDao();
    }

    @Override
    public long insertOne(Player player) {
        return playersDao.insert(player);
    }

    public Player getOne(long playerId) { return playersDao.getOne(playerId); }

    public LiveData<Player> getOne(String playerName) { return playersDao.getOne(playerName); }

    @Override
    public LiveData<List<Player>> getAll() { return playersDao.getAll(); }

    @Override
    public boolean updateOne(Player player) { return playersDao.updateOne(player) == 1; }

    public boolean deleteOne(long gameId) { return playersDao.deleteOne(gameId) == 1; }

    @Override
    public boolean deleteAll() { return playersDao.deleteAll() >= 0; } //TODO: ¿Cómo saber si ha habido algún error?
}
