package es.etologic.mahjongscoring2.domain.use_cases;

import java.util.List;

import javax.inject.Inject;

import es.etologic.mahjongscoring2.data.repositories.GamesRepository;
import es.etologic.mahjongscoring2.domain.model.Game;
import es.etologic.mahjongscoring2.domain.model.GameWithRounds;
import io.reactivex.Completable;
import io.reactivex.Single;

public class GetGamesWithRoundsUseCase {

    private GamesRepository gamesRepository;

    @Inject
    public GetGamesWithRoundsUseCase(GamesRepository gamesRepository) { this.gamesRepository = gamesRepository; }

    public Single<List<GameWithRounds>> getAllWithRounds() { return gamesRepository.getAllWithRounds(); }

    public Single<GameWithRounds> getOneWithRounds(long gameId) {
        return gamesRepository.getOneWithRounds(gameId);
    }
}
