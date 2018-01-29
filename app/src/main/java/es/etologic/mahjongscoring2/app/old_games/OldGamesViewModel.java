package es.etologic.mahjongscoring2.app.old_games;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import es.etologic.mahjongscoring2.app.base.BaseViewModel;
import es.etologic.mahjongscoring2.domain.entities.Game;
import es.etologic.mahjongscoring2.domain.threading.UseCase;
import es.etologic.mahjongscoring2.domain.threading.UseCaseHandler;
import es.etologic.mahjongscoring2.domain.use_cases.GetGamesUseCase;

import static es.etologic.mahjongscoring2.app.model.ShowState.HIDE;
import static es.etologic.mahjongscoring2.app.model.ShowState.SHOW;

class OldGamesViewModel extends BaseViewModel {

    private final GetGamesUseCase getOldGamesUseCase;
    private MutableLiveData<List<Game>> oldGames = new MutableLiveData<List<Game>>() {};

    OldGamesViewModel(UseCaseHandler useCaseHandler, GetGamesUseCase getOldGamesUseCase) {
        super(useCaseHandler);
        this.getOldGamesUseCase = getOldGamesUseCase;
    }

    void loadGames() {
        progressState.setValue(SHOW);
        useCaseHandler.execute(getOldGamesUseCase, null,
                new UseCase.UseCaseCallback<GetGamesUseCase.ResponseValue>() {
                    @Override
                    public void onSuccess(GetGamesUseCase.ResponseValue response) {
                        oldGames.setValue(response.getGames());
                        progressState.setValue(HIDE);
                    }

                    @Override
                    public void onError(String ignored) {
                        oldGames.setValue(new ArrayList<>());
                        progressState.setValue(HIDE);
                    }
                });
    }

    LiveData<List<Game>> getOldGames() {
        return oldGames;
    }
}
