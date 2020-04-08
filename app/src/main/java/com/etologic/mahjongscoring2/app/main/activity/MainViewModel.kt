package com.etologic.mahjongscoring2.app.main.activity

import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.etologic.mahjongscoring2.app.base.BaseViewModel
import com.etologic.mahjongscoring2.business.model.enums.GameStartType

class MainViewModel internal constructor() : BaseViewModel() {
    
    enum class MainScreens {
        OLD_GAMES,
        GAME,
        COMBINATIONS,
        GREEN_BOOK,
        RATE,
        CONTACT,
        FINISH
    }
    
    private val currentScreen = MutableLiveData<MainScreens>()
    internal fun getCurrentScreen(): LiveData<MainScreens> = currentScreen
    private val currentToolbar = MutableLiveData<Toolbar>()
    internal fun getCurrentToolbar(): LiveData<Toolbar> = currentToolbar
    internal var lastGameStartType: GameStartType? = null
    
    internal fun setToolbar(toolbar: Toolbar) {
        currentToolbar.postValue(toolbar)
    }
    
    internal fun navigateTo(screen: MainScreens) {
        currentScreen.postValue(screen)
    }
}