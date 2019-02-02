package es.etologic.mahjongscoring2.app.main.activity;

import dagger.Module;
import dagger.Provides;

@Module
public class MainActivityModule {

    @Provides
    MainActivityViewModelFactory provideMainViewModelFactory() {
        return new MainActivityViewModelFactory();
    }
}
