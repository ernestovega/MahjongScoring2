package com.etologic.mahjongscoring2.injection

import com.etologic.mahjongscoring2.data_source.local_data_source.local.AppDataBaseModule
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AppModule::class,
        AppActivitiesBuilder::class,
        AppDataBaseModule::class]
)
interface AppComponent : AndroidInjector<MahjongScoringApp> {
    
    @Component.Factory
    abstract class Factory : AndroidInjector.Factory<MahjongScoringApp>
}
