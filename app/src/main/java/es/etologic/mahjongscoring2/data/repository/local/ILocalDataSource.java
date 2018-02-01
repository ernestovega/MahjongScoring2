package es.etologic.mahjongscoring2.data.repository.local;

import java.util.List;

import es.etologic.mahjongscoring2.domain.entities.Combination;
import es.etologic.mahjongscoring2.domain.entities.Game;
import es.etologic.mahjongscoring2.domain.entities.Player;

public interface ILocalDataSource {

    //region DB
    void clearDatabase();
    //endregion

    //region GAMES
    long insertGame(Game game);
    Game getGame(long gameId);
    List<Game> getAllGames();
    boolean deleteGame(long gameId);
    //endregion

    //region PLAYERS
    boolean insertPlayer(Player player);
    Player getPlayer(String playerName);
    List<Player> getAllPlayers();
    //endregion

    //region COMBINATIONS
    List<Combination> getAllCombinations();
    //endregion
}
