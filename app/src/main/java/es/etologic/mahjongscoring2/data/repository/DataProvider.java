package es.etologic.mahjongscoring2.data.repository;

import java.util.List;

import es.etologic.mahjongscoring2.data.repository.local.ILocalDataSource;
import es.etologic.mahjongscoring2.domain.DataSource;
import es.etologic.mahjongscoring2.domain.entities.Combination;
import es.etologic.mahjongscoring2.domain.entities.Game;
import es.etologic.mahjongscoring2.domain.entities.Player;

public class DataProvider implements DataSource {

    private static DataProvider INSTANCE = null;
    private final ILocalDataSource localDataSource;

    private DataProvider(ILocalDataSource localDataSource) {
        this.localDataSource = localDataSource;
    }

    public static synchronized DataProvider getInstance(ILocalDataSource localDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new DataProvider(localDataSource);
        }
        return INSTANCE;
    }

    //region GAMES

    @Override
    public Long createGame(Game gameToInsert) {
        return localDataSource.insertGame(gameToInsert);
    }

    @Override
    public Game getGame(long gameId) {
        return localDataSource.getGame(gameId);
    }

    @Override
    public List<Game> getAllGames() {
        return localDataSource.getAllGames();
    }

    //endregion

    //region PLAYERS

    @Override
    public boolean createPlayer(Player player) {
        return localDataSource.insertPlayer(player);
    }

    @Override
    public Player getPlayer(String playerName) {
        return localDataSource.getPlayer(playerName);
    }

    @Override
    public List<Player> getAllPlayers() {
        return localDataSource.getAllPlayers();
    }

    //endregion

    //region COMBINATIONS

    @Override
    public List<Combination> getAllCombinations() {
        return localDataSource.getAllCombinations();
    }

    //endregion
}
