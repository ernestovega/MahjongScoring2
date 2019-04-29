package es.etologic.mahjongscoring2.domain.use_cases;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import es.etologic.mahjongscoring2.data.repositories.GamesRepository;
import es.etologic.mahjongscoring2.domain.model.Game;
import es.etologic.mahjongscoring2.domain.model.GameWithRounds;
import io.reactivex.Single;

public class CreateGameUseCase {

    private GamesRepository gamesRepository;

    @Inject
    public CreateGameUseCase(GamesRepository gamesRepository) { this.gamesRepository = gamesRepository; }

    public Single<GameWithRounds> createGame() {
        List<String> playersNames = new ArrayList<>(4);
        playersNames.add("Player 1");
        playersNames.add("Player 2");
        playersNames.add("Player 3");
        playersNames.add("Player 4");
        Game newGame = new Game(playersNames);
        return gamesRepository.insertOne(newGame)
                .flatMap(gamesRepository::getOneWithRounds);
    }
}
