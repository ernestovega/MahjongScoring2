package es.etologic.mahjongscoring2.domain.use_cases;

import java.util.List;

import javax.inject.Inject;

import es.etologic.mahjongscoring2.data.repositories.GamesRepository;
import es.etologic.mahjongscoring2.domain.model.Game;
import es.etologic.mahjongscoring2.domain.model.GameWithRounds;
import io.reactivex.Completable;
import io.reactivex.Single;

public class GetGamesUseCase {

    private GamesRepository gamesRepository;

    @Inject
    public GetGamesUseCase(GamesRepository gamesRepository) {
        this.gamesRepository = gamesRepository;
    }

    public Single<List<GameWithRounds>> getAllWithRounds() {
        return gamesRepository.getAllWithRounds();
    }

    public Single<GameWithRounds> getGame(long gameId) {
        return gamesRepository.getOneWithRounds(gameId);
    }
}
