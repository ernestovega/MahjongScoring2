package es.etologic.mahjongscoring2.domain.use_cases;

import java.util.List;

import javax.inject.Inject;

import es.etologic.mahjongscoring2.data.repositories.RoundsRepository;
import es.etologic.mahjongscoring2.domain.model.Round;
import io.reactivex.Single;

public class GetRoundsUseCase {

    private final RoundsRepository roundsRepository;

    @Inject
    public GetRoundsUseCase(RoundsRepository roundsRepository) {
        this.roundsRepository = roundsRepository;
    }

    public Single<List<Round>> getGameRounds(long gameId) {
        return roundsRepository.getRoundsByGame(gameId);
    }
}
