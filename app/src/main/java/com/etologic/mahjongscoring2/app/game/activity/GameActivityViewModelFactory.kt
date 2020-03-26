package com.etologic.mahjongscoring2.app.game.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.etologic.mahjongscoring2.business.use_cases.current_game.GetCurrentGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.current_game.SaveCurrentPlayersUseCase
import com.etologic.mahjongscoring2.business.use_cases.current_round.HuUseCase
import com.etologic.mahjongscoring2.business.use_cases.current_round.PenaltyUseCase
import javax.inject.Inject

class GameActivityViewModelFactory
@Inject internal constructor(
    private val getCurrentGameUseCase: GetCurrentGameUseCase,
    private val saveCurrentPlayersUseCase: SaveCurrentPlayersUseCase,
    private val huUseCase: HuUseCase,
    private val penaltyUseCase: PenaltyUseCase
) : ViewModelProvider.NewInstanceFactory() {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GameActivityViewModel(
            getCurrentGameUseCase,
            saveCurrentPlayersUseCase,
            huUseCase,
            penaltyUseCase
        ) as T
    }
}
