package es.etologic.mahjongscoring2.app.new_game;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import es.etologic.mahjongscoring2.app.base.BaseViewModel;
import es.etologic.mahjongscoring2.domain.entities.Player;
import es.etologic.mahjongscoring2.domain.threading.UseCase;
import es.etologic.mahjongscoring2.domain.threading.UseCaseHandler;
import es.etologic.mahjongscoring2.domain.use_cases.GetGamesUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetPlayersUseCase;

import static es.etologic.mahjongscoring2.app.model.ShowState.HIDE;
import static es.etologic.mahjongscoring2.app.model.ShowState.SHOW;

class NewGameViewModel extends BaseViewModel {

    private final GetPlayersUseCase getPlayersUseCase;
    private MutableLiveData<List<Player>> players = new MutableLiveData<List<Player>>() {};

    NewGameViewModel(UseCaseHandler useCaseHandler, GetPlayersUseCase getPlayersUseCase) {
        super(useCaseHandler);
        this.getPlayersUseCase = getPlayersUseCase;
    }

    LiveData<List<Player>> getPlayers() {
        return players;
    }

    void loadAllPlayers() {
        progressState.setValue(SHOW);
        useCaseHandler.execute(getPlayersUseCase, null,
                new UseCase.UseCaseCallback<GetPlayersUseCase.ResponseValue>() {
                    @Override
                    public void onSuccess(GetPlayersUseCase.ResponseValue response) {
                        players.setValue(response.getPlayers());
                        progressState.setValue(HIDE);
                    }

                    @Override
                    public void onError(String ignored) {
                        players.setValue(new ArrayList<>());
                        progressState.setValue(HIDE);
                    }
                });
    }

    void playersEntered(List<Player> players) {
        progressState.setValue(SHOW);
    }
}
