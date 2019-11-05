package es.etologic.mahjongscoring2.app.main.combinations

import dagger.Module
import dagger.Provides
import es.etologic.mahjongscoring2.domain.use_cases.GetCombinationsUseCase

@Module
class CombinationsActivityModule {
    
    @Provides
    internal fun provideCombinationsViewModelFactory(getCombinationsUseCase: GetCombinationsUseCase): CombinationsViewModelFactory {
        return CombinationsViewModelFactory(getCombinationsUseCase)
    }
}
