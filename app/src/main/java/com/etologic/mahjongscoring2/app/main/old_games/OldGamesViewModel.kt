/*
    Copyright (C) 2020  Ernesto Vega de la Iglesia Soria
 
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.etologic.mahjongscoring2.app.main.old_games

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.etologic.mahjongscoring2.app.base.BaseViewModel
import com.etologic.mahjongscoring2.app.model.ShowState.HIDE
import com.etologic.mahjongscoring2.app.model.ShowState.SHOW
import com.etologic.mahjongscoring2.business.model.entities.Table
import com.etologic.mahjongscoring2.business.model.enums.GameStartType
import com.etologic.mahjongscoring2.business.model.enums.GameStartType.NEW
import com.etologic.mahjongscoring2.business.model.enums.GameStartType.RESUME
import com.etologic.mahjongscoring2.business.use_cases.current_game.SetCurrentGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.games.CreateGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.games.DeleteGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.games.GetGamesUseCase
import io.reactivex.schedulers.Schedulers

internal class OldGamesViewModel(
    private val getGamesUseCase: GetGamesUseCase,
    private val deleteGameUseCase: DeleteGameUseCase,
    private val createGameUseCase: CreateGameUseCase,
    private val setCurrentGameUseCase: SetCurrentGameUseCase
) : BaseViewModel() {
    
    private val _allGames = MutableLiveData<List<Table>>()
    internal fun getGames(): LiveData<List<Table>> = _allGames
    private val _startGame = MutableLiveData<GameStartType>()
    internal fun getStartGame(): LiveData<GameStartType> = _startGame
    
    fun deleteGame(gameId: Long) {
        disposables.add(
            deleteGameUseCase.deleteGame(gameId)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnSuccess(_allGames::postValue)
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
                .doOnSuccess(_allGames::postValue)
                .subscribe({ progressState.postValue(HIDE) }, this::showError)
        )
    }
    
    internal fun startGame(gameId: Long) {
        disposables.add(
            setCurrentGameUseCase.setCurrentGame(gameId)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .doOnSuccess { progressState.postValue(HIDE) }
                .subscribe({ _startGame.postValue(RESUME) }, this::showError)
        )
    }
    
    internal fun startNewGame() {
        disposables.add(
            createGameUseCase.createGame()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .flatMap(setCurrentGameUseCase::setCurrentGame)
                .doOnSuccess { progressState.postValue(HIDE) }
                .subscribe({ _startGame.postValue(NEW) }, this::showError)
        )
    }
}
