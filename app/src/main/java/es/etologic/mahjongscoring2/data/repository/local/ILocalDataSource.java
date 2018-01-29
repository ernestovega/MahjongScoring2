package es.etologic.mahjongscoring2.data.repository.local;

import android.database.sqlite.SQLiteConstraintException;

import java.util.List;

import es.etologic.mahjongscoring2.domain.entities.Game;
import es.etologic.mahjongscoring2.domain.entities.Player;

public interface ILocalDataSource {

    void clearDataBase();

    List<Game> getAllGames();

    boolean savePlayers(List<Player> players);

    boolean saveGame(Game game) throws SQLiteConstraintException;
}
