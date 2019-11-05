package es.etologic.mahjongscoring2.data.repositories

import android.content.Context
import es.etologic.mahjongscoring2.data.local_data_source.local.AppDatabase
import javax.inject.Singleton

@Singleton
abstract class BaseRepository internal constructor(context: Context) {
    
    protected val database: AppDatabase? = AppDatabase.getInstance(context.applicationContext)
}
