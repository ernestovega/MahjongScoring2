package es.etologic.mahjongscoring2.app.main.old_games;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.List;
import java.util.Objects;

import es.etologic.mahjongscoring2.app.base.BaseViewModel;
import es.etologic.mahjongscoring2.domain.entities.Game;
import es.etologic.mahjongscoring2.domain.operation_objects.BaseError;
import es.etologic.mahjongscoring2.domain.operation_objects.OperationResult;
import es.etologic.mahjongscoring2.domain.use_cases.DeleteGameUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetAllGamesUseCase;

import static es.etologic.mahjongscoring2.app.model.ShowState.HIDE;
import static es.etologic.mahjongscoring2.app.model.ShowState.SHOW;
import static es.etologic.mahjongscoring2.domain.operation_objects.OperationState.SUCCESS;

class OldGamesViewModel extends BaseViewModel {

    //FIELDS
    private GetAllGamesUseCase getAllGamesUseCase;
    private DeleteGameUseCase deleteGameUseCase;
    private MutableLiveData<List<Game>> oldGames = new MutableLiveData<>();

    //CONSTRUCTOR
    OldGamesViewModel(GetAllGamesUseCase getAllGamesUseCase, DeleteGameUseCase deleteGameUseCase) {
        this.getAllGamesUseCase = getAllGamesUseCase;
        this.deleteGameUseCase = deleteGameUseCase;
    }

    //OBSERVABLES
    LiveData<List<Game>> getGames() { return oldGames; }

    //METHODS
    void loadGames() {
        progressState.setValue(SHOW);
        OperationResult<List<Game>, BaseError> operationResult = getAllGamesUseCase.execute();
        progressState.setValue(HIDE);
        if(operationResult.getState() == SUCCESS) oldGames.setValue(operationResult.getResponse());
        else snackbarMessage.setValue(operationResult.getError().getMessage());
    }
    void deleteGame(long gameId) {
        progressState.setValue(SHOW);
        OperationResult<Boolean, BaseError> operationResult = deleteGameUseCase.execute(gameId);
        if(operationResult.getState() == SUCCESS) {
            for(Game game : Objects.requireNonNull(oldGames.getValue())) {
                if(game.getGameId() == gameId) {
                    oldGames.getValue().remove(game);
                    break;
                }
            }
            progressState.setValue(HIDE);
            oldGames.setValue(oldGames.getValue());
        } else {
            progressState.setValue(HIDE);
            snackbarMessage.setValue(operationResult.getError().getMessage());
        }
    }
}
