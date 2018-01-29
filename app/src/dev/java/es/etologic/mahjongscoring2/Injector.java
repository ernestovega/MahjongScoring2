package es.etologic.mahjongscoring2;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;

import es.etologic.mahjongscoring2.app.main.IMainToolbarListener;
import es.etologic.mahjongscoring2.app.main.MainNavigation;
import es.etologic.mahjongscoring2.app.new_game.NewGameViewModelFactory;
import es.etologic.mahjongscoring2.app.old_games.OldGamesViewModelFactory;
import es.etologic.mahjongscoring2.data.repository.DataProvider;
import es.etologic.mahjongscoring2.data.repository.local.LocalDataSource;
import es.etologic.mahjongscoring2.domain.threading.UseCaseHandler;
import es.etologic.mahjongscoring2.domain.use_cases.GetGamesUseCase;
import es.etologic.mahjongscoring2.domain.use_cases.GetPlayersUseCase;

public class Injector {

    private Injector() {}

    private static UseCaseHandler provideUseCaseHandler() {
        return UseCaseHandler.getInstance();
    }

    private static DataProvider provideDataSource(@NonNull Context context) {
        return DataProvider.getInstance(new LocalDataSource(context));
    }

    public static MainNavigation provideMainNavigation(NavigationView navigationView,
                                                       FragmentManager supportFragmentManager,
                                                       IMainToolbarListener iMainToolbarListener) {
        return new MainNavigation(navigationView, supportFragmentManager, iMainToolbarListener);
    }

    public static OldGamesViewModelFactory provideOldGamesViewModelFactory(
            @NonNull Context context) {
        return new OldGamesViewModelFactory(provideUseCaseHandler(),
                provideGetOldGamesUseCase(context));
    }

    private static GetGamesUseCase provideGetOldGamesUseCase(@NonNull Context context) {
        return new GetGamesUseCase(provideDataSource(context));
    }

    public static ViewModelProvider.Factory provideNewGameViewModelFactory(
            @NonNull Context context) {
        return new NewGameViewModelFactory(provideUseCaseHandler(),
                provideGetPlayersUseCase(context));
    }

    private static GetPlayersUseCase provideGetPlayersUseCase(Context context) {
        return new GetPlayersUseCase(provideDataSource(context));
    }
}
