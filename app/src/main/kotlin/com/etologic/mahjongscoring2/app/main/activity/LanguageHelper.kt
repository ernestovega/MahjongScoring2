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

package com.etologic.mahjongscoring2.app.main.activity

import android.content.Intent
import androidx.lifecycle.lifecycleScope
import com.etologic.mahjongscoring2.data_source.repositories.LanguageRepository
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageHelper @Inject constructor(
    private val languageRepository: LanguageRepository,
) {
    fun getCurrentLanguage(): String = languageRepository.get()

    fun changeLanguage(language: String, activity: MainActivity) {
        with(activity) {
            lifecycleScope.launch {
                languageRepository.save(language)
                    .onSuccess {
                        startActivity(Intent(applicationContext, MainActivity::class.java))
                        finish()
                    }
            }
        }
    }
}

fun String.toLocale(): Locale = Locale(this)