package es.etologic.mahjongscoring2.app.main.activity

import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.etologic.mahjongscoring2.app.base.BaseViewModel

class MainActivityViewModel : BaseViewModel() {
    
    enum class MainScreens {
        OLD_GAMES,
        COMBINATIONS,
        GREEN_BOOK,
        RATE,
        CONTACT,
        FINISH
    }
    
    private val currentScreen = MutableLiveData<MainScreens>()
    private val currentGame = MutableLiveData<Long>()
    private val currentToolbar = MutableLiveData<Toolbar>()
    
    internal fun getCurrentScreen(): LiveData<MainScreens> = currentScreen
    internal fun getCurrentGame(): LiveData<Long> = currentGame
    internal fun getCurrentToolbar(): LiveData<Toolbar> = currentToolbar
    
    fun setToolbar(toolbar: Toolbar) {
        currentToolbar.postValue(toolbar)
    }
    
    fun navigateTo(screen: MainScreens) {
        if (currentScreen.value != screen)
            currentScreen.postValue(screen)
    }
    
    fun startGame(gameId: Long?) {
        currentGame.postValue(gameId)
    }
}
