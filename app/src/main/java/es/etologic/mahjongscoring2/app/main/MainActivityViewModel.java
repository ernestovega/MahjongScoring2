package es.etologic.mahjongscoring2.app.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import es.etologic.mahjongscoring2.app.base.BaseViewModel;
import es.etologic.mahjongscoring2.domain.entities.Game;
import es.etologic.mahjongscoring2.domain.threading.UseCase;
import es.etologic.mahjongscoring2.domain.threading.UseCaseHandler;
import es.etologic.mahjongscoring2.domain.use_cases.DeleteGameUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetGamesUseCase;

import static es.etologic.mahjongscoring2.app.main.MainActivityViewModel.MainScreens.BACK;
import static es.etologic.mahjongscoring2.app.main.MainActivityViewModel.MainScreens.OLD_GAMES;
import static es.etologic.mahjongscoring2.app.model.ShowState.HIDE;
import static es.etologic.mahjongscoring2.app.model.ShowState.SHOW;

public class MainActivityViewModel extends ViewModel {

    public enum MainScreens {
        OLD_GAMES,
        NEW_GAME,
        GAME,
        COMBINATIONS,
        GREEN_BOOK,
        RATE,
        CONTACT,
        BACK
    }

    private MutableLiveData<MainScreens> currentScreen = new MutableLiveData<MainScreens>() {};
    private MutableLiveData<Long> currentGame = new MutableLiveData<Long>() {};

    LiveData<MainScreens> getCurrentScreen() {
        return currentScreen;
    }

    LiveData<Long> getCurrentGame() {
        return currentGame;
    }

    public void goToScreen(MainScreens screen) {
        currentScreen.setValue(screen);
    }

    public void goToGame(long gameId) {
        currentGame.setValue(gameId);
    }
}
