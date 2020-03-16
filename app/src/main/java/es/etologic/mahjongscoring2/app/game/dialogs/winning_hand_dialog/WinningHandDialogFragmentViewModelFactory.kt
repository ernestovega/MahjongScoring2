package es.etologic.mahjongscoring2.app.game.dialogs.winning_hand_dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import es.etologic.mahjongscoring2.domain.use_cases.*
import javax.inject.Inject

class WinningHandDialogFragmentViewModelFactory @Inject internal constructor(
    private val getCurrentGameUseCase: GetCurrentGameUseCase
) : ViewModelProvider.NewInstanceFactory() {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WinningHandDialogFragmentViewModel(getCurrentGameUseCase) as T
    }
}
