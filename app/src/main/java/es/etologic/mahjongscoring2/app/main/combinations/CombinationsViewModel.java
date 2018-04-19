package es.etologic.mahjongscoring2.app.main.combinations;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.List;

import es.etologic.mahjongscoring2.app.base.BaseViewModel;
import es.etologic.mahjongscoring2.domain.entities.Combination;
import es.etologic.mahjongscoring2.domain.operation_objects.BaseError;
import es.etologic.mahjongscoring2.domain.operation_objects.OperationResult;
import es.etologic.mahjongscoring2.domain.use_cases.GetAllCombinationsUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetFilteredCombinationsUseCase;

import static es.etologic.mahjongscoring2.app.model.ShowState.HIDE;
import static es.etologic.mahjongscoring2.app.model.ShowState.SHOW;
import static es.etologic.mahjongscoring2.domain.operation_objects.OperationState.SUCCESS;

class CombinationsViewModel extends BaseViewModel {

    //FIELDS
    private final GetAllCombinationsUseCase getAllCombinationsUseCase;
    private final GetFilteredCombinationsUseCase getFilteredCombinationsUseCase;
    private MutableLiveData<List<Combination>> combinations = new MutableLiveData<>();

    //CONSTRUCTOR
    CombinationsViewModel(GetAllCombinationsUseCase getAllCombinationsUseCase, GetFilteredCombinationsUseCase getFilteredCombinationsUseCase) {
        this.getAllCombinationsUseCase = getAllCombinationsUseCase;
        this.getFilteredCombinationsUseCase = getFilteredCombinationsUseCase;
    }

    //OBSERVABLES
    LiveData<List<Combination>> getCombinations() { return combinations; }

    void loadCombinations() {
        progressState.setValue(SHOW);
        OperationResult<List<Combination>, BaseError> operationResult = getAllCombinationsUseCase.execute();
        progressState.setValue(HIDE);
        if(operationResult.getState() == SUCCESS) combinations.setValue(operationResult.getResponse());
        else snackbarMessage.setValue(operationResult.getError().getMessage());
    }

    void searchCombination(String filter) {
        progressState.setValue(SHOW);
        OperationResult<List<Combination>, BaseError> operationResult = getFilteredCombinationsUseCase.execute(filter);
        progressState.setValue(HIDE);
        if(operationResult.getState() == SUCCESS) combinations.setValue(operationResult.getResponse());
        else snackbarMessage.setValue(operationResult.getError().getMessage());
    }
}
