package es.etologic.mahjongscoring2.data.repository;

import java.util.List;

import es.etologic.mahjongscoring2.data.repository.local.ILocalDataSource;
import es.etologic.mahjongscoring2.domain.DataSource;
import es.etologic.mahjongscoring2.domain.entities.Combination;
import es.etologic.mahjongscoring2.domain.entities.Game;
import es.etologic.mahjongscoring2.domain.entities.Player;
import es.etologic.mahjongscoring2.domain.entities.Round;

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

    //region PLAYERS

    @Override
    public List<Player> getAllPlayers() {
        return localDataSource.getAllPlayers();
    }

    @Override
    public Player getPlayer(String playerName) {
        return localDataSource.getPlayer(playerName);
    }

    @Override
    public boolean createPlayer(Player player) {
        return localDataSource.insertPlayer(player);
    }

    //endregion

    //region GAMES

    @Override
    public List<Game> getAllGames() {
        return localDataSource.getAllGames();
    }

    @Override
    public Game getGame(long gameId) {
        return localDataSource.getGame(gameId);
    }

    @Override
    public Long createGame(Game game) {
        return localDataSource.insertGame(game);
    }

    @Override
    public boolean deleteGame(long gameId) {
        return localDataSource.deleteGame(gameId);
    }

    //endregion

    //region ROUNDS

    @Override
    public boolean updateRound(Round round) {
        return localDataSource.updateRound(round);
    }

    //endregion

    //region COMBINATIONS

    @Override
    public List<Combination> getAllCombinations() {
        return localDataSource.getAllCombinations();
    }

    @Override
    public List<Combination> getFilteredCombinations(String filter) {
        return localDataSource.getFilteredCombinations(filter);
    }

    //endregion
}
