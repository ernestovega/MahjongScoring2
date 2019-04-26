package es.etologic.mahjongscoring2.app.main.old_games;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import es.etologic.mahjongscoring2.domain.use_cases.DeleteGameUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetGamesUseCase;

public class OldGamesViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private GetGamesUseCase getGamesUseCase;
    private DeleteGameUseCase deleteGameUseCase;

    @Inject
    public OldGamesViewModelFactory(GetGamesUseCase getGamesUseCase, DeleteGameUseCase deleteGameUseCase) {
        this.getGamesUseCase = getGamesUseCase;
        this.deleteGameUseCase = deleteGameUseCase;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) { return (T) new OldGamesViewModel(getGamesUseCase, deleteGameUseCase); }
}
