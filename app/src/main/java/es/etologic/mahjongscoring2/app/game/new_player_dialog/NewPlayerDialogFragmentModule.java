package es.etologic.mahjongscoring2.app.game.new_player_dialog;

import dagger.Module;
import dagger.Provides;
import es.etologic.mahjongscoring2.domain.use_cases.CreatePlayerUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetAllPlayersUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetPlayerUseCase;

@Module
public class NewPlayerDialogFragmentModule {

    @Provides
    NewPlayerDialogViewModelFactory provideNewGameViewModelFactory(GetAllPlayersUseCase getAllPlayersUseCase,
                                                                   CreatePlayerUseCase createPlayerUseCase,
                                                                   GetPlayerUseCase getPlayerUseCase) {
        return new NewPlayerDialogViewModelFactory(
                getAllPlayersUseCase,
                createPlayerUseCase,
                getPlayerUseCase);
    }
}
