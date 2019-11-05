package es.etologic.mahjongscoring2.injection

import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

class MahjongScoringApp : DaggerApplication() {
    
    override fun applicationInjector(): AndroidInjector<out MahjongScoringApp> {
        return DaggerAppComponent.builder().create(this)
    }
}
