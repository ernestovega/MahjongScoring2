package es.etologic.mahjongscoring2.domain.use_cases;

import java.util.List;

import javax.inject.Inject;

import es.etologic.mahjongscoring2.data.repositories.GamesRepository;
import es.etologic.mahjongscoring2.domain.entities.Game;
import io.reactivex.Single;

public class GetAllGamesUseCase {

    private GamesRepository gamesRepository;

    @Inject
    public GetAllGamesUseCase(GamesRepository gamesRepository) { this.gamesRepository = gamesRepository; }

    public Single<List<Game>> getAll() { return gamesRepository.getAll(); }
}
