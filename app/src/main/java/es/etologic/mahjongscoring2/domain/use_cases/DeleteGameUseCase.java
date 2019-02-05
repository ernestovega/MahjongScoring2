package es.etologic.mahjongscoring2.domain.use_cases;

import javax.inject.Inject;

import es.etologic.mahjongscoring2.data.repositories.GamesRepository;
import io.reactivex.Single;

public class DeleteGameUseCase {

    private final GamesRepository gamesRepository;

    @Inject
    public DeleteGameUseCase(GamesRepository gamesRepository) {
        this.gamesRepository = gamesRepository;
    }

    public Single<Boolean> deleteGame(long gameId) {
        return gamesRepository.deleteOne(gameId);
    }
}
