package es.etologic.mahjongscoring2.app.main.activity

import dagger.Module
import dagger.Provides
import es.etologic.mahjongscoring2.domain.use_cases.CreateGameUseCase
import es.etologic.mahjongscoring2.domain.use_cases.UpdateCurrentGameUseCase

@Module
class MainActivityModule {
    
    @Provides
    internal fun provideMainViewModelFactory(
        createGameUseCase: CreateGameUseCase,
        updateCurrentGameUseCase: UpdateCurrentGameUseCase
    ): MainActivityViewModelFactory {
        return MainActivityViewModelFactory(
            createGameUseCase,
            updateCurrentGameUseCase
        )
    }
}
