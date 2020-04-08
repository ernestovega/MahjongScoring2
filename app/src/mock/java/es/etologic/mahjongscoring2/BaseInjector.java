package com.etologic.mahjongscoring2;

import android.content.Context;

import com.etologic.mahjongscoring2.repository.MockLocalDataSource;

import androidx.annotation.NonNull;

public class BaseInjector {

    @SuppressWarnings("unused")
    static DataProvider provideDataSource(@NonNull Context context) {
        return DataProvider.getInstance(new MockLocalDataSource());
    }
}
