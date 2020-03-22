package com.etologic.mahjongscoring2.app.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.etologic.mahjongscoring2.BuildConfig
import com.etologic.mahjongscoring2.app.model.ShowState
import com.etologic.mahjongscoring2.app.model.ShowState.HIDE
import io.reactivex.disposables.CompositeDisposable

open class BaseViewModel : ViewModel() {
    
    protected var progressState = MutableLiveData<ShowState>()
    internal fun getProgressState(): LiveData<ShowState> = progressState
    protected var snackbarMessage = MutableLiveData<String>()
    internal fun getSnackbarMessage(): LiveData<String> = snackbarMessage
    private var error = MutableLiveData<Throwable>()
    internal fun getError(): LiveData<Throwable> = error
    
    protected var disposables = CompositeDisposable()
    
    override fun onCleared() {
        if (!disposables.isDisposed)
            disposables.dispose()
        super.onCleared()
    }
    
    internal fun showError(throwable: Throwable) {
        progressState.postValue(HIDE)
        error.postValue(throwable)
        if(BuildConfig.DEBUG)
            throwable.printStackTrace()
    }
}
