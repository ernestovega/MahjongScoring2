package com.etologic.mahjongscoring2.app.main.activity

import dagger.Module
import dagger.Provides
import com.etologic.mahjongscoring2.business.use_cases.games.CreateGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.current_game.SetCurrentGameUseCase

@Module
class MainActivityModule {
    
    @Provides
    internal fun provideMainViewModelFactory(
        createGameUseCase: CreateGameUseCase,
        setCurrentGameUseCase: SetCurrentGameUseCase
    ): MainActivityViewModelFactory {
        return MainActivityViewModelFactory(
            createGameUseCase,
            setCurrentGameUseCase
        )
    }
}
