package es.etologic.mahjongscoring2.domain.use_cases;

import java.util.List;

import javax.inject.Inject;

import es.etologic.mahjongscoring2.data.repositories.CombinationsRepository;
import es.etologic.mahjongscoring2.domain.model.Combination;
import io.reactivex.Single;

public class GetCombinationsUseCase {

    private final CombinationsRepository combinationsRepository;

    @Inject
    public GetCombinationsUseCase(CombinationsRepository combinationsRepository) { this.combinationsRepository = combinationsRepository; }

    public Single<List<Combination>> getSome(String filter) { return combinationsRepository.getFiltered(filter); }

    public Single<List<Combination>> getAll() { return combinationsRepository.getAll(); }
}
