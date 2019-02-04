package es.etologic.mahjongscoring2.domain.use_cases;

import javax.inject.Inject;

import es.etologic.mahjongscoring2.data.repositories.GamesRepository;
import es.etologic.mahjongscoring2.domain.model.Game;
import io.reactivex.Single;

public class GetGameUseCase {

    private final GamesRepository gamesRepository;

    @Inject
    public GetGameUseCase(GamesRepository gamesRepository) {
        this.gamesRepository = gamesRepository;
    }

    public Single<Game> getGame(long gameId) {
        return gamesRepository.getOne(gameId);
    }
}
