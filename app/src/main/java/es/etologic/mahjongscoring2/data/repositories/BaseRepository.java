package es.etologic.mahjongscoring2.data.repositories;

import android.content.Context;

import java.util.List;

import es.etologic.mahjongscoring2.data.local_data_source.local.AppDatabase;
import es.etologic.mahjongscoring2.domain.entities.Game;

public abstract class BaseRepository<T> {

    protected final AppDatabase database;

    BaseRepository(Context context) {
        database = AppDatabase.getInstance(context.getApplicationContext());
    }

    public abstract long insertOne(T entity);

    public abstract List<T> getAll();

    public abstract T getOne(long id);

    public abstract boolean updateOne(T entity);

    public abstract boolean deleteOne(long entityId);

    public abstract long deleteAll();
}
