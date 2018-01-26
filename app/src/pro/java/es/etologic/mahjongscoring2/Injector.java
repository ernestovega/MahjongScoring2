package es.etologic.mahjongscoring2;

import android.content.Context;
import android.support.annotation.NonNull;

import es.etologic.mahjongscoring2.app.old_games.OldGamesViewModelFactory;
import es.etologic.mahjongscoring2.data.repository.DataProvider;
import es.etologic.mahjongscoring2.data.repository.local.LocalDataSource;
import es.etologic.mahjongscoring2.domain.threading.UseCaseHandler;
import es.etologic.mahjongscoring2.domain.use_cases.GetGamesUseCase;

public class Injector {

    private static LocalDataSource testLocalDataSource;

    private Injector() {}

    private static UseCaseHandler provideUseCaseHandler() {
        return UseCaseHandler.getInstance();
    }

    private static DataProvider provideDataSource(@NonNull Context context) {
        LocalDataSource localDataSource = testLocalDataSource != null ?
                testLocalDataSource : new LocalDataSource(context);
        return DataProvider.getInstance(localDataSource);
    }

    private static GetGamesUseCase provideGetOldGamesUseCase(@NonNull Context context) {
        return new GetGamesUseCase(provideDataSource(context));
    }

    public static OldGamesViewModelFactory provideOldGamesViewModelFactory(
            @NonNull Context context) {
        return new OldGamesViewModelFactory(
                provideUseCaseHandler(),
                provideGetOldGamesUseCase(context));
    }
}
