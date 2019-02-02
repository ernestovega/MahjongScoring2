package es.etologic.mahjongscoring2.injection;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
class AppModule {

    @Provides
    @Singleton
    Context provideContext(MahjongScoringApp mahjongScoringApp) { return mahjongScoringApp; }
}
