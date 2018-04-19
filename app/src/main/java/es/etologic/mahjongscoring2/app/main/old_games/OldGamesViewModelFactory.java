package es.etologic.mahjongscoring2.app.main.old_games;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import es.etologic.mahjongscoring2.domain.use_cases.DeleteGameUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetAllGamesUseCase;

public class OldGamesViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private GetAllGamesUseCase getAllGamesUseCase;
    private DeleteGameUseCase deleteGameUseCase;

    public OldGamesViewModelFactory(GetAllGamesUseCase getAllGamesUseCase, DeleteGameUseCase deleteGameUseCase) {
        this.getAllGamesUseCase = getAllGamesUseCase;
        this.deleteGameUseCase = deleteGameUseCase;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) { return (T) new OldGamesViewModel(getAllGamesUseCase, deleteGameUseCase); }
}
