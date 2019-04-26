package es.etologic.mahjongscoring2.app.game.new_player_dialog;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import es.etologic.mahjongscoring2.domain.use_cases.CreatePlayerUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetAllPlayersUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetPlayerUseCase;

public class NewPlayerDialogViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final GetAllPlayersUseCase getAllPlayersUseCase;
    private final CreatePlayerUseCase createPlayerUseCase;
    private GetPlayerUseCase getPlayerUseCase;

    @Inject
    NewPlayerDialogViewModelFactory(GetAllPlayersUseCase getAllPlayersUseCase, CreatePlayerUseCase createPlayerUseCase,
                                           GetPlayerUseCase getPlayerUseCase) {
        this.getAllPlayersUseCase = getAllPlayersUseCase;
        this.createPlayerUseCase = createPlayerUseCase;
        this.getPlayerUseCase = getPlayerUseCase;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new NewPlayerDialogViewModel(getAllPlayersUseCase, createPlayerUseCase, getPlayerUseCase);
    }
}
