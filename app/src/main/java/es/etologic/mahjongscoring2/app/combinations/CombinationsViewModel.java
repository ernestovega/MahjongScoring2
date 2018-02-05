package es.etologic.mahjongscoring2.app.combinations;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import es.etologic.mahjongscoring2.app.base.BaseViewModel;
import es.etologic.mahjongscoring2.domain.entities.Combination;
import es.etologic.mahjongscoring2.domain.threading.UseCase;
import es.etologic.mahjongscoring2.domain.threading.UseCaseHandler;
import es.etologic.mahjongscoring2.domain.use_cases.GetCombinationsUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetFilteredCombinationsUseCase;

import static es.etologic.mahjongscoring2.app.model.ShowState.HIDE;
import static es.etologic.mahjongscoring2.app.model.ShowState.SHOW;

class CombinationsViewModel extends BaseViewModel {

    private final GetCombinationsUseCase getCombinationsUseCase;
    private GetFilteredCombinationsUseCase getFilteredCombinationsUseCase;
    private MutableLiveData<List<Combination>> combinations = new MutableLiveData<List<Combination>>() {};

    CombinationsViewModel(UseCaseHandler useCaseHandler,
                          GetCombinationsUseCase getCombinationsUseCase,
                          GetFilteredCombinationsUseCase getFilteredCombinationsUseCase) {
        super(useCaseHandler);
        this.getCombinationsUseCase = getCombinationsUseCase;
        this.getFilteredCombinationsUseCase = getFilteredCombinationsUseCase;
    }

    LiveData<List<Combination>> getCombinations() {
        return combinations;
    }

    void loadCombinations() {
        progressState.setValue(SHOW);
        useCaseHandler.execute(getCombinationsUseCase, null,
                new UseCase.UseCaseCallback<GetCombinationsUseCase.ResponseValue>() {
                    @Override
                    public void onSuccess(GetCombinationsUseCase.ResponseValue response) {
                        combinations.setValue(response.getCombinations());
                        progressState.setValue(HIDE);
                    }

                    @Override
                    public void onError(String ignored) {
                        combinations.setValue(new ArrayList<>());
                        progressState.setValue(HIDE);
                    }
                });
    }

    void searchCombination(String text) {
        useCaseHandler.execute(getFilteredCombinationsUseCase,
                new GetFilteredCombinationsUseCase.RequestValues(text),
                new UseCase.UseCaseCallback<GetFilteredCombinationsUseCase.ResponseValue>() {
                    @Override
                    public void onSuccess(GetFilteredCombinationsUseCase.ResponseValue response) {
                        combinations.setValue(response.getCombinations());
                        progressState.setValue(HIDE);
                    }

                    @Override
                    public void onError(String ignored) {
                        combinations.setValue(new ArrayList<>());
                        progressState.setValue(HIDE);
                    }
                });
    }
}
