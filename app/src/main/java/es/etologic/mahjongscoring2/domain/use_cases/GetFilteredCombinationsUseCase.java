package es.etologic.mahjongscoring2.domain.use_cases;

import android.arch.lifecycle.LiveData;

import java.util.List;

import es.etologic.mahjongscoring2.data.repositories.CombinationsRepository;
import es.etologic.mahjongscoring2.domain.entities.Combination;
import es.etologic.mahjongscoring2.domain.operation_objects.BaseError;
import es.etologic.mahjongscoring2.domain.operation_objects.OperationResult;

public class GetFilteredCombinationsUseCase {

    private final CombinationsRepository combinationsRepository;

    public GetFilteredCombinationsUseCase(CombinationsRepository combinationsRepository) { this.combinationsRepository = combinationsRepository; }

    public LiveData<List<Combination>> execute(String filter) { return combinationsRepository.getFilteredCombinations(filter); }
}
