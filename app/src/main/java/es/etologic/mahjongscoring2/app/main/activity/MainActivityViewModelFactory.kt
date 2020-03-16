package es.etologic.mahjongscoring2.app.main.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import es.etologic.mahjongscoring2.domain.use_cases.*

class MainActivityViewModelFactory(
    private val createGameUseCase: CreateGameUseCase,
    private val updateCurrentGameUseCase: UpdateCurrentGameUseCase
) : ViewModelProvider.NewInstanceFactory() {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainActivityViewModel(
            createGameUseCase,
            updateCurrentGameUseCase
        ) as T
    }
}
