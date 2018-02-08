package es.etologic.mahjongscoring2.data.repository.local;

import java.util.List;

import es.etologic.mahjongscoring2.domain.entities.Combination;
import es.etologic.mahjongscoring2.domain.entities.Game;
import es.etologic.mahjongscoring2.domain.entities.Player;
import es.etologic.mahjongscoring2.domain.entities.Round;

public interface ILocalDataSource {

    //PLAYERS
    List<Player> getAllPlayers();
    Player getPlayer(String playerName);
    boolean insertPlayer(Player player);

    //GAMES
    List<Game> getAllGames();
    Game getGame(long gameId);
    long insertGame(Game game);
    boolean deleteGame(long gameId);

    //ROUNDS
    boolean updateRound(Round round);

    //COMBINATIONS
    List<Combination> getAllCombinations();
    List<Combination> getFilteredCombinations(String filter);
}
