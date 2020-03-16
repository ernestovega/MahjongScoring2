package es.etologic.mahjongscoring2.app.game.activity

import dagger.Module
import dagger.Provides
import es.etologic.mahjongscoring2.domain.use_cases.GetCurrentGameUseCase
import es.etologic.mahjongscoring2.domain.use_cases.GetGamesUseCase
import es.etologic.mahjongscoring2.domain.use_cases.UpdateGameUseCase

@Module
class GameActivityModule {
    
    @Provides
    internal fun provideGameViewModelFactory(
        getCurrentGameUseCase: GetCurrentGameUseCase,
        updateGameUseCase: UpdateGameUseCase
    ): GameActivityViewModelFactory {
        return GameActivityViewModelFactory(
            getCurrentGameUseCase,
            updateGameUseCase
        )
    }
}
