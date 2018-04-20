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
    private MutableLiveData<List<Game>> allGames = new MutableLiveData<>();

    //CONSTRUCTOR
    OldGamesViewModel(GetAllGamesUseCase getAllGamesUseCase, DeleteGameUseCase deleteGameUseCase) {
        this.getAllGamesUseCase = getAllGamesUseCase;
        this.deleteGameUseCase = deleteGameUseCase;
    }

    //OBSERVABLES
    LiveData<List<Game>> getGames() { return allGames; }

    //METHODS
    void bindGames() {
        allGames.setValue(getAllGamesUseCase.execute().getValue());
    }
    void deleteGame(long gameId) {
        progressState.setValue(SHOW);
        OperationResult<Boolean, BaseError> operationResult = deleteGameUseCase.execute(gameId);
        if(operationResult.getState() == SUCCESS) {
            for(Game game : Objects.requireNonNull(allGames.getValue())) {
                if(game.getGameId() == gameId) {
                    allGames.getValue().remove(game);
                    break;
                }
            }
            progressState.setValue(HIDE);
            allGames.setValue(allGames.getValue());
        } else {
            progressState.setValue(HIDE);
            snackbarMessage.setValue(operationResult.getError().getMessage());
        }
    }
}
