package es.etologic.mahjongscoring2.data.repository.local;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;

import java.util.List;

import es.etologic.mahjongscoring2.data.repository.local.daos.CombinationsDao;
import es.etologic.mahjongscoring2.data.repository.local.daos.GamesDao;
import es.etologic.mahjongscoring2.data.repository.local.daos.PlayersDao;
import es.etologic.mahjongscoring2.domain.entities.Combination;
import es.etologic.mahjongscoring2.domain.entities.Game;
import es.etologic.mahjongscoring2.domain.entities.Player;

public class LocalDataSource implements ILocalDataSource {

    //region Fields

    private final Context context;
    private final PlayersDao playersDao;
    private final GamesDao gamesDao;
    private final CombinationsDao combinationsDao;

    //endregion

    //region Constructor

    public LocalDataSource(Context context) {
        this.context = context;
        AppDatabase database = AppDatabase.getInstance(this.context.getApplicationContext());
        playersDao = database.getPlayersDao();
        gamesDao = database.getGamesDao();
        combinationsDao = database.getCombinationsDao();
    }

    //endregion

    //region DB

    @Override
    public void clearDatabase() {}

    //endregion

    //region GAMES

    @Override
    public long insertGame(Game game) {
        try {
            return gamesDao.insertOne(game);
        } catch(SQLiteConstraintException exception) {
            return 0;
            //TODO Hacer pruebas forzando datos (tests unitarios!)
        }
    }

    @Override
    public Game getGame(long gameId) {
        return gamesDao.getOne(gameId);
        //TODO Hacer pruebas forzando datos (tests unitarios!)
    }

    @Override
    public List<Game> getAllGames() {
        return gamesDao.getAll();
    }

    //endregion

    //region PLAYERS

    @Override
    public boolean insertPlayer(Player player) {
        try {
            playersDao.insert(player);
        } catch(SQLiteConstraintException exception) {
            return false;
            //TODO Hacer pruebas forzando datos (tests unitarios!)
        }
        return true;
    }

    @Override
    public Player getPlayer(String playerName) {
        return playersDao.getOne(playerName);
        //TODO Hacer pruebas forzando datos (tests unitarios!)
    }

    @Override
    public List<Player> getAllPlayers() {
        return playersDao.getAll();
    }

    //endregion

    //region COMBINATIONS

    @Override
    public List<Combination> getAllCombinations() {
        return combinationsDao.getAll();
    }

    //endregion
}