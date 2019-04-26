package es.etologic.mahjongscoring2.app.main.activity;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.v7.widget.Toolbar;

import io.reactivex.annotations.Nullable;

public class MainActivityViewModel extends ViewModel {

    //ENUM
    public enum MainScreens {
        OLD_GAMES,
        NEW_GAME,
        COMBINATIONS,
        GREEN_BOOK,
        RATE,
        CONTACT,
        FINISH
    }

    //FIELDS
    private MutableLiveData<MainScreens> currentScreen = new MutableLiveData<>();
    private MutableLiveData<Long> currentGame = new MutableLiveData<>();
    private MutableLiveData<Toolbar> currentToolbar = new MutableLiveData<>();

    //OBSERVABLES
    LiveData<MainScreens> getCurrentScreen() { return currentScreen; }
    LiveData<Long> getCurrentGame() { return currentGame; }
    public LiveData<Toolbar> getToolbar() { return currentToolbar; }
    //SETTERS
    public void setToolbar(Toolbar toolbar) { currentToolbar.postValue(toolbar); }

    //METHODS
    public void navigateTo(MainScreens screen) {
        if(currentScreen.getValue() != screen) {
            currentScreen.postValue(screen);
        }
    }
    public void startGame(Long gameId) {
        currentGame.postValue(gameId);
    }
}
