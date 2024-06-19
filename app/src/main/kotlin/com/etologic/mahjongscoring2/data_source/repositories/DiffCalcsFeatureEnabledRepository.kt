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
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.etologic.mahjongscoring2.data_source.local_data_sources.datastore.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiffCalcsFeatureEnabledRepository @Inject constructor(
    @ApplicationContext val context: Context,
) {
    companion object {
        private val KEY_IS_DIFFS_CALCS_FEATURE_ENABLED = booleanPreferencesKey("isDiffsCalcsFeatureEnabled")
    }

        val diffCalcsFeatureEnabledFlow: Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[KEY_IS_DIFFS_CALCS_FEATURE_ENABLED] ?: true
        }

    suspend fun save(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_IS_DIFFS_CALCS_FEATURE_ENABLED] = isEnabled
        }
    }
}