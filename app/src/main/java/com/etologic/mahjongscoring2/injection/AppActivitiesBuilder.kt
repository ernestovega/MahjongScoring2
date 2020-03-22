package com.etologic.mahjongscoring2.injection

import dagger.Module
import dagger.android.ContributesAndroidInjector
import com.etologic.mahjongscoring2.app.game.activity.GameActivity
import com.etologic.mahjongscoring2.app.game.activity.GameActivityFragmentsBuilder
import com.etologic.mahjongscoring2.app.game.activity.GameActivityModule
import com.etologic.mahjongscoring2.app.main.activity.MainActivity
import com.etologic.mahjongscoring2.app.main.activity.MainActivityFragmentsBuilder
import com.etologic.mahjongscoring2.app.main.activity.MainActivityModule
import com.etologic.mahjongscoring2.app.main.combinations.CombinationsActivity
import com.etologic.mahjongscoring2.app.main.combinations.CombinationsActivityModule

@Module
internal abstract class AppActivitiesBuilder {
    
    @ContributesAndroidInjector(modules = [MainActivityModule::class, MainActivityFragmentsBuilder::class])
    internal abstract fun bindMainActivity(): MainActivity
    
    @ContributesAndroidInjector(modules = [CombinationsActivityModule::class])
    internal abstract fun bindCombinationsActivity(): CombinationsActivity
    
    @ContributesAndroidInjector(modules = [GameActivityModule::class, GameActivityFragmentsBuilder::class])
    internal abstract fun bindGameActivity(): GameActivity
}
