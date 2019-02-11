package es.etologic.mahjongscoring2.domain.use_cases;

import java.util.List;

import javax.inject.Inject;

import es.etologic.mahjongscoring2.data.repositories.GamesRepository;
import es.etologic.mahjongscoring2.domain.model.Game;
import io.reactivex.Single;

public class CreateGameUseCase {

    private GamesRepository gamesRepository;

    @Inject
    public CreateGameUseCase(GamesRepository gamesRepository) { this.gamesRepository = gamesRepository; }

    public Single<Long> createGame(List<String> playersNames) {
        return gamesRepository.insertOne(new Game(playersNames));
    }
}
