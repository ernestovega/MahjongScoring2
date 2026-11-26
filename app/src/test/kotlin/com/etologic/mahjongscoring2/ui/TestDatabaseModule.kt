package com.etologic.mahjongscoring2.ui

import android.content.Context
import androidx.room.Room
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.AppDataBaseModule
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.AppDatabase
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.daos.GamesDao
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.daos.RoundsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

/** Replaces the on-disk database with an in-memory one for every Hilt UI test. */
@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [AppDataBaseModule::class])
object TestDatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

    @Provides
    fun provideGamesDao(database: AppDatabase): GamesDao = database.gamesDao

    @Provides
    fun provideRoundsDao(database: AppDatabase): RoundsDao = database.roundsDao
}
