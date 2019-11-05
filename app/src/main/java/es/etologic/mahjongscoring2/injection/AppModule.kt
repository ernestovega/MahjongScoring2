package es.etologic.mahjongscoring2.injection

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal class AppModule {
    
    @Provides
    @Singleton
    internal fun provideContext(mahjongScoringApp: MahjongScoringApp): Context = mahjongScoringApp
}
