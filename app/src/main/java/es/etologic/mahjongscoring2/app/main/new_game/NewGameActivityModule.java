package es.etologic.mahjongscoring2.app.main.new_game;

import dagger.Module;
import dagger.Provides;
import es.etologic.mahjongscoring2.domain.use_cases.CreateGameUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.CreatePlayerUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetAllPlayersUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetPlayerUseCase;

@Module
public class NewGameActivityModule {

    @Provides
    NewGameViewModelFactory provideNewGameViewModelFactory(GetAllPlayersUseCase getAllPlayersUseCase,
                                                           CreatePlayerUseCase createPlayerUseCase,
                                                           GetPlayerUseCase getPlayerUseCase,
                                                           CreateGameUseCase createGameUseCase) {
        return new NewGameViewModelFactory(
                getAllPlayersUseCase,
                createPlayerUseCase,
                getPlayerUseCase,
                createGameUseCase);
    }
}
