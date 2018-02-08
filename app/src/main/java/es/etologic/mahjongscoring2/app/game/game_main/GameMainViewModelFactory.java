package es.etologic.mahjongscoring2.app.game.game_main;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import es.etologic.mahjongscoring2.app.base.BaseViewModelFactory;
import es.etologic.mahjongscoring2.domain.threading.UseCaseHandler;
import es.etologic.mahjongscoring2.domain.use_cases.GetGameUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.UpdateRoundUseCase;

public class GameMainViewModelFactory extends BaseViewModelFactory {

    private final GetGameUseCase getGameUseCase;
    private final UpdateRoundUseCase updateGameUseCase;

    public GameMainViewModelFactory(UseCaseHandler useCaseHandler, GetGameUseCase getGameUseCase,
                                    UpdateRoundUseCase updateGameUseCase) {
        super(useCaseHandler);
        this.getGameUseCase = getGameUseCase;
        this.updateGameUseCase = updateGameUseCase;
    }

    @SuppressWarnings ("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T)new GameMainViewModel(useCaseHandler, getGameUseCase, updateGameUseCase);
    }
}
