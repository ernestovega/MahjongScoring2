/*
 *     Copyright © 2023  Ernesto Vega de la Iglesia Soria
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.etologic.mahjongscoring2.app.main.activity

import android.app.Activity
import android.content.Context
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.etologic.mahjongscoring2.app.base.BaseViewModel
import com.etologic.mahjongscoring2.business.model.dtos.ExportedDb
import com.etologic.mahjongscoring2.business.use_cases.CreateGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.ExportDbToCsvUseCase
import com.etologic.mahjongscoring2.business.use_cases.ExportGameToTextUseCase
import com.etologic.mahjongscoring2.business.use_cases.ShowInAppReviewUseCase
import com.etologic.mahjongscoring2.data_source.model.GameId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val createGameUseCase: CreateGameUseCase,
    private val showInAppReviewUseCase: ShowInAppReviewUseCase,
    private val exportDbToCsvUseCase: ExportDbToCsvUseCase,
    private val exportGameToTextUseCase: ExportGameToTextUseCase,
) : BaseViewModel() {

    enum class MainScreens {
        OLD_GAMES,
        SETUP_NEW_GAME,
        GAME,
        COMBINATIONS,
        DIFFS_CALCULATOR,
        GREEN_BOOK_ENGLISH,
        GREEN_BOOK_SPANISH,
        MM_WEB,
        EMA_WEB,
        CONTACT,
    }

    var activeGameId: GameId? = null

    private val currentScreen = MutableLiveData<MainScreens>()
    fun getCurrentScreen(): LiveData<MainScreens> = currentScreen
    private val currentToolbar = MutableLiveData<Toolbar>()
    fun getCurrentToolbar(): LiveData<Toolbar> = currentToolbar
    private val exportedDb = MutableLiveData<ExportedDb>()
    fun getExportedFiles(): LiveData<ExportedDb> = exportedDb
    private val exportedGame = MutableLiveData<String>()
    fun getExportedGame(): LiveData<String> = exportedGame

    fun setToolbar(toolbar: Toolbar) {
        currentToolbar.postValue(toolbar)
    }

    fun navigateTo(screen: MainScreens) {
        currentScreen.postValue(screen)
    }

    fun createGame(
        gameName: String,
        nameP1: String,
        nameP2: String,
        nameP3: String,
        nameP4: String,
        onSuccess: (GameId) -> Unit,
    ) {
        viewModelScope.launch {
            createGameUseCase.invoke(gameName, nameP1, nameP2, nameP3, nameP4)
                .fold(onSuccess, ::showError)
        }
    }

    fun showInAppReviewIfProceed(activity: Activity) {
        viewModelScope.launch { showInAppReviewUseCase(activity) }
    }

    fun exportDb(context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                exportDbToCsvUseCase.invoke(context.getExternalFilesDir(null))
                    .also { exportedDb.postValue(it) }
            }
        }
    }

    fun shareGame(gameId: GameId, getStringRes: (Int) -> String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                exportGameToTextUseCase.invoke(gameId, getStringRes)
                    .also { exportedGame.postValue(it) }
            }
        }
    }
}