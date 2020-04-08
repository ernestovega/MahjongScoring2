/*
    Copyright (C) 2020  Ernesto Vega de la Iglesia Soria

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
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
