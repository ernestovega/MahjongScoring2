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

package com.etologic.mahjongscoring2.app.crashes

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.etologic.mahjongscoring2.R
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Crashlytics: java.lang.ClassCastException in onViewCreated of
 * - EditNamesDialogFragment (issue 3b62b990d7fe38d5a24d4aee658a90fc)
 * - HandActionsFragment (issue 4dcd9db77dd298714ebe0ec204a8e0f6)
 * - RankingDialogFragment (issue 409cab7e2300bd3edfda21e7804fd46e)
 *
 * After process death, Android restores the open dialog, but the active game id was lost.
 * The game state is still Loading when the dialog reads it.
 * These tests open each dialog while the game state is Loading, which is the same situation.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class DialogsWithoutLoadedGameCrashTest : BaseCrashTest() {

    @Test
    fun editNamesDialog_gameNotLoadedYet_doesNotCrash() {
        assertDialogOpensWithoutGame(R.id.editNamesDialogFragment)
    }

    @Test
    fun handActionsDialog_gameNotLoadedYet_doesNotCrash() {
        assertDialogOpensWithoutGame(R.id.handActionsDialogFragment)
    }

    @Test
    fun rankingDialog_gameNotLoadedYet_doesNotCrash() {
        assertDialogOpensWithoutGame(R.id.rankingDialogFragment)
    }

    @Test
    fun diceDialog_gameNotLoadedYet_doesNotCrash() {
        assertDialogOpensWithoutGame(R.id.diceDialogFragment)
    }

    private fun assertDialogOpensWithoutGame(dialogId: Int) {
        navigate(R.id.gameFragment)
        navigate(dialogId)
        settle()

        assertThat(currentDestinationId()).isEqualTo(dialogId)
    }
}
