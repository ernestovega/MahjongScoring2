package es.etologic.mahjongscoring2.domain.use_cases;

import java.util.Calendar;
import java.util.List;

import es.etologic.mahjongscoring2.data.local_data_source.local.AppDatabase;
import es.etologic.mahjongscoring2.data.repositories.GamesRepository;
import es.etologic.mahjongscoring2.domain.entities.Game;
import es.etologic.mahjongscoring2.domain.operation_objects.BaseError;
import es.etologic.mahjongscoring2.domain.operation_objects.OperationResult;

public class CreateGameUseCase {

    private GamesRepository gamesRepository;

    public CreateGameUseCase(GamesRepository gamesRepository) { this.gamesRepository = gamesRepository; }

    public OperationResult<Long, BaseError> execute(List<String> playersNames) {
        long gameId = gamesRepository.insertOne(Game.getNewGame(playersNames));
        if (gameId > 0) return new OperationResult<>(gameId);
        else return new OperationResult<>(BaseError.getInsertionError());
    }
}
