package com.etologic.mahjongscoring2.app.main.old_games

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.etologic.mahjongscoring2.app.base.BaseViewModel
import com.etologic.mahjongscoring2.app.model.ShowState.HIDE
import com.etologic.mahjongscoring2.app.model.ShowState.SHOW
import com.etologic.mahjongscoring2.business.model.entities.GameWithRounds
import com.etologic.mahjongscoring2.business.use_cases.games.DeleteGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.games.GetGamesUseCase
import io.reactivex.schedulers.Schedulers

internal class OldGamesViewModel(
    private val getGamesUseCase: GetGamesUseCase,
    private val deleteGameUseCase: DeleteGameUseCase
) : BaseViewModel() {
    
    private val allGames = MutableLiveData<List<GameWithRounds>>()
    
    internal fun getGames(): LiveData<List<GameWithRounds>> = allGames
    
    fun deleteGame(gameId: Long) {
        disposables.add(
            deleteGameUseCase.deleteGame(gameId)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnSuccess(allGames::postValue)
                .subscribe({ progressState.postValue(HIDE) }, this::showError)
        )
    }
    
    //METHODS
    fun getAllGames() {
        disposables.add(
            getGamesUseCase.getAllWithRounds()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .onErrorReturnItem(ArrayList())
                .doOnSuccess(allGames::postValue)
                .subscribe({ progressState.postValue(HIDE) }, this::showError)
        )
    }
}
