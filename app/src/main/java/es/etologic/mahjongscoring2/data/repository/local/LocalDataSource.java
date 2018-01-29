package es.etologic.mahjongscoring2.data.repository.local;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;

import java.util.List;

import es.etologic.mahjongscoring2.data.repository.local.daos.GamesDao;
import es.etologic.mahjongscoring2.data.repository.local.daos.PlayersDao;
import es.etologic.mahjongscoring2.domain.entities.Game;
import es.etologic.mahjongscoring2.domain.entities.Player;

public class LocalDataSource implements ILocalDataSource {

    //region Fields

    private final Context context;
    private final PlayersDao playersDao;
    private final GamesDao gamesDao;

    //endregion

    //region Constructor

    public LocalDataSource(Context context) {
        this.context = context;
        AppDatabase database = AppDatabase.getInstance(this.context.getApplicationContext());
        playersDao = database.getPlayersDao();
        gamesDao = database.getGamesDao();
    }

    //endregion

    //region IRemoteDataSource implementation

    @Override
    public void clearDatabase() {
    }

    //GAMES

    @Override
    public List<Game> getAllGames() {
        return gamesDao.getAll();
    }

    @Override
    public boolean saveGame(Game game) {
        try {
            gamesDao.insertOne(game);
        } catch(SQLiteConstraintException exception) {
            return false;
        }
        return true;
    }

    //PLAYERS
    @Override
    public List<Player> getAllPlayers() {
        return playersDao.getAll();
    }

    @Override
    public boolean savePlayers(List<Player> players) {
        try {
            playersDao.bulkInsert(players);
        } catch(SQLiteConstraintException exception) {
            return false;
        }
        return true;
    }

    //endregion
}