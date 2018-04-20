package es.etologic.mahjongscoring2.domain.use_cases;

import es.etologic.mahjongscoring2.data.repositories.GamesRepository;
import es.etologic.mahjongscoring2.domain.operation_objects.BaseError;
import es.etologic.mahjongscoring2.domain.operation_objects.OperationResult;

public class DeleteGameUseCase {

    private final GamesRepository gamesRepository;

    public DeleteGameUseCase(GamesRepository gamesRepository) {
        this.gamesRepository = gamesRepository;
    }

    public OperationResult<Boolean, BaseError> execute(long gameId) {
        return /*gamesRepository.deleteOne(gameId) ? new OperationResult<>(true) :*/ new OperationResult<>(BaseError.getDeletionError());
    }
}
