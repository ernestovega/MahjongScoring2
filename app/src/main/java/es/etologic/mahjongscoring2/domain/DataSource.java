package es.etologic.mahjongscoring2.domain;

import java.util.List;

import es.etologic.mahjongscoring2.domain.entities.Game;
import es.etologic.mahjongscoring2.domain.entities.Player;

public interface DataSource {

    List<Game> getAllGames();

    List<Player> getAllPlayers();
}

