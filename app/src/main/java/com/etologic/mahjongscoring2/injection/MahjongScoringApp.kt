package com.etologic.mahjongscoring2.injection

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class MahjongScoringApp : DaggerApplication() {
    
    override fun applicationInjector(): AndroidInjector<out MahjongScoringApp> {
        return DaggerAppComponent.factory().create(this)
    }
}
