package es.etologic.mahjongscoring2.app.game.activity

import dagger.Module
import dagger.Provides
import es.etologic.mahjongscoring2.domain.use_cases.CreateGameUseCase
import es.etologic.mahjongscoring2.domain.use_cases.GetGamesUseCase
import es.etologic.mahjongscoring2.domain.use_cases.UpdateGameUseCase
import es.etologic.mahjongscoring2.domain.use_cases.UpdateRoundsUseCase

@Module
class GameActivityModule {
    
    @Provides
    internal fun provideGameViewModelFactory(
        createGameUseCase: CreateGameUseCase,
        getGamesUseCase: GetGamesUseCase,
        updateRoundUseCase: UpdateRoundsUseCase,
        updateGameUseCase: UpdateGameUseCase
    ): GameActivityViewModelFactory {
        return GameActivityViewModelFactory(
            createGameUseCase,
            getGamesUseCase,
            updateRoundUseCase,
            updateGameUseCase
        )
    }
}
