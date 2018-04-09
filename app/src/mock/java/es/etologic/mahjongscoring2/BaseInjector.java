package es.etologic.mahjongscoring2;

import android.content.Context;
import android.support.annotation.NonNull;

import es.etologic.mahjongscoring2.repository.MockLocalDataSource;

public class BaseInjector {

    @SuppressWarnings ("unused")
    static DataProvider provideDataSource(@NonNull Context context) {
        return DataProvider.getInstance(new MockLocalDataSource());
    }
}
