package com.etologic.mahjongscoring2.app.game.activity

import com.etologic.mahjongscoring2.business.use_cases.games.current_game.GetCurrentGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.games.current_game.UpdateCurrentPlayersUseCase
import com.etologic.mahjongscoring2.business.use_cases.rounds.current_round.DrawUseCase
import com.etologic.mahjongscoring2.business.use_cases.rounds.current_round.HuUseCase
import com.etologic.mahjongscoring2.business.use_cases.rounds.current_round.PenaltyUseCase
import dagger.Module
import dagger.Provides

@Module
class GameActivityModule {
    
    @Provides
    internal fun provideGameViewModelFactory(
        getCurrentGameUseCase: GetCurrentGameUseCase,
        updateCurrentPlayersUseCase: UpdateCurrentPlayersUseCase,
        huUseCase: HuUseCase,
        drawUseCase: DrawUseCase,
        penaltyUseCase: PenaltyUseCase
    ): GameActivityViewModelFactory {
        return GameActivityViewModelFactory(
            getCurrentGameUseCase,
            updateCurrentPlayersUseCase,
            huUseCase,
            drawUseCase,
            penaltyUseCase
        )
    }
}
