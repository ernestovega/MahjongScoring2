package es.etologic.mahjongscoring2.app.game.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import es.etologic.mahjongscoring2.domain.use_cases.EndGameUseCase
import es.etologic.mahjongscoring2.domain.use_cases.GetCurrentGameUseCase
import es.etologic.mahjongscoring2.domain.use_cases.UpdateGameUseCase
import javax.inject.Inject

class GameActivityViewModelFactory @Inject internal constructor(
    private val getCurrentGameUseCase: GetCurrentGameUseCase,
    private val updateGameUseCase: UpdateGameUseCase,
    private val endGameUseCase: EndGameUseCase
) : ViewModelProvider.NewInstanceFactory() {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GameActivityViewModel(getCurrentGameUseCase, updateGameUseCase, endGameUseCase) as T
    }
}
