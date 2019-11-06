package es.etologic.mahjongscoring2.app.main.old_games

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.etologic.mahjongscoring2.app.base.BaseViewModel
import es.etologic.mahjongscoring2.app.model.ShowState.HIDE
import es.etologic.mahjongscoring2.app.model.ShowState.SHOW
import es.etologic.mahjongscoring2.domain.model.GameWithRounds
import es.etologic.mahjongscoring2.domain.use_cases.DeleteGameUseCase
import es.etologic.mahjongscoring2.domain.use_cases.GetGamesUseCase
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
                .doOnEvent { _, _ -> progressState.postValue(HIDE) }
                .subscribe({ getAllGames() }, { error.postValue(it) })
        )
    }
    
    //METHODS
    fun getAllGames() {
        disposables.add(
            getGamesUseCase.getAllWithRounds()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnEvent { _, _ -> progressState.postValue(HIDE) }
                .subscribe(allGames::postValue) {
                    allGames.postValue(ArrayList())
                    error.postValue(it)
                }
        )
    }
}
