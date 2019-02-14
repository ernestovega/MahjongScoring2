package es.etologic.mahjongscoring2.domain.use_cases;

import java.util.List;

import javax.inject.Inject;

import es.etologic.mahjongscoring2.data.repositories.GamesRepository;
import es.etologic.mahjongscoring2.data.repositories.RoundsRepository;
import es.etologic.mahjongscoring2.domain.model.GameWithRounds;
import es.etologic.mahjongscoring2.domain.model.Round;
import io.reactivex.Single;

public class UpdateRoundsUseCase {

    private final RoundsRepository roundsRepository;
    private final GamesRepository gamesRepository;

    @Inject
    public UpdateRoundsUseCase(RoundsRepository roundsRepository, GamesRepository gamesRepository) {
        this.roundsRepository = roundsRepository;
        this.gamesRepository = gamesRepository;
    }

    public Single<List<Round>> getGameRounds(long gameId) {
        return roundsRepository.getRoundsByGame(gameId);
    }

    public Single<GameWithRounds> addRound(Round round) {
        return roundsRepository.insertOne(round)
                .flatMap(aLong -> gamesRepository.getOneWithRounds(round.getGameId()));
    }
}
