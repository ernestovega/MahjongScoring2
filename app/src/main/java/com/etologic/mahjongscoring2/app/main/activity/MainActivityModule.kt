package com.etologic.mahjongscoring2.app.main.activity

import com.etologic.mahjongscoring2.business.use_cases.current_game.SetCurrentGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.games.CreateGameUseCase
import dagger.Module
import dagger.Provides

@Module
class MainActivityModule {
    
    @Provides
    internal fun provideMainViewModelFactory(): MainActivityViewModelFactory = MainActivityViewModelFactory()
}
