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
package com.etologic.mahjongscoring2.app.screens

import android.app.Activity
import android.content.Intent
import android.content.Intent.ACTION_SENDTO
import android.content.Intent.ACTION_VIEW
import android.content.Intent.FLAG_ACTIVITY_MULTIPLE_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_DOCUMENT
import android.content.Intent.FLAG_ACTIVITY_NO_HISTORY
import android.net.Uri
import com.etologic.mahjongscoring2.BuildConfig
import com.etologic.mahjongscoring2.R

private const val GREEN_BOOK_ENGLISH_URL = "http://mahjong-europe.org/portal/images/docs/mcr_EN.pdf"
private const val GREEN_BOOK_SPANISH_URL = "http://mahjong-europe.org/portal/images/docs/GreenBookTranslatedintoSpanishbyIvanMaestreRos.pdf"
private const val MAHJONG_MADRID_URL = "https://www.mahjongmadrid.com"
private const val EMA_URL = "http://mahjong-europe.org/"
private const val EMAIL_SUBJECT = "Mahjong Scoring ${BuildConfig.VERSION_NAME}"
private const val MAHJONG_MADRID_EMAIL_ADDRESS = "mahjongmadrid@gmail.com"
private const val APP_SUPPORT_EMAIL_ADDRESS = "ernestovega85@gmail.com"

fun Activity.goToGreenBookEnglish() = goToGreenBook(GREEN_BOOK_ENGLISH_URL)

fun Activity.goToGreenBookSpanish() = goToGreenBook(GREEN_BOOK_SPANISH_URL)

fun Activity.goToWebsiteMM() = goToWebsite(MAHJONG_MADRID_URL)

fun Activity.goToWebsiteEMA() = goToWebsite(EMA_URL)

fun MainActivity.goToContactMM() = goToContact(MAHJONG_MADRID_EMAIL_ADDRESS)

fun MainActivity.goToContactSupport() = goToContact(APP_SUPPORT_EMAIL_ADDRESS)

fun MainActivity.goToPickFileToImport() = goToPickFile("*/*")

private fun Activity.goToGreenBook(url: String) {
    val intent = Intent(ACTION_VIEW, Uri.parse(url))
    startActivity(intent)
}

private fun Activity.goToWebsite(url: String) {
    val intent = Intent(ACTION_VIEW, Uri.parse(url))
    with(intent) {
        addFlags(FLAG_ACTIVITY_NO_HISTORY or FLAG_ACTIVITY_NEW_DOCUMENT or FLAG_ACTIVITY_MULTIPLE_TASK)
        startActivity(this)
    }
}

private fun MainActivity.goToContact(url: String) {
    with(Intent(ACTION_SENDTO)) {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf(url))
        putExtra(Intent.EXTRA_SUBJECT, EMAIL_SUBJECT)
        val intentChooser = Intent.createChooser(this, getString(R.string.send_email))
        startActivity(intentChooser)
    }
}

private fun MainActivity.goToPickFile(mimeType: String) {
    pickFileResultLauncher.launch(
        Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = mimeType
        }
    )
}
