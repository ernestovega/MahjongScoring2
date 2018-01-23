package es.etologic.mahjongscoring2.data.repository.local;

import android.content.Context;

public class LocalDataSource implements ILocalDataSource {

    //region Fields

    private final Context context;

    //endregion

    //region Constructor

    public LocalDataSource(Context context) {
        this.context = context;
        AppDatabase database = AppDatabase.getInstance(this.context.getApplicationContext());
    }

    //endregion

    //region IRemoteDataSource implementation

    @Override
    public void clearDataBase() {
    }

    //endregion
}