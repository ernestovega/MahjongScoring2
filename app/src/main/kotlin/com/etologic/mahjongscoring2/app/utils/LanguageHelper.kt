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

package com.etologic.mahjongscoring2.app.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.screens.MainActivity
import com.etologic.mahjongscoring2.data_source.repositories.LanguageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

const val CHINESE = "zh"
const val DUTCH = "nl"
const val ENGLISH = "en"
const val FRENCH = "fr"
const val SPANISH = "es"
const val ITALIAN = "it"

@Singleton
class LanguageHelper @Inject constructor(
    private val languageRepository: LanguageRepository,
) {
    var currentLanguage: String = ENGLISH

    init {
        CoroutineScope(Dispatchers.Unconfined).launch {
            languageRepository.get().collect {
                currentLanguage = it
            }
        }
    }

    suspend fun getCurrentLanguage(): String =
        languageRepository.get().firstOrNull() ?: ENGLISH

    fun changeLanguage(language: String, mainActivity: MainActivity) {
        with(mainActivity) {
            lifecycleScope.launch {
                languageRepository.save(language)
                    .onSuccess {
                        mainActivity.restartMainActivity()
                    }
            }
        }
    }
}

fun Context.setLocale(language: String): Context {
    val locale = when (language) {
        CHINESE -> Locale.CHINESE
        DUTCH -> Locale("nl", "")
        ENGLISH -> Locale.ENGLISH
        FRENCH -> Locale.FRENCH
        SPANISH -> Locale("es", "")
        ITALIAN -> Locale.ITALIAN
        else -> Locale.ENGLISH
    }
    Locale.setDefault(locale)

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        resources.configuration.setLocale(locale)
        this.createConfigurationContext(resources.configuration)
    } else {
        @Suppress("DEPRECATION")
        resources.configuration.locale = locale
        @Suppress("DEPRECATION")
        resources.updateConfiguration(resources.configuration, resources.displayMetrics)
        this
    }
}

fun Activity.goToChangeLanguage(languageHelper: LanguageHelper) {
    AlertDialog.Builder(this, R.style.AlertDialogStyleMM)
        .setTitle(R.string.choose_language)
        .setSingleChoiceItems(
            /* items = */
            arrayOf(
                getString(R.string.chinese),
                getString(R.string.dutch),
                getString(R.string.english),
                getString(R.string.french),
                getString(R.string.spanish),
                getString(R.string.italian), // We cant follow alphabetical order or the list gets wrongly selected.
            ),
            /* checkedItem = */
            when (languageHelper.currentLanguage) {
                CHINESE -> 0
                DUTCH -> 1
                ENGLISH -> 2
                FRENCH -> 3
                SPANISH -> 4
                ITALIAN -> 5
                else -> 2
            },
        )
        /* listener = */ { dialog, newSelectedItem ->
            languageHelper.changeLanguage(
                when (newSelectedItem) {
                    0 -> CHINESE
                    1 -> DUTCH
                    2 -> ENGLISH
                    3 -> FRENCH
                    4 -> SPANISH
                    5 -> ITALIAN
                    else -> ENGLISH
                },
                this as MainActivity,
            )
            dialog.dismiss()
        }
        .setNegativeButton(R.string.close, null)
        .create()
        .show()
}