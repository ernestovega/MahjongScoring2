package es.etologic.mahjongscoring2.app.game.activity;

import dagger.Module;
import dagger.Provides;
import es.etologic.mahjongscoring2.domain.use_cases.GetGameUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetGamesWithRoundsUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetRoundsUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.UpdateRoundsUseCase;

@Module
public class GameActivityModule {

    @Provides
    GameActivityViewModelFactory provideGameViewModelFactory(GetGamesWithRoundsUseCase getGamesWithRoundsUseCase,
                                                             UpdateRoundsUseCase updateRoundUseCase) {
        return new GameActivityViewModelFactory(
                getGamesWithRoundsUseCase, updateRoundUseCase);
    }
}
