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
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.etologic.mahjongscoring2.app.base.BaseViewModel
import com.etologic.mahjongscoring2.business.model.enums.ShareGameOptions
import com.etologic.mahjongscoring2.business.model.enums.ShareGameOptions.CSV
import com.etologic.mahjongscoring2.business.model.enums.ShareGameOptions.TEXT
import com.etologic.mahjongscoring2.business.use_cases.CreateGameUseCase
import com.etologic.mahjongscoring2.business.use_cases.ExportAllGamesToCsvUseCase
import com.etologic.mahjongscoring2.business.use_cases.ExportDbToCsvUseCase
import com.etologic.mahjongscoring2.business.use_cases.ExportGameToCsvUseCase
import com.etologic.mahjongscoring2.business.use_cases.ExportGameResultsToTextUseCase
import com.etologic.mahjongscoring2.business.use_cases.GetIsDiffCalcsFeatureEnabledUseCase
import com.etologic.mahjongscoring2.business.use_cases.SaveIsDiffCalcsFeatureEnabledUseCase
import com.etologic.mahjongscoring2.business.use_cases.ShowInAppReviewUseCase
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.GameId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val createGameUseCase: CreateGameUseCase,
    private val showInAppReviewUseCase: ShowInAppReviewUseCase,
    private val exportDbToCsvUseCase: ExportDbToCsvUseCase,
    private val exportGameResultsToTextUseCase: ExportGameResultsToTextUseCase,
    private val exportGameToCsvUseCase: ExportGameToCsvUseCase,
    private val exportAllGamesToCsvUseCase: ExportAllGamesToCsvUseCase,
    getIsDiffCalcsFeatureEnabledUseCase: GetIsDiffCalcsFeatureEnabledUseCase,
    private val saveIsDiffCalcsFeatureEnabledUseCase: SaveIsDiffCalcsFeatureEnabledUseCase,
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

    private val _currentScreen = MutableLiveData<MainScreens>()
    fun getCurrentScreen(): LiveData<MainScreens> = _currentScreen
    private val _currentToolbar = MutableLiveData<Toolbar>()
    fun getCurrentToolbar(): LiveData<Toolbar> = _currentToolbar
    private val _exportedText = MutableLiveData<String>()
    fun getExportedText(): LiveData<String> = _exportedText
    private val _exportedFiles = MutableLiveData<List<File>>()
    fun getExportedFiles(): LiveData<List<File>> = _exportedFiles

    val isDiffsCalcsFeatureEnabledFlow: StateFlow<Boolean> = getIsDiffCalcsFeatureEnabledUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    fun setToolbar(toolbar: Toolbar) {
        _currentToolbar.postValue(toolbar)
    }

    fun navigateTo(screen: MainScreens) {
        _currentScreen.postValue(screen)
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

    fun exportDb(getExternalFilesDir: () -> File?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                exportDbToCsvUseCase.invoke(getExternalFilesDir)
                    .fold(_exportedFiles::postValue, ::showError)
            }
        }
    }

    fun shareGame(gameId: GameId, option: ShareGameOptions, getExternalFilesDir: () -> File?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                when (option) {
                    TEXT -> exportGameResultsToTextUseCase.invoke(gameId)
                        .fold(_exportedText::postValue, ::showError)

                    CSV -> exportGameToCsvUseCase.invoke(gameId, getExternalFilesDir)
                        .fold(_exportedFiles::postValue, ::showError)
                }
            }
        }
    }

    fun shareAllGames(getExternalFilesDir: () -> File?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                exportAllGamesToCsvUseCase.invoke(getExternalFilesDir)
                    .fold(_exportedFiles::postValue, ::showError)
            }
        }
    }

    fun toggleDiffsFeature(isEnabled: Boolean) {
        viewModelScope.launch {
            saveIsDiffCalcsFeatureEnabledUseCase(isEnabled)
        }
    }
}