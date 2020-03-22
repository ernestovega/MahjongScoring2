package com.etologic.mahjongscoring2.app.main.activity

import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.etologic.mahjongscoring2.app.base.BaseViewModel
import com.etologic.mahjongscoring2.app.main.activity.MainActivityViewModel.MainScreens.GAME
import com.etologic.mahjongscoring2.app.model.ShowState.HIDE
import com.etologic.mahjongscoring2.app.model.ShowState.SHOW
import com.etologic.mahjongscoring2.business.use_cases.games.CreateGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.current_game.SetCurrentGameUseCase
import io.reactivex.schedulers.Schedulers

class MainActivityViewModel
internal constructor(
    private val createGameUseCase: CreateGameUseCase,
    private val setCurrentGameUseCase: SetCurrentGameUseCase
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
    internal fun getCurrentScreen(): LiveData<MainScreens> = currentScreen
    private val currentToolbar = MutableLiveData<Toolbar>()
    internal fun getCurrentToolbar(): LiveData<Toolbar> = currentToolbar
    
    internal fun setToolbar(toolbar: Toolbar) {
        currentToolbar.postValue(toolbar)
    }
    
    internal fun navigateTo(screen: MainScreens) {
        currentScreen.postValue(screen)
    }
    
    internal fun startGame(gameId: Long) {
        disposables.add(
            setCurrentGameUseCase.setCurrentGame(gameId)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnSuccess{ progressState.postValue(HIDE) }
                .subscribe({ navigateTo(GAME) }, this::showError)
        )
    }
    
    internal fun startNewGame() {
        disposables.add(
            createGameUseCase.createGame()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .flatMap(setCurrentGameUseCase::setCurrentGame)
                .doOnSuccess{ progressState.postValue(HIDE) }
                .subscribe({ navigateTo(GAME) }, this::showError)
        )
    }
}