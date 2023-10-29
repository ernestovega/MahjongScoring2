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
package com.etologic.mahjongscoring2.app.main.activity

import android.app.Activity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.etologic.mahjongscoring2.app.base.BaseViewModel
import com.etologic.mahjongscoring2.business.model.enums.GameStartType
import com.etologic.mahjongscoring2.business.use_cases.ShowInAppReviewUseCase
import io.reactivex.schedulers.Schedulers

class MainViewModel internal constructor(
    private val showInAppReviewUseCase: ShowInAppReviewUseCase,
) : BaseViewModel() {

    enum class MainScreens {
        OLD_GAMES,
        GAME,
        COMBINATIONS,
        GREEN_BOOK,
        MM_WEB,
        EMA_WEB,
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

    internal fun showInAppReviewIfProceed(activity: Activity) {
        disposables.add(
            showInAppReviewUseCase(activity)
                .subscribeOn(Schedulers.io())
                .subscribe({}, {})
        )
    }
}