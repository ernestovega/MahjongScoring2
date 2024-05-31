/*
 *     Copyright © 2024  Ernesto Vega de la Iglesia Soria
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.etologic.mahjongscoring2.business.use_cases.di

import com.etologic.mahjongscoring2.app.main.activity.LanguageHelper
import com.etologic.mahjongscoring2.data_source.repositories.LanguageRepository
import com.etologic.mahjongscoring2.data_source.repositories.games.DefaultGamesRepository
import com.etologic.mahjongscoring2.data_source.repositories.games.GamesRepository
import com.etologic.mahjongscoring2.data_source.repositories.rounds.DefaultRoundsRepository
import com.etologic.mahjongscoring2.data_source.repositories.rounds.RoundsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface Module {

    @Binds
    fun provideGamesRepository(gamesRepository: DefaultGamesRepository): GamesRepository

    @Binds
    fun provideRoundsRepository(roundsRepository: DefaultRoundsRepository): RoundsRepository

    companion object {
        @Provides
        @Singleton
        fun provideLanguageHelper(languageRepository: LanguageRepository): LanguageHelper {
            return LanguageHelper(languageRepository)
        }
    }
}