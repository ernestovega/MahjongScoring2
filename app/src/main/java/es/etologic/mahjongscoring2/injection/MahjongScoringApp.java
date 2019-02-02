package es.etologic.mahjongscoring2.injection;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;

public class MahjongScoringApp extends DaggerApplication {

    @Override protected AndroidInjector<? extends MahjongScoringApp> applicationInjector() {
        return DaggerAppComponent.builder().create(this);
    }
}
