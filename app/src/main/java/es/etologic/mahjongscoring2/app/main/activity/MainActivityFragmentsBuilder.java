package es.etologic.mahjongscoring2.app.main.activity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import es.etologic.mahjongscoring2.app.game.new_player_dialog.NewPlayerDialogFragment;
import es.etologic.mahjongscoring2.app.game.new_player_dialog.NewPlayerDialogFragmentModule;
import es.etologic.mahjongscoring2.app.main.old_games.OldGamesFragment;

@Module
public abstract class MainActivityFragmentsBuilder {

    @ContributesAndroidInjector
    abstract OldGamesFragment provideOldGamesFragmentFactory();

    @ContributesAndroidInjector(modules = NewPlayerDialogFragmentModule.class)
    abstract NewPlayerDialogFragment provideNewGamesFragmentFactory();

}
