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

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import androidx.appcompat.app.AlertDialog
import com.etologic.mahjongscoring2.R
import java.util.Locale

const val CHINESE = "zh"
const val DUTCH = "nl"
const val ENGLISH = "en"
const val FRENCH = "fr"
const val SPANISH = "es"

@Suppress("DEPRECATION")
fun Context.setLocale(language: String): Context {
    val locale = when (language) {
        CHINESE -> Locale.CHINESE
        DUTCH -> Locale("nl", "")
        ENGLISH -> Locale.ENGLISH
        FRENCH -> Locale.FRENCH
        SPANISH -> Locale("es", "")
        else -> Locale.ENGLISH
    }
    Locale.setDefault(locale)

    val resources: Resources = resources
    val configuration: Configuration = resources.configuration

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        configuration.setLocale(locale)
        createConfigurationContext(configuration)
    } else {
        configuration.locale = locale
        resources.updateConfiguration(configuration, resources.displayMetrics)
        this
    }
}

fun Context.goToChooseLanguage(
    currentLanguage: String,
    changeLanguage: (language: String) -> Unit,
) {
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
            ),
            /* checkedItem = */
            when (currentLanguage) {
                CHINESE -> 0
                DUTCH -> 1
                ENGLISH -> 2
                FRENCH -> 3
                SPANISH -> 4
                else -> -1
            },
        )
        /* listener = */ { dialog, newSelectedItem ->
            changeLanguage(
                when (newSelectedItem) {
                    0 -> CHINESE
                    1 -> DUTCH
                    2 -> ENGLISH
                    3 -> FRENCH
                    4 -> SPANISH
                    else -> Locale.getDefault().language
                }
            )
            dialog.dismiss()
        }
        .setNegativeButton(R.string.cancel, null)
        .create()
        .show()
}