package es.etologic.mahjongscoring2.domain;

import java.util.List;

import es.etologic.mahjongscoring2.domain.entities.Game;

public interface DataSource {

    List<Game> getAllGames();
}

