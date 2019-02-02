package es.etologic.mahjongscoring2.app.game.activity;

import dagger.Module;
import dagger.Provides;
import es.etologic.mahjongscoring2.app.main.activity.MainActivityViewModelFactory;
import es.etologic.mahjongscoring2.data.repositories.GamesRepository;
import es.etologic.mahjongscoring2.data.repositories.RoundsRepository;

@Module
public class GameActivityModule {

    @Provides
    GameActivityViewModelFactory provideGameViewModelFactory(GamesRepository gamesRepository, RoundsRepository roundsRepository) {
        return new GameActivityViewModelFactory(gamesRepository, roundsRepository);
    }
}
