package es.etologic.mahjongscoring2;

import android.content.Context;
import android.support.annotation.NonNull;

import es.etologic.mahjongscoring2.data.repository.DataProvider;
import es.etologic.mahjongscoring2.data.repository.local.LocalDataSource;

public class BaseInjector {

    static DataProvider provideDataSource(@NonNull Context context) {
        return DataProvider.getInstance(new LocalDataSource(context));
    }
}
