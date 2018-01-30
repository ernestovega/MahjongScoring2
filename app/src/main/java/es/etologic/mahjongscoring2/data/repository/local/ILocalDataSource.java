package es.etologic.mahjongscoring2.data.repository.local;

import android.database.sqlite.SQLiteConstraintException;

import java.util.List;

import es.etologic.mahjongscoring2.domain.entities.Combination;
import es.etologic.mahjongscoring2.domain.entities.Game;
import es.etologic.mahjongscoring2.domain.entities.Player;

public interface ILocalDataSource {

    //DATABASE
    void clearDatabase();

    //GAMES
    List<Game> getAllGames();
    boolean saveGame(Game game) throws SQLiteConstraintException;

    //PLAYERS
    List<Player> getAllPlayers();
    boolean savePlayers(List<Player> players);

    //COMBINATIONS
    List<Combination> getAllCombinations();
}
