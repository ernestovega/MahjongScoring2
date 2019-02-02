package es.etologic.mahjongscoring2.injection;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import es.etologic.mahjongscoring2.app.game.activity.GameActivity;
import es.etologic.mahjongscoring2.app.game.activity.GameActivityFragmentsBuilder;
import es.etologic.mahjongscoring2.app.game.activity.GameActivityModule;
import es.etologic.mahjongscoring2.app.main.activity.MainActivity;
import es.etologic.mahjongscoring2.app.main.activity.MainActivityFragmentsBuilder;
import es.etologic.mahjongscoring2.app.main.activity.MainActivityModule;
import es.etologic.mahjongscoring2.app.main.combinations.CombinationsActivity;
import es.etologic.mahjongscoring2.app.main.combinations.CombinationsActivityModule;
import es.etologic.mahjongscoring2.app.main.new_game.NewGameActivity;
import es.etologic.mahjongscoring2.app.main.new_game.NewGameActivityModule;

@Module
abstract class AppActivitiesBuilder {

    @ContributesAndroidInjector(modules = {MainActivityModule.class, MainActivityFragmentsBuilder.class})
    abstract MainActivity bindMainActivity();

    @ContributesAndroidInjector(modules = {CombinationsActivityModule.class})
    abstract CombinationsActivity bindCombinationsActivity();

    @ContributesAndroidInjector(modules = {NewGameActivityModule.class})
    abstract NewGameActivity bindNewGameActivity();

    @ContributesAndroidInjector(modules = {GameActivityModule.class, GameActivityFragmentsBuilder.class})
    abstract GameActivity bindGameActivity();
}
