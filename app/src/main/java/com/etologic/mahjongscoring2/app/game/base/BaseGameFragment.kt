package com.etologic.mahjongscoring2.app.game.base

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.etologic.mahjongscoring2.app.base.BaseFragment
import com.etologic.mahjongscoring2.app.game.activity.GameActivityViewModel
import com.etologic.mahjongscoring2.app.game.activity.GameActivityViewModelFactory
import javax.inject.Inject

abstract class BaseGameFragment : BaseFragment() {
    
    @Inject
    internal lateinit var activityViewModelFactory: GameActivityViewModelFactory
    protected var activityViewModel: GameActivityViewModel? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        if(activity != null)
            activityViewModel = ViewModelProvider(activity!!, activityViewModelFactory).get(GameActivityViewModel::class.java)
    }
}