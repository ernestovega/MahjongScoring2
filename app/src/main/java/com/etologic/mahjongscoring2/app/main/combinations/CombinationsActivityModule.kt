package com.etologic.mahjongscoring2.app.main.combinations

import com.etologic.mahjongscoring2.business.use_cases.combinations.GetCombinationsUseCase
import dagger.Module
import dagger.Provides

@Module
class CombinationsActivityModule {
    
    @Provides
    internal fun provideCombinationsViewModelFactory(getCombinationsUseCase: GetCombinationsUseCase): CombinationsViewModelFactory {
        return CombinationsViewModelFactory(getCombinationsUseCase)
    }
}
