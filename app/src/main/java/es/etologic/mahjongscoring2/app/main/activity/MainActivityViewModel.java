package es.etologic.mahjongscoring2.app.main.activity;

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
    private MutableLiveData<MainScreens> currentScreen = new MutableLiveData<MainScreens>() {
    };
    private MutableLiveData<Long> currentGame = new MutableLiveData<Long>() {
    };
    private MutableLiveData<Toolbar> currentToolbar = new MutableLiveData<Toolbar>() {
    };

    //GETTERS (OBSERVABLES)
    MutableLiveData<MainScreens> getCurrentScreen() {
        return currentScreen;
    }
    MutableLiveData<Long> getCurrentGame() {
        return currentGame;
    }
    public MutableLiveData<Toolbar> getToolbar() {
        return currentToolbar;
    }
    public void setToolbar(Toolbar toolbar) {
        currentToolbar.setValue(toolbar);
    }
    //METHODS
    public void goToScreen(MainScreens screen) {
        currentScreen.setValue(screen);
    }
    public void goBack() {
        switch(Objects.requireNonNull(currentScreen.getValue())) {
            case OLD_GAMES:
                goToScreen(FINISH);
                break;
            case COMBINATIONS:
                goToScreen(OLD_GAMES);
                break;
            default:
                break;
        }
    }
    public void goToGame(long gameId) {
        currentGame.setValue(gameId);
    }
}
