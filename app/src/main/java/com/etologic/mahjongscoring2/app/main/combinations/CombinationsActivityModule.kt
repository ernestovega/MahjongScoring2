package com.etologic.mahjongscoring2.app.main.combinations

import dagger.Module
import dagger.Provides
import com.etologic.mahjongscoring2.business.use_cases.combinations.GetCombinationsUseCase

@Module
class CombinationsActivityModule {
    
    @Provides
    internal fun provideCombinationsViewModelFactory(getCombinationsUseCase: GetCombinationsUseCase): CombinationsViewModelFactory {
        return CombinationsViewModelFactory(getCombinationsUseCase)
    }
}
