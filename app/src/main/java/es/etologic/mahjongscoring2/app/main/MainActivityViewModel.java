package es.etologic.mahjongscoring2.app.main;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.v7.widget.Toolbar;

import es.etologic.mahjongscoring2.app.model.MainScreens;

public class MainActivityViewModel extends ViewModel {

    //FIELDS
    private MutableLiveData<MainScreens> currentScreen = new MutableLiveData<MainScreens>() {};
    private MutableLiveData<Long> currentGame = new MutableLiveData<Long>() {};
    private MutableLiveData<Toolbar> currentToolbar = new MutableLiveData<Toolbar>() {};

    //GETTERS (OBSERVABLES)
    MutableLiveData<MainScreens> getCurrentScreen() { return currentScreen; }
    MutableLiveData<Long> getCurrentGame() { return currentGame; }
    public MutableLiveData<Toolbar> getToolbar() { return currentToolbar; }

    //METHODS
    public void goToScreen(MainScreens screen) { currentScreen.setValue(screen); }
    public void goToGame(long gameId) { currentGame.setValue(gameId); }
    public void setToolbar(Toolbar toolbar) { currentToolbar.setValue(toolbar); }
}
