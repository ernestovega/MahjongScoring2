package es.etologic.mahjongscoring2.app.old_games;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.List;

import es.etologic.mahjongscoring2.app.base.BaseViewModel;
import es.etologic.mahjongscoring2.domain.entities.OldGame;
import es.etologic.mahjongscoring2.domain.threading.UseCase;
import es.etologic.mahjongscoring2.domain.threading.UseCaseHandler;
import es.etologic.mahjongscoring2.domain.use_cases.GetOldGamesUseCase;

import static es.etologic.mahjongscoring2.app.model.ShowState.HIDE;
import static es.etologic.mahjongscoring2.app.model.ShowState.SHOW;

class OldGamesViewModel extends BaseViewModel {

    private final GetOldGamesUseCase getOldGamesUseCase;
    private MutableLiveData<List<OldGame>> oldGames = new MutableLiveData<List<OldGame>>() {};

    OldGamesViewModel(UseCaseHandler useCaseHandler,
                      GetOldGamesUseCase getOldGamesUseCase) {
        super(useCaseHandler);
        this.getOldGamesUseCase = getOldGamesUseCase;
    }

    void loadOldGames() {
        progressState.setValue(SHOW);
        useCaseHandler.execute(getOldGamesUseCase, null,
                new UseCase.UseCaseCallback<GetOldGamesUseCase.ResponseValue>() {
                    @Override
                    public void onSuccess(GetOldGamesUseCase.ResponseValue response) {
                        oldGames.setValue(response.getOldGames());
                        progressState.setValue(HIDE);
                    }

                    @Override
                    public void onError(String error) {
                        progressState.setValue(HIDE);
                        snackbarMessage.setValue(error);
                    }
                });
    }

    LiveData<List<OldGame>> getOldGames() {
        return oldGames;
    }
}
