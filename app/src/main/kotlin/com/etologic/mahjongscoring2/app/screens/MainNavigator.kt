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
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.etologic.mahjongscoring2.BuildConfig
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.R.anim.enter_from_left
import com.etologic.mahjongscoring2.R.anim.enter_from_right
import com.etologic.mahjongscoring2.R.anim.exit_to_left
import com.etologic.mahjongscoring2.R.anim.exit_to_right
import com.etologic.mahjongscoring2.R.id.frameLayoutMain
import com.etologic.mahjongscoring2.app.screens.MainViewModel.MainScreens
import com.etologic.mahjongscoring2.app.screens.MainViewModel.MainScreens.COMBINATIONS
import com.etologic.mahjongscoring2.app.screens.MainViewModel.MainScreens.DIFFS_CALCULATOR
import com.etologic.mahjongscoring2.app.screens.MainViewModel.MainScreens.GAME
import com.etologic.mahjongscoring2.app.screens.MainViewModel.MainScreens.OLD_GAMES
import com.etologic.mahjongscoring2.app.screens.combinations.CombinationsFragment
import com.etologic.mahjongscoring2.app.screens.dialogs.setup_new_game.SetupNewGameDialogFragment
import com.etologic.mahjongscoring2.app.screens.diffs_calculator.DiffsCalculatorFragment
import com.etologic.mahjongscoring2.app.screens.game.GameFragment
import com.etologic.mahjongscoring2.app.screens.old_games.OldGamesFragment
import com.google.android.material.snackbar.Snackbar

private const val GREEN_BOOK_ENGLISH_URL = "http://mahjong-europe.org/portal/images/docs/mcr_EN.pdf"
private const val GREEN_BOOK_SPANISH_URL = "http://mahjong-europe.org/portal/images/docs/GreenBookTranslatedintoSpanishbyIvanMaestreRos.pdf"
private const val MAHJONG_MADRID_URL = "https://www.mahjongmadrid.com"
private const val EMA_URL = "http://mahjong-europe.org/"
private const val EMAIL_SUBJECT = "Mahjong Scoring ${BuildConfig.VERSION_NAME}"
private const val MAHJONG_MADRID_EMAIL_ADDRESS = "mahjongmadrid@gmail.com"
private const val APP_SUPPORT_EMAIL_ADDRESS = "ernestovega85@gmail.com"

object MainNavigator {

    fun MainActivity.goToScreen(screen: MainScreens) {
        when (screen) {
            OLD_GAMES -> goToOldGames()
            GAME -> goToGame()
            COMBINATIONS -> goToCombinations()
            DIFFS_CALCULATOR -> goToDiffsCalculator()
        }
    }

    private fun MainActivity.goToOldGames() {
        replaceFragment(OldGamesFragment(), OldGamesFragment.TAG)
    }

    private fun MainActivity.goToDiffsCalculator() {
        replaceFragment(DiffsCalculatorFragment(), DiffsCalculatorFragment.TAG)
    }

    private fun MainActivity.goToCombinations() {
        replaceFragment(CombinationsFragment(), CombinationsFragment.TAG)
    }

    private fun MainActivity.goToGame() {
        replaceFragment(GameFragment(), GameFragment.TAG)
    }

    private fun MainActivity.replaceFragment(fragment: Fragment, tag: String) {
        this.supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(enter_from_left, exit_to_right, enter_from_right, exit_to_left)
            replace(frameLayoutMain, fragment, tag)
            commit()
        }
    }
}

fun Activity.openSetupNewGameDialog() {
    this as AppCompatActivity
    SetupNewGameDialogFragment().show(supportFragmentManager, SetupNewGameDialogFragment.TAG)
}

fun Activity.goToGreenBookEnglish() {
    startActivity(Intent(ACTION_VIEW, Uri.parse(GREEN_BOOK_ENGLISH_URL)))
}

fun Activity.goToGreenBookSpanish() {
    startActivity(Intent(ACTION_VIEW, Uri.parse(GREEN_BOOK_SPANISH_URL)))
}

fun Activity.goToWebsiteMM() {
    goToWebsite(MAHJONG_MADRID_URL)
}

fun Activity.goToWebsiteEMA() {
    goToWebsite(EMA_URL)
}

private fun Activity.goToWebsite(url: String) {
    with(Intent(ACTION_VIEW, Uri.parse(url))) {
        addFlags(FLAG_ACTIVITY_NO_HISTORY or FLAG_ACTIVITY_NEW_DOCUMENT or FLAG_ACTIVITY_MULTIPLE_TASK)
        try {
            startActivity(this)
        } catch (e: Exception) {
            startActivity(Intent(ACTION_VIEW, Uri.parse(url)))
        }
    }
}

fun MainActivity.goToContactMM() {
    goToContact(MAHJONG_MADRID_EMAIL_ADDRESS)
}

fun MainActivity.goToContactSupport() {
    goToContact(APP_SUPPORT_EMAIL_ADDRESS)
}

private fun MainActivity.goToContact(url: String) {
    with(Intent(ACTION_SENDTO)) {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf(url))
        putExtra(Intent.EXTRA_SUBJECT, EMAIL_SUBJECT)
        try {
            startActivity(Intent.createChooser(this, getString(R.string.send_email)))
        } catch (e: Exception) {
            Snackbar.make(binding.drawerLayoutMain, R.string.no_email_apps_founded, Snackbar.LENGTH_LONG).show()
        }
    }
}

fun MainActivity.goToPickFile() {
    pickFileResultLauncher.launch(
        Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
    )
}