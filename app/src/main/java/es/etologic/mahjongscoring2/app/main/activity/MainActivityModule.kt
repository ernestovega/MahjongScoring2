package es.etologic.mahjongscoring2.app.main.activity

import dagger.Module
import dagger.Provides

@Module
class MainActivityModule {
    
    @Provides
    internal fun provideMainViewModelFactory(): MainActivityViewModelFactory {
        return MainActivityViewModelFactory()
    }
}
