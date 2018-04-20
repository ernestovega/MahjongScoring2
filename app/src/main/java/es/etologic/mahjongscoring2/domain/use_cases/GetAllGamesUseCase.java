package es.etologic.mahjongscoring2.domain.use_cases;

import android.arch.lifecycle.LiveData;

import java.util.List;

import es.etologic.mahjongscoring2.data.repositories.GamesRepository;
import es.etologic.mahjongscoring2.domain.entities.Game;

public class GetAllGamesUseCase {

    private GamesRepository gamesRepository;

    public GetAllGamesUseCase(GamesRepository gamesRepository) { this.gamesRepository = gamesRepository; }

    public LiveData<List<Game>> execute() { return gamesRepository.getAll(); }
}
