package es.etologic.mahjongscoring2.data.repository.local;

import java.util.List;

import es.etologic.mahjongscoring2.domain.entities.Combination;
import es.etologic.mahjongscoring2.domain.entities.Game;
import es.etologic.mahjongscoring2.domain.entities.Player;

public interface ILocalDataSource {

    //GAMES
    List<Game> getAllGames();
    Game getGame(long gameId);
    boolean updateGame(Game game);
    long insertGame(Game game);
    boolean deleteGame(long gameId);

    //PLAYERS
    List<Player> getAllPlayers();
    Player getPlayer(String playerName);
    boolean insertPlayer(Player player);

    //COMBINATIONS
    List<Combination> getAllCombinations();
    List<Combination> getFilteredCombinations(String filter);
}
