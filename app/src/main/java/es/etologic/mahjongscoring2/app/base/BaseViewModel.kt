package es.etologic.mahjongscoring2.app.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.etologic.mahjongscoring2.app.model.ShowState
import es.etologic.mahjongscoring2.domain.model.Round
import io.reactivex.disposables.CompositeDisposable

open class BaseViewModel : ViewModel() {
    
    protected var disposables = CompositeDisposable()
    protected var progressState = MutableLiveData<ShowState>()
    protected var snackbarMessage = MutableLiveData<String>()
    protected var error = MutableLiveData<Throwable>()
    
    internal fun getProgressState(): LiveData<ShowState> = progressState
    internal fun getSnackbarMessage(): LiveData<String> = snackbarMessage
    internal fun getError(): LiveData<Throwable> = error
    
    override fun onCleared() {
        if (!disposables.isDisposed)
            disposables.dispose()
        super.onCleared()
    }
}
