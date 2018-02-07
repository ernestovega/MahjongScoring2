package es.etologic.mahjongscoring2.app.game.game_main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.Date;

import es.etologic.mahjongscoring2.app.base.BaseViewModel;
import es.etologic.mahjongscoring2.domain.entities.Game;
import es.etologic.mahjongscoring2.domain.threading.UseCase;
import es.etologic.mahjongscoring2.domain.threading.UseCaseHandler;
import es.etologic.mahjongscoring2.domain.use_cases.GetGameUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.UpdateGameUseCase;

import static es.etologic.mahjongscoring2.app.model.ShowState.HIDE;
import static es.etologic.mahjongscoring2.app.model.ShowState.SHOW;

class GameMainViewModel extends BaseViewModel {

    //region Fields

    private final GetGameUseCase getGameUseCase;
    private final UpdateGameUseCase updateGameUseCase;
    private MutableLiveData<Game> game = new MutableLiveData<Game>() {};
    private MutableLiveData<Boolean> gameFinished = new MutableLiveData<>();

    //endregion

    //region Constructor

    GameMainViewModel(UseCaseHandler useCaseHandler, GetGameUseCase getGameUseCase,
                      UpdateGameUseCase updateGameUseCase) {
        super(useCaseHandler);
        this.getGameUseCase = getGameUseCase;
        this.updateGameUseCase = updateGameUseCase;
    }

    //endregion

    //region Observables

    LiveData<Game> getGame() {
        return game;
    }

    LiveData<Boolean> isGameFinished() {
        return gameFinished;
    }

    //endregion

    //region Public

    void loadGame(long gameId) {
        progressState.setValue(SHOW);
        useCaseHandler.execute(getGameUseCase, new GetGameUseCase.RequestValues(gameId),
                new UseCase.UseCaseCallback<GetGameUseCase.ResponseValue>() {
                    @Override
                    public void onSuccess(GetGameUseCase.ResponseValue response) {
                        game.setValue(response.getGame());
                        progressState.setValue(HIDE);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        snackbarMessage.setValue(errorMessage);
                        progressState.setValue(HIDE);
                    }
                });
    }

    void endGame() {
        Game game = this.game.getValue();
        if(game != null) {
            game.setEndDate(new Date());
            useCaseHandler.execute(updateGameUseCase, new UpdateGameUseCase.RequestValues(game),
                    new UseCase.UseCaseCallback<UpdateGameUseCase.ResponseValue>() {
                        @Override
                        public void onSuccess(UpdateGameUseCase.ResponseValue response) {
                            gameFinished.setValue(true);
                        }

                        @Override
                        public void onError(String ignored) {
                            gameFinished.setValue(false);
                        }
                    });
        }
    }

    //endregion

    //region Private

    //endregion
}
