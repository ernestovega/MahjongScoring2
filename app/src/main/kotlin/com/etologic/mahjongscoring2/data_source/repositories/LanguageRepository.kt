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

package com.etologic.mahjongscoring2.data_source.repositories

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.etologic.mahjongscoring2.data_source.local_data_sources.datastore.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageRepository @Inject constructor(
    @ApplicationContext val context: Context,
) {
    companion object {
        private val KEY_LANGUAGE = stringPreferencesKey("language")
    }

    fun get(): Flow<String> =
        context.dataStore.data.map { preferences ->
            preferences[KEY_LANGUAGE] ?: Locale.getDefault().language
        }

    suspend fun save(language: String): Result<Unit> {
        context.dataStore.edit { preferences ->
            preferences[KEY_LANGUAGE] = language
        }
        return Result.success(Unit)
    }
}