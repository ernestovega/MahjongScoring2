package es.etologic.mahjongscoring2.app.game.dialogs.winning_hand_dialog

import dagger.Module
import dagger.Provides
import es.etologic.mahjongscoring2.domain.use_cases.GetCurrentGameUseCase

@Module
class WinningHandDialogFragmentModule {
    
    @Provides
    internal fun provideWiningHandDialogFragmentViewModelFactory(
        getCurrentGameUseCase: GetCurrentGameUseCase
    ): WinningHandDialogFragmentViewModelFactory {
        return WinningHandDialogFragmentViewModelFactory(
            getCurrentGameUseCase
        )
    }
}
