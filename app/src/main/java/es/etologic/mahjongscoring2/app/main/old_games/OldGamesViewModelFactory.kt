package es.etologic.mahjongscoring2.app.main.old_games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import es.etologic.mahjongscoring2.domain.use_cases.DeleteGameUseCase
import es.etologic.mahjongscoring2.domain.use_cases.GetGamesUseCase
import javax.inject.Inject

class OldGamesViewModelFactory @Inject
constructor(private val getGamesUseCase: GetGamesUseCase, private val deleteGameUseCase: DeleteGameUseCase) : ViewModelProvider.NewInstanceFactory() {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return OldGamesViewModel(getGamesUseCase, deleteGameUseCase) as T
    }
}
