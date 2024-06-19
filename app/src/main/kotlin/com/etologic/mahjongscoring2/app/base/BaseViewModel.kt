/*
 *     Copyright © 2024  Ernesto Vega de la Iglesia Soria
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

package com.etologic.mahjongscoring2.app.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.etologic.mahjongscoring2.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

open class BaseViewModel : ViewModel() {

    private var _error = MutableStateFlow<Throwable?>(null)
    val errorFlow: Flow<Throwable?> = _error

    fun showError(throwable: Throwable) {
        viewModelScope.launch {
            _error.emit(throwable)

            if (BuildConfig.DEBUG) {
                throwable.printStackTrace()
            }
        }
    }
}
