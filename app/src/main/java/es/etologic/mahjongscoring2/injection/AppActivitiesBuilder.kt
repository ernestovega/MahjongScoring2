package es.etologic.mahjongscoring2.injection

import dagger.Module
import dagger.android.ContributesAndroidInjector
import es.etologic.mahjongscoring2.app.game.activity.GameActivity
import es.etologic.mahjongscoring2.app.game.activity.GameActivityFragmentsBuilder
import es.etologic.mahjongscoring2.app.game.activity.GameActivityModule
import es.etologic.mahjongscoring2.app.main.activity.MainActivity
import es.etologic.mahjongscoring2.app.main.activity.MainActivityFragmentsBuilder
import es.etologic.mahjongscoring2.app.main.activity.MainActivityModule
import es.etologic.mahjongscoring2.app.main.combinations.CombinationsActivity
import es.etologic.mahjongscoring2.app.main.combinations.CombinationsActivityModule

@Module
internal abstract class AppActivitiesBuilder {
    
    @ContributesAndroidInjector(modules = [MainActivityModule::class, MainActivityFragmentsBuilder::class])
    internal abstract fun bindMainActivity(): MainActivity
    
    @ContributesAndroidInjector(modules = [CombinationsActivityModule::class])
    internal abstract fun bindCombinationsActivity(): CombinationsActivity
    
    @ContributesAndroidInjector(modules = [GameActivityModule::class, GameActivityFragmentsBuilder::class])
    internal abstract fun bindGameActivity(): GameActivity
}
