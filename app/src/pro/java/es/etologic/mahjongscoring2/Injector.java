package com.etologic.mahjongscoring2;

import android.content.Context;

import com.etologic.mahjongscoring2.app.main.old_games.OldGamesViewModelFactory;
import com.etologic.mahjongscoring2.business.use_cases.GetGamesUseCase;

import androidx.annotation.NonNull;

public class Injector {

    private static LocalDataSource testLocalDataSource;

    private Injector() {}
    public static OldGamesViewModelFactory provideOldGamesViewModelFactory(
            @NonNull Context context) {
        return new OldGamesViewModelFactory(
                provideUseCaseHandler(),
                provideGetOldGamesUseCase(context));
    }
    private static UseCaseHandler provideUseCaseHandler() {
        return UseCaseHandler.getInstance();
    }
    private static GetGamesUseCase provideGetOldGamesUseCase(@NonNull Context context) {
        return new GetGamesUseCase(provideDataSource(context));
    }
    private static DataProvider provideDataSource(@NonNull Context context) {
        LocalDataSource localDataSource = testLocalDataSource != null ?
                testLocalDataSource : new LocalDataSource(context);
        return DataProvider.getInstance(localDataSource);
    }
}
