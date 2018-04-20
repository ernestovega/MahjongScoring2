package es.etologic.mahjongscoring2.data.repositories;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import java.util.List;

import es.etologic.mahjongscoring2.data.local_data_source.local.AppDatabase;

public abstract class BaseRepository<T> {

    protected final AppDatabase database;

    BaseRepository(Context context) {
        database = AppDatabase.getInstance(context.getApplicationContext());
    }

    public abstract long insertOne(T entity);

    public abstract LiveData<List<T>> getAll();

    public abstract boolean updateOne(T entity);

    public abstract boolean deleteAll();
}
