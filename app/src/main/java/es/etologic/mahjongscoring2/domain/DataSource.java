package es.etologic.mahjongscoring2.domain;

import java.util.List;

import es.etologic.mahjongscoring2.domain.entities.Combination;
import es.etologic.mahjongscoring2.domain.entities.Game;
import es.etologic.mahjongscoring2.domain.entities.Player;
import es.etologic.mahjongscoring2.domain.entities.Round;

public interface DataSource {

    //PLAYERS
    List<Player> getAllPlayers();
    Player getPlayer(String playerName);
    boolean createPlayer(Player player);

    //GAMES
    List<Game> getAllGames();
    Game getGame(long gameId);
    Long createGame(Game game);
    boolean deleteGame(long gameId);

    //ROUNDS
    boolean updateRound(Round round);

    //COMBINATIONS
    List<Combination> getAllCombinations();
    List<Combination> getFilteredCombinations(String text);

}

