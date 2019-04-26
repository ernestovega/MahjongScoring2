package es.etologic.mahjongscoring2.app.game.activity;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import es.etologic.mahjongscoring2.domain.use_cases.CreateGameUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetGamesUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.UpdateRoundsUseCase;

public class GameActivityViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private CreateGameUseCase createGameUseCase;
    private final GetGamesUseCase getGamesUseCase;
    private UpdateRoundsUseCase updateRoundUseCase;


    @Inject
    GameActivityViewModelFactory(CreateGameUseCase createGameUseCase, GetGamesUseCase getGamesUseCase, UpdateRoundsUseCase updateRoundUseCase) {
        this.createGameUseCase = createGameUseCase;
        this.getGamesUseCase = getGamesUseCase;
        this.updateRoundUseCase = updateRoundUseCase;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new GameActivityViewModel(createGameUseCase, getGamesUseCase, updateRoundUseCase);
    }
}
