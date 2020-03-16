package es.etologic.mahjongscoring2.app.main.activity

import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.etologic.mahjongscoring2.app.base.BaseViewModel
import es.etologic.mahjongscoring2.app.main.activity.MainActivityViewModel.MainScreens.GAME
import es.etologic.mahjongscoring2.app.model.ShowState.HIDE
import es.etologic.mahjongscoring2.app.model.ShowState.SHOW
import es.etologic.mahjongscoring2.domain.use_cases.CreateGameUseCase
import es.etologic.mahjongscoring2.domain.use_cases.UpdateCurrentGameUseCase
import io.reactivex.schedulers.Schedulers

class MainActivityViewModel
internal constructor(
    private val createGameUseCase: CreateGameUseCase,
    private val updateCurrentGameUseCase: UpdateCurrentGameUseCase
) : BaseViewModel() {
    
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
    private val currentToolbar = MutableLiveData<Toolbar>()
    
    internal fun getCurrentScreen(): LiveData<MainScreens> = currentScreen
    internal fun getCurrentToolbar(): LiveData<Toolbar> = currentToolbar
    
    internal fun setToolbar(toolbar: Toolbar) {
        currentToolbar.postValue(toolbar)
    }
    
    internal fun navigateTo(screen: MainScreens) {
        if (currentScreen.value != screen)
            currentScreen.postValue(screen)
    }
    
    internal fun startGame(gameId: Long) {
        disposables.add(
            updateCurrentGameUseCase.setCurrentGame(gameId)
                .subscribeOn(Schedulers.io())
                .subscribe({ navigateTo(GAME) }, error::postValue)
        )
    }
    
    internal fun startNewGame() {
        disposables.add(
            createGameUseCase.createGame()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnEvent { _, _ -> progressState.postValue(HIDE) }
                .subscribe(this::startGame, error::postValue)
        )
    }
}