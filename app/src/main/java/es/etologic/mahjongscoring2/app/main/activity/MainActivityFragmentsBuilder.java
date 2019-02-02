package es.etologic.mahjongscoring2.app.main.activity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import es.etologic.mahjongscoring2.app.main.old_games.OldGamesFragment;

@Module
public abstract class MainActivityFragmentsBuilder {

    @ContributesAndroidInjector
    abstract OldGamesFragment provideOldGamesFragmentFactory();

}
