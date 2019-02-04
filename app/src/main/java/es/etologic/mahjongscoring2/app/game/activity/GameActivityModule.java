package es.etologic.mahjongscoring2.app.game.activity;

import dagger.Module;
import dagger.Provides;
import es.etologic.mahjongscoring2.domain.use_cases.GetGameUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetRoundsUseCase;

@Module
public class GameActivityModule {

    @Provides
    GameActivityViewModelFactory provideGameViewModelFactory(GetGameUseCase getGameUseCase,
                                                             GetRoundsUseCase getRoundsRepository) {
        return new GameActivityViewModelFactory(
                getGameUseCase,
                getRoundsRepository);
    }
}
