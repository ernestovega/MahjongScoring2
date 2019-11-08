package es.etologic.mahjongscoring2.app.game.base

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import es.etologic.mahjongscoring2.app.base.BaseFragment
import es.etologic.mahjongscoring2.app.game.activity.GameActivityViewModel
import es.etologic.mahjongscoring2.app.game.activity.GameActivityViewModelFactory
import javax.inject.Inject

abstract class BaseGameActivityFragment : BaseFragment() {
    
    @Inject
    internal lateinit var activityViewModelFactory: GameActivityViewModelFactory
    protected lateinit var activityViewModel: GameActivityViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityViewModel = ViewModelProviders.of(this, activityViewModelFactory).get(GameActivityViewModel::class.java)
    }
}