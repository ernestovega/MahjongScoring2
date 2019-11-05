package es.etologic.mahjongscoring2.injection

import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AppModule::class,
        AppActivitiesBuilder::class]
)
interface AppComponent : AndroidInjector<MahjongScoringApp> {
    
    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<MahjongScoringApp>()
}
