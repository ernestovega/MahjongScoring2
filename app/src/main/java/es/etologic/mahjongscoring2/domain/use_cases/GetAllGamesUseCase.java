package es.etologic.mahjongscoring2.domain.use_cases;

import java.util.List;

import es.etologic.mahjongscoring2.data.repositories.GamesRepository;
import es.etologic.mahjongscoring2.domain.entities.Game;
import es.etologic.mahjongscoring2.domain.operation_objects.BaseError;
import es.etologic.mahjongscoring2.domain.operation_objects.OperationResult;

public class GetAllGamesUseCase {

        private GamesRepository gamesRepository;

        public GetAllGamesUseCase(GamesRepository gamesRepository) { this.gamesRepository = gamesRepository; }

        public OperationResult<List<Game>, BaseError> execute() {
            List<Game> games = gamesRepository.getAll();
            return games != null ? new OperationResult<>(games) : new OperationResult<>(BaseError.getDefaultError());
        }
}
