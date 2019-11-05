package es.etologic.mahjongscoring2.app.game.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import es.etologic.mahjongscoring2.domain.use_cases.CreateGameUseCase
import es.etologic.mahjongscoring2.domain.use_cases.GetGamesUseCase
import es.etologic.mahjongscoring2.domain.use_cases.UpdateGameUseCase
import es.etologic.mahjongscoring2.domain.use_cases.UpdateRoundsUseCase
import javax.inject.Inject

class GameActivityViewModelFactory @Inject internal constructor(
    private val createGameUseCase: CreateGameUseCase,
    private val getGamesUseCase: GetGamesUseCase,
    private val updateRoundUseCase: UpdateRoundsUseCase,
    private val updateGameUseCase: UpdateGameUseCase
) : ViewModelProvider.NewInstanceFactory() {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GameActivityViewModel(createGameUseCase, getGamesUseCase, updateRoundUseCase, updateGameUseCase) as T
    }
}
