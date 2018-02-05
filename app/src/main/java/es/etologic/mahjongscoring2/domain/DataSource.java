package es.etologic.mahjongscoring2.domain;

import java.util.List;

import es.etologic.mahjongscoring2.domain.entities.Combination;
import es.etologic.mahjongscoring2.domain.entities.Game;
import es.etologic.mahjongscoring2.domain.entities.Player;

public interface DataSource {

    //GAMES
    Long createGame(Game gameToInsert);
    Game getGame(long gameId);
    List<Game> getAllGames();
    boolean deleteGame(long gameId);

    //PLAYERS
    boolean createPlayer(Player player);
    Player getPlayer(String playerName);
    List<Player> getAllPlayers();

    //COMBINATIONS
    List<Combination> getAllCombinations();
    List<Combination> getFilteredCombinations(String text);
}

