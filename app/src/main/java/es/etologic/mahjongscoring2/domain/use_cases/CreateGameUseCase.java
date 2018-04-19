package es.etologic.mahjongscoring2.domain.use_cases;

import java.util.List;

import es.etologic.mahjongscoring2.data.repositories.GamesRepository;
import es.etologic.mahjongscoring2.domain.entities.Game;
import es.etologic.mahjongscoring2.domain.operation_objects.BaseError;
import es.etologic.mahjongscoring2.domain.operation_objects.OperationResult;

public class CreateGameUseCase {

    private GamesRepository gamesRepository;

    public CreateGameUseCase(GamesRepository gamesRepository) { this.gamesRepository = gamesRepository; }

    public OperationResult<Game, BaseError> execute(List<String> playersNames) {
        long newGameId = gamesRepository.insertOne(Game.getNewGame(playersNames));
        Game newGame = gamesRepository.getOne(newGameId);
        return newGameId > 0 ? new OperationResult<>(newGame) : new OperationResult<>(BaseError.getInsertionError());
    }
}
