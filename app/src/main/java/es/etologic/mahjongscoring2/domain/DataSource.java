package es.etologic.mahjongscoring2.domain;

import java.util.List;

import es.etologic.mahjongscoring2.domain.entities.Combination;
import es.etologic.mahjongscoring2.domain.entities.Game;
import es.etologic.mahjongscoring2.domain.entities.Player;

public interface DataSource {

    //GAMES
    List<Game> getAllGames();
    Game getGame(long gameId);
    Long createGame(Game game);
    boolean updateGame(Game game);
    boolean deleteGame(long gameId);

    //PLAYERS
    List<Player> getAllPlayers();
    Player getPlayer(String playerName);
    boolean createPlayer(Player player);

    //COMBINATIONS
    List<Combination> getAllCombinations();
    List<Combination> getFilteredCombinations(String text);

}

