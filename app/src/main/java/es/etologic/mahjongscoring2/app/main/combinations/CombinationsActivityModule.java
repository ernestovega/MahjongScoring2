package es.etologic.mahjongscoring2.app.main.combinations;

import dagger.Module;
import dagger.Provides;
import es.etologic.mahjongscoring2.domain.use_cases.GetCombinationsUseCase;

@Module
public class CombinationsActivityModule {

    @Provides
    CombinationsViewModelFactory provideCombinationsViewModelFactory(GetCombinationsUseCase getCombinationsUseCase) {
        return new CombinationsViewModelFactory(getCombinationsUseCase);
    }
}
