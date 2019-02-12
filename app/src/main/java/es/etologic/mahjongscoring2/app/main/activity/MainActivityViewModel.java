package es.etologic.mahjongscoring2.app.main.activity;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.v7.widget.Toolbar;

import java.util.Objects;

import static es.etologic.mahjongscoring2.app.main.activity.MainActivityViewModel.MainScreens.FINISH;
import static es.etologic.mahjongscoring2.app.main.activity.MainActivityViewModel.MainScreens.OLD_GAMES;

public class MainActivityViewModel extends ViewModel {

    //ENUM
    public enum MainScreens {
        OLD_GAMES,
        NEW_GAME,
        GAME,
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
    public void navigateTo(MainScreens screen) { currentScreen.postValue(screen); }
    public void startGame(long gameId) {
        currentGame.postValue(gameId);
    }
}
