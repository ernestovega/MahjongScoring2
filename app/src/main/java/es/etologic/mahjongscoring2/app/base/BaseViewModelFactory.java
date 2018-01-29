package es.etologic.mahjongscoring2.app.base;

import android.arch.lifecycle.ViewModelProvider;

import es.etologic.mahjongscoring2.domain.threading.UseCaseHandler;

public class BaseViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    protected final UseCaseHandler useCaseHandler;

    protected BaseViewModelFactory(UseCaseHandler useCaseHandler) {
        this.useCaseHandler = useCaseHandler;
    }
}
