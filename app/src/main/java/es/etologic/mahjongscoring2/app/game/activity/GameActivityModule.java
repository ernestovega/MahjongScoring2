package es.etologic.mahjongscoring2.app.game.activity;

import dagger.Module;
import dagger.Provides;
import es.etologic.mahjongscoring2.domain.use_cases.CreateGameUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetGamesUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.UpdateGameUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.UpdateRoundsUseCase;

@Module
public class GameActivityModule {

    @Provides
    GameActivityViewModelFactory provideGameViewModelFactory(CreateGameUseCase createGameUseCase,
                                                             GetGamesUseCase getGamesUseCase,
                                                             UpdateRoundsUseCase updateRoundUseCase,
                                                             UpdateGameUseCase updateGameUseCase) {
        return new GameActivityViewModelFactory(
                createGameUseCase,
                getGamesUseCase,
                updateRoundUseCase,
                updateGameUseCase);
    }
}
