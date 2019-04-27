package es.etologic.mahjongscoring2.domain.use_cases;

import javax.inject.Inject;

import es.etologic.mahjongscoring2.data.repositories.GamesRepository;
import es.etologic.mahjongscoring2.domain.model.Game;
import io.reactivex.Single;

public class UpdateGameUseCase {

    private final GamesRepository gamesRepository;

    @Inject
    public UpdateGameUseCase(GamesRepository gamesRepository) {
        this.gamesRepository = gamesRepository;
    }

    public Single<Boolean> updateGame(Game game) {
        return gamesRepository.updateOne(game)
                .flatMap(wasUpdated -> {
                    if (wasUpdated) {
                        return Single.just(Boolean.TRUE);
                    } else {
                        return Single.error(new Exception());
                    }
                });
    }
}
