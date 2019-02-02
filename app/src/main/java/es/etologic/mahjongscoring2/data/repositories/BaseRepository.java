package es.etologic.mahjongscoring2.data.repositories;

import android.content.Context;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import es.etologic.mahjongscoring2.data.local_data_source.local.AppDatabase;
import io.reactivex.Single;

@Singleton
public abstract class BaseRepository {

    protected final AppDatabase database;

    BaseRepository(Context context) {
        database = AppDatabase.getInstance(context.getApplicationContext());
    }
}
