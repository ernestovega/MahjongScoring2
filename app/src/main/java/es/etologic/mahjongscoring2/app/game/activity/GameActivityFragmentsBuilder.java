package es.etologic.mahjongscoring2.app.game.activity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import es.etologic.mahjongscoring2.app.game.game_list.GameListFragment;
import es.etologic.mahjongscoring2.app.game.game_table.GameTableFragment;

@Module
public abstract class GameActivityFragmentsBuilder {

    @ContributesAndroidInjector
    abstract GameListFragment provideGameListFragmentFactory();

    @ContributesAndroidInjector
    abstract GameTableFragment provideGameTableFragmentFactory();

}
