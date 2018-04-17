package es.etologic.mahjongscoring2.data.repositories;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;

import java.util.Calendar;
import java.util.List;

import es.etologic.mahjongscoring2.data.local_data_source.local.AppDatabase;
import es.etologic.mahjongscoring2.data.local_data_source.local.daos.GamesDao;
import es.etologic.mahjongscoring2.domain.entities.Game;

public class GamesRepository extends BaseRepository<Game> { /*TODO Hacer pruebas forzando datos (tests unitarios!)*/

    private final GamesDao gamesDao;

    public GamesRepository(Context context) {
        super(context);
        gamesDao = database.getGamesDao();
    }

    @Override
    public long insertOne(Game game) {
        try {
            return gamesDao.insertOne(game);
        } catch(SQLiteConstraintException exception) {
            return 0;
        }
    }

    public long insertOne(List<String> players) {
        Game game = new Game(AppDatabase.NOT_SET_ID, players.get(0), players.get(1), players.get(2), players.get(3), Calendar.getInstance().getTime());
        return gamesDao.insertOne(game);
    }

    @Override
    public Game getOne(long gameId) {
        return gamesDao.getOne(gameId);
    }

    @Override
    public List<Game> getAll() {
        return gamesDao.getAll();
    }

    @Override
    public boolean updateOne(Game game) {
        return gamesDao.updateOne(game) == 1;
    }

    @Override
    public boolean deleteOne(long gameId) {
        return gamesDao.deleteOne(gameId) == 1;
    }

    @Override
    public long deleteAll() {
        return gamesDao.deleteAll();
    }
}
