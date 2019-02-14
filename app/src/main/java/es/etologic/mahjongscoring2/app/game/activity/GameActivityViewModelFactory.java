package es.etologic.mahjongscoring2.app.game.activity;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import es.etologic.mahjongscoring2.domain.use_cases.GetGameUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetGamesWithRoundsUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetRoundsUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.UpdateRoundsUseCase;

public class GameActivityViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final GetGamesWithRoundsUseCase getGamesWithRoundsUseCase;
    private UpdateRoundsUseCase updateRoundUseCase;

    @Inject
    GameActivityViewModelFactory(GetGamesWithRoundsUseCase getGamesWithRoundsUseCase, UpdateRoundsUseCase updateRoundUseCase) {
        this.getGamesWithRoundsUseCase = getGamesWithRoundsUseCase;
        this.updateRoundUseCase = updateRoundUseCase;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new GameActivityViewModel(getGamesWithRoundsUseCase, updateRoundUseCase);
    }
}
