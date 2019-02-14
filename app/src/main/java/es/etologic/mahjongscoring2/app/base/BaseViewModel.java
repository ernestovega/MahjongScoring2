package es.etologic.mahjongscoring2.app.base;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import es.etologic.mahjongscoring2.app.model.ShowState;
import io.reactivex.disposables.CompositeDisposable;

public class BaseViewModel extends ViewModel {

    protected CompositeDisposable disposables = new CompositeDisposable();
    protected MutableLiveData<ShowState> progressState = new MutableLiveData<>();
    protected MutableLiveData<String> snackbarMessage = new MutableLiveData<>();
    protected MutableLiveData<Throwable> error = new MutableLiveData<>();

    public LiveData<ShowState> getLocalProgressState() { return progressState; }
    public LiveData<String> getSnackbarMessage() { return snackbarMessage; }
    public LiveData<Throwable> getError() { return error; }

    @Override protected void onCleared() {
        if(!disposables.isDisposed()) {
            disposables.dispose();
        }
        super.onCleared();
    }
}
