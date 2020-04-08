package com.etologic.mahjongscoring2.app.game.base

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.etologic.mahjongscoring2.app.game.activity.GameActivityViewModelFactory
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel
import dagger.android.support.DaggerAppCompatDialogFragment
import javax.inject.Inject

open class BaseGameDialogFragment : DaggerAppCompatDialogFragment() {
    
    @Inject
    internal lateinit var activityViewModelFactory: GameActivityViewModelFactory
    protected var activityViewModel: GameViewModel? = null
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activityViewModel = activity?.let { ViewModelProvider(it, activityViewModelFactory).get(GameViewModel::class.java) }
    }
}
