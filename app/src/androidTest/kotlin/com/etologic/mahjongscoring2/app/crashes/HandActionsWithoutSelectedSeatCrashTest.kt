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

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Crashlytics: java.lang.ArrayIndexOutOfBoundsException (length=4; index=-1) in
 * - HandActionsFragment.setPlayer (issue a87aa95c5c84ee1447d1af8a0a4d194f)
 * - HuFragment.initViews (issue 89588c7f242a5caf06cca1c435c2b888)
 *
 * The dialogs read the selected seat. When no seat is selected (TableWinds.NONE, code -1),
 * the players names array is read at index -1.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class HandActionsWithoutSelectedSeatCrashTest : BaseCrashTest() {

    @Test
    fun handActionsDialog_noSelectedSeat_closesWithoutCrash() {
        openGame(createGame())

        navigate(R.id.handActionsDialogFragment)
        settle()

        assertThat(currentDestinationId()).isEqualTo(R.id.gameFragment)
    }

    @Test
    fun huPage_restoredWithoutSelectedSeat_closesWithoutCrash() {
        openGame(createGame())
        val viewModel = gameViewModel()
        onActivity { viewModel.onSeatClicked(TableWinds.EAST) }
        waitUntil { loadedState().selectedSeat == TableWinds.EAST }
        navigate(R.id.handActionsDialogFragment)
        onView(withId(R.id.btHandActionsDialogHu)).inRoot(isDialog()).perform(click())
        settle()

        // The seat is lost (as after process death) and the dialog is created again.
        onActivity { viewModel.unselectSeats() }
        waitUntil { loadedState().selectedSeat == TableWinds.NONE }
        scenario.recreate()
        settle()

        assertThat(currentDestinationId()).isEqualTo(R.id.gameFragment)
    }
}
