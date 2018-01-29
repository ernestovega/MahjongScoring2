package es.etologic.mahjongscoring2.domain.threading;

/**
 * Created by miguelangel.perez on 24/04/2017.
 */

public class UseCaseWrapper {

    UseCase useCase;
    UseCase.RequestValues requestValues;
    UseCase.UseCaseCallback responseValueUseCaseCallback;

    public UseCaseWrapper(UseCase useCase, UseCase.RequestValues requestValues, UseCase.UseCaseCallback responseValueUseCaseCallback) {
        this.useCase = useCase;
        this.requestValues = requestValues;
        this.responseValueUseCaseCallback = responseValueUseCaseCallback;
    }

    public UseCase getUseCase() {
        return useCase;
    }

    public void setUseCase(UseCase useCase) {
        this.useCase = useCase;
    }

    public UseCase.RequestValues getRequestValues() {
        return requestValues;
    }

    public void setRequestValues(UseCase.RequestValues requestValues) {
        this.requestValues = requestValues;
    }

    public UseCase.UseCaseCallback<UseCase.ResponseValue> getResponseValueUseCaseCallback() {
        return responseValueUseCaseCallback;
    }

    public void setResponseValueUseCaseCallback(UseCase.UseCaseCallback<UseCase.ResponseValue> responseValueUseCaseCallback) {
        this.responseValueUseCaseCallback = responseValueUseCaseCallback;
    }
}


