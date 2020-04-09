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
package com.etologic.mahjongscoring2.app.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.etologic.mahjongscoring2.BuildConfig
import io.reactivex.disposables.CompositeDisposable

open class BaseViewModel : ViewModel() {
    
    protected var disposables = CompositeDisposable()
    
    @Suppress("MemberVisibilityCanBePrivate")
    protected var snackbarMessage = MutableLiveData<String>()
    internal fun getSnackbarMessage(): LiveData<String> = snackbarMessage
    private var error = MutableLiveData<Throwable>()
    internal fun getError(): LiveData<Throwable> = error
    
    internal fun showError(throwable: Throwable) {
        error.postValue(throwable)
        if (BuildConfig.DEBUG)
            throwable.printStackTrace()
    }
    
    override fun onCleared() {
        if (!disposables.isDisposed)
            disposables.dispose()
        super.onCleared()
    }
}
