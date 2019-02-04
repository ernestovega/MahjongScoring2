package es.etologic.mahjongscoring2.app.game.activity;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import es.etologic.mahjongscoring2.data.repositories.GamesRepository;
import es.etologic.mahjongscoring2.data.repositories.RoundsRepository;
import es.etologic.mahjongscoring2.domain.use_cases.GetGameUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetRoundsUseCase;

public class GameActivityViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final GetGameUseCase getGameUseCase;
    private final GetRoundsUseCase getRoundsUseCase;

    @Inject
    GameActivityViewModelFactory(GetGameUseCase getGameUseCase, GetRoundsUseCase getRoundsUseCase) {
        this.getGameUseCase = getGameUseCase;
        this.getRoundsUseCase = getRoundsUseCase;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new GameActivityViewModel(getGameUseCase, getRoundsUseCase);
    }
}
