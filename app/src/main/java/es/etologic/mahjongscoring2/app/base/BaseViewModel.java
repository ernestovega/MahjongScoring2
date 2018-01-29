package es.etologic.mahjongscoring2.app.base;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import es.etologic.mahjongscoring2.app.model.ShowState;
import es.etologic.mahjongscoring2.domain.threading.UseCaseHandler;

public class BaseViewModel extends ViewModel {

    protected final UseCaseHandler useCaseHandler;
    protected MutableLiveData<ShowState> progressState = new MutableLiveData<ShowState>() {};
    protected MutableLiveData<String> snackbarMessage = new MutableLiveData<String>() {};

    protected BaseViewModel(UseCaseHandler useCaseHandler) {
        this.useCaseHandler = useCaseHandler;
    }

    public LiveData<ShowState> getProgressState() {
        return progressState;
    }

    public LiveData<String> getSnackbarMessage() {
        return snackbarMessage;
    }
}
