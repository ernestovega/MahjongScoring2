package es.etologic.mahjongscoring2.data.repositories;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;

import java.util.Calendar;
import java.util.List;

import es.etologic.mahjongscoring2.data.local_data_source.local.AppDatabase;
import es.etologic.mahjongscoring2.data.local_data_source.local.daos.GamesDao;
import es.etologic.mahjongscoring2.domain.entities.Game;

public class GamesRepository extends BaseRepository<Game> { /*TODO: ¿Qué pasa con posibles errores? Hacer pruebas forzando datos (tests unitarios!)*/

    private final GamesDao gamesDao;

    public GamesRepository(Context context) {
        super(context);
        gamesDao = database.getGamesDao();
    }

    @Override
    public long insertOne(Game game) { return gamesDao.insertOne(game); }

    public LiveData<Game> getOne(long gameId) { return gamesDao.getOne(gameId); }

    @Override
    public LiveData<List<Game>> getAll() { return gamesDao.getAll(); }

    @Override
    public boolean updateOne(Game game) { return gamesDao.updateOne(game) == 1; }

    public boolean deleteOne(long gameId) { return gamesDao.deleteOne(gameId) == 1; }

    @Override
    public boolean deleteAll() { return gamesDao.deleteAll() >= 0; } //TODO: ¿Cómo saber si ha habido algún error?
}
