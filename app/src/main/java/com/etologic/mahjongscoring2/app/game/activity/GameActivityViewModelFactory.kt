package com.etologic.mahjongscoring2.app.game.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.etologic.mahjongscoring2.business.use_cases.games.current_game.GetCurrentGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.games.current_game.UpdateCurrentPlayersUseCase
import com.etologic.mahjongscoring2.business.use_cases.rounds.current_round.DrawUseCase
import com.etologic.mahjongscoring2.business.use_cases.rounds.current_round.HuUseCase
import com.etologic.mahjongscoring2.business.use_cases.rounds.current_round.PenaltyUseCase
import javax.inject.Inject

class GameActivityViewModelFactory
@Inject internal constructor(
    private val getCurrentGameUseCase: GetCurrentGameUseCase,
    private val updateCurrentPlayersUseCase: UpdateCurrentPlayersUseCase,
    private val huUseCase: HuUseCase,
    private val drawUseCase: DrawUseCase,
    private val penaltyUseCase: PenaltyUseCase
) : ViewModelProvider.NewInstanceFactory() {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GameActivityViewModel(
            getCurrentGameUseCase,
            updateCurrentPlayersUseCase,
            huUseCase,
            drawUseCase,
            penaltyUseCase
        ) as T
    }
}
