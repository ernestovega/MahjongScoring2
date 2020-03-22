package com.etologic.mahjongscoring2.app.main.activity

import dagger.Module
import dagger.android.ContributesAndroidInjector
import com.etologic.mahjongscoring2.app.main.old_games.OldGamesFragment

@Module
abstract class MainActivityFragmentsBuilder {
    
    @ContributesAndroidInjector
    internal abstract fun provideOldGamesFragmentFactory(): OldGamesFragment
}
