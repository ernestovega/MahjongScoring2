package es.etologic.mahjongscoring2;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import es.etologic.mahjongscoring2.app.combinations.CombinationsViewModelFactory;
import es.etologic.mahjongscoring2.app.main.MainActivity;
import es.etologic.mahjongscoring2.app.main.MainNavigation;
import es.etologic.mahjongscoring2.app.new_game.NewGameViewModelFactory;
import es.etologic.mahjongscoring2.app.old_games.OldGamesViewModelFactory;
import es.etologic.mahjongscoring2.domain.threading.UseCaseHandler;
import es.etologic.mahjongscoring2.domain.use_cases.CreateGameUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.CreatePlayerUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.DeleteGameUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetCombinationsUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetFilteredCombinationsUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetGamesUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetPlayersUseCase;

public class Injector extends BaseInjector {

    private static UseCaseHandler provideUseCaseHandler() {
        return UseCaseHandler.getInstance();
    }

    public static MainNavigation provideMainNavigation(MainActivity mainActivity) {
        return new MainNavigation(mainActivity);
    }

    public static OldGamesViewModelFactory provideOldGamesViewModelFactory(
            @NonNull Context context) {
        return new OldGamesViewModelFactory(provideUseCaseHandler(),
                provideGetOldGamesUseCase(context), provideDeleteGameUseCase(context));
    }

    private static GetGamesUseCase provideGetOldGamesUseCase(@NonNull Context context) {
        return new GetGamesUseCase(provideDataSource(context));
    }

    private static DeleteGameUseCase provideDeleteGameUseCase(Context context) {
        return new DeleteGameUseCase(provideDataSource(context));
    }

    public static ViewModelProvider.Factory provideNewGameViewModelFactory(Context context) {
        return new NewGameViewModelFactory(provideUseCaseHandler(),
                provideGetPlayersUseCase(context), provideCreatePlayerUseCase(context),
                provideCreateGameUseCase(context));
    }

    private static GetPlayersUseCase provideGetPlayersUseCase(Context context) {
        return new GetPlayersUseCase(provideDataSource(context));
    }

    private static CreatePlayerUseCase provideCreatePlayerUseCase(Context context) {
        return new CreatePlayerUseCase(provideDataSource(context));
    }

    private static CreateGameUseCase provideCreateGameUseCase(Context context) {
        return new CreateGameUseCase(provideDataSource(context));
    }

    public static ViewModelProvider.Factory provideCombinationsViewModelFactory(Context context) {
        return new CombinationsViewModelFactory(provideUseCaseHandler(),
                provideGetCombinationsUseCase(context),
                provideGetFilteredCombinationsUseCase(context));
    }

    private static GetCombinationsUseCase provideGetCombinationsUseCase(Context context) {
        return new GetCombinationsUseCase(provideDataSource(context));
    }

    private static GetFilteredCombinationsUseCase provideGetFilteredCombinationsUseCase(Context context) {
        return new GetFilteredCombinationsUseCase(provideDataSource(context));
    }
}
