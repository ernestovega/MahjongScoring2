package com.etologic.mahjongscoring2.app.main.activity

import com.etologic.mahjongscoring2.app.main.old_games.OldGamesFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityFragmentsBuilder {
    
    @ContributesAndroidInjector
    internal abstract fun provideOldGamesFragmentFactory(): OldGamesFragment
}
