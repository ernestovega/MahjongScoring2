package es.etologic.mahjongscoring2.injection;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {
        AndroidSupportInjectionModule.class,
        AppModule.class,
        AppActivitiesBuilder.class})
public interface AppComponent extends AndroidInjector<MahjongScoringApp> {

    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<MahjongScoringApp> {}
}
