package es.etologic.mahjongscoring2.injection

import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import es.etologic.mahjongscoring2.data.local_data_source.local.AppDataBaseModule
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
