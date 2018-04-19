package es.etologic.mahjongscoring2.domain.use_cases;

import java.util.List;

import es.etologic.mahjongscoring2.data.repositories.CombinationsRepository;
import es.etologic.mahjongscoring2.domain.entities.Combination;
import es.etologic.mahjongscoring2.domain.operation_objects.BaseError;
import es.etologic.mahjongscoring2.domain.operation_objects.OperationResult;

public class GetAllCombinationsUseCase {

    private final CombinationsRepository combinationsRepository;

    public GetAllCombinationsUseCase(CombinationsRepository combinationsRepository) { this.combinationsRepository = combinationsRepository; }

    public OperationResult<List<Combination>, BaseError> execute() {
        List<Combination> combinations = combinationsRepository.getAll();
        return combinations != null ? new OperationResult<>(combinations) : new OperationResult<>(BaseError.getDefaultError());
    }
}
