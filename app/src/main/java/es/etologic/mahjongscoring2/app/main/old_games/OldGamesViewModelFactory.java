package es.etologic.mahjongscoring2.app.main.old_games;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import es.etologic.mahjongscoring2.domain.use_cases.DeleteGameUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetGamesWithRoundsUseCase;

public class OldGamesViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private GetGamesWithRoundsUseCase getGamesWithRoundsUseCase;
    private DeleteGameUseCase deleteGameUseCase;

    @Inject
    public OldGamesViewModelFactory(GetGamesWithRoundsUseCase getGamesWithRoundsUseCase, DeleteGameUseCase deleteGameUseCase) {
        this.getGamesWithRoundsUseCase = getGamesWithRoundsUseCase;
        this.deleteGameUseCase = deleteGameUseCase;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) { return (T) new OldGamesViewModel(getGamesWithRoundsUseCase, deleteGameUseCase); }
}
