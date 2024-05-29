/*
 *     Copyright © 2023  Ernesto Vega de la Iglesia Soria
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
import com.etologic.mahjongscoring2.R.anim.enter_from_left
import com.etologic.mahjongscoring2.R.anim.enter_from_right
import com.etologic.mahjongscoring2.R.anim.exit_to_left
import com.etologic.mahjongscoring2.R.anim.exit_to_right
import com.etologic.mahjongscoring2.R.id.frameLayoutMain
import com.etologic.mahjongscoring2.app.game.activity.GameActivity
import com.etologic.mahjongscoring2.app.game.activity.GameActivity.Companion.KEY_GAME_ID
import com.etologic.mahjongscoring2.app.main.activity.MainViewModel.MainScreens
import com.etologic.mahjongscoring2.app.main.activity.MainViewModel.MainScreens.COMBINATIONS
import com.etologic.mahjongscoring2.app.main.activity.MainViewModel.MainScreens.DIFFS_CALCULATOR
import com.etologic.mahjongscoring2.app.main.activity.MainViewModel.MainScreens.GAME
import com.etologic.mahjongscoring2.app.main.activity.MainViewModel.MainScreens.OLD_GAMES
import com.etologic.mahjongscoring2.app.main.activity.MainViewModel.MainScreens.SETUP_NEW_GAME
import com.etologic.mahjongscoring2.app.main.combinations.CombinationsActivity
import com.etologic.mahjongscoring2.app.main.dialogs.setup_new_game.SetupNewGameDialogFragment
import com.etologic.mahjongscoring2.app.main.diffs_calculator.DiffsCalculatorActivity
import com.etologic.mahjongscoring2.app.main.old_games.OldGamesFragment
import com.google.android.material.snackbar.Snackbar

object MainNavigator {

    private const val GREEN_BOOK_ENGLISH_URL = "http://mahjong-europe.org/portal/images/docs/mcr_EN.pdf"
    private const val GREEN_BOOK_SPANISH_URL = "http://mahjong-europe.org/portal/images/docs/GreenBookTranslatedintoSpanishbyIvanMaestreRos.pdf"
    private const val MAHJONG_MADRID_URL = "https://www.mahjongmadrid.com"
    private const val EMA_URL = "http://mahjong-europe.org/"
    private const val EMAIL_SUBJECT = "Mahjong Scoring ${BuildConfig.VERSION_NAME}"
    private const val MAHJONG_MADRID_EMAIL_ADDRESS = "mahjongmadrid@gmail.com"
    private const val APP_SUPPORT_EMAIL_ADDRESS = "ernestovega85@gmail.com"

    fun goToScreen(screen: MainScreens, activity: MainActivity, viewModel: MainViewModel) {
        when (screen) {
            OLD_GAMES -> goToOldGames(activity)
            SETUP_NEW_GAME -> SetupNewGameDialogFragment().show(activity.supportFragmentManager, SetupNewGameDialogFragment.TAG)
            GAME -> goToGame(activity, viewModel)
            COMBINATIONS -> goToCombinations(activity)
            DIFFS_CALCULATOR -> activity.startActivity(Intent(activity, DiffsCalculatorActivity::class.java))
        }
    }

    private fun goToOldGames(activity: MainActivity) {
        activity.supportFragmentManager.beginTransaction().apply {
            if (isEmpty) {
                setCustomAnimations(enter_from_left, exit_to_right, enter_from_right, exit_to_left)
                add(frameLayoutMain, OldGamesFragment(), OldGamesFragment.TAG)
                commit()
            }
        }
    }

    private fun goToGame(activity: MainActivity, viewModel: MainViewModel) {
        activity.gameActivityResultLauncher.launch(
            Intent(activity, GameActivity::class.java).apply {
                putExtra(KEY_GAME_ID, viewModel.activeGameId)
            }
        )
    }

    private fun goToCombinations(activity: MainActivity) {
        activity.startActivity(Intent(activity, CombinationsActivity::class.java))
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

    fun Activity.goToWebsite(url: String) {
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
}
