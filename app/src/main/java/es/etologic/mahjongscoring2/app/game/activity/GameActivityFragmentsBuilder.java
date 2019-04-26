package es.etologic.mahjongscoring2.app.game.activity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import es.etologic.mahjongscoring2.app.game.game_list.GameListFragment;
import es.etologic.mahjongscoring2.app.game.game_list.GameListModule;
import es.etologic.mahjongscoring2.app.game.game_table.GameTableFragment;
import es.etologic.mahjongscoring2.app.game.game_table.GameTableRankingFragmentDialog;
import es.etologic.mahjongscoring2.app.game.game_table.GameTableSeatsFragment;

@Module
public abstract class GameActivityFragmentsBuilder {

    @ContributesAndroidInjector(modules = GameListModule.class)
    abstract GameListFragment provideGameListFragmentFactory();

    @ContributesAndroidInjector
    abstract GameTableFragment provideGameTableFragmentFactory();

    @ContributesAndroidInjector
    abstract GameTableSeatsFragment provideGameTableSeatFragmentFactory();

    @ContributesAndroidInjector
    abstract GameTableRankingFragmentDialog provideGameTableRankingFragmentDialogFactory();
}
