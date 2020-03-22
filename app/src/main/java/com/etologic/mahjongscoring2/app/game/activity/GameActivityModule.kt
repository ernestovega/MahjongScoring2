package com.etologic.mahjongscoring2.app.game.activity

import com.etologic.mahjongscoring2.business.use_cases.current_game.GetCurrentGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.current_game.SaveCurrentPlayersUseCase
import com.etologic.mahjongscoring2.business.use_cases.current_round.DrawUseCase
import com.etologic.mahjongscoring2.business.use_cases.current_round.HuUseCase
import com.etologic.mahjongscoring2.business.use_cases.current_round.PenaltyUseCase
import dagger.Module
import dagger.Provides

@Module
class GameActivityModule {
    
    @Provides
    internal fun provideGameViewModelFactory(
        getCurrentGameUseCase: GetCurrentGameUseCase,
        saveCurrentPlayersUseCase: SaveCurrentPlayersUseCase,
        huUseCase: HuUseCase,
        drawUseCase: DrawUseCase,
        penaltyUseCase: PenaltyUseCase
    ): GameActivityViewModelFactory {
        return GameActivityViewModelFactory(
            getCurrentGameUseCase,
            saveCurrentPlayersUseCase,
            huUseCase,
            drawUseCase,
            penaltyUseCase
        )
    }
}
