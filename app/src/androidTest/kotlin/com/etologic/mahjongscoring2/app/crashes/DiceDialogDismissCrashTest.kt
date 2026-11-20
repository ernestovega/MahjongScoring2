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

import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.etologic.mahjongscoring2.app.screens.game.dialogs.DiceDialogFragment
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Crashlytics: lateinit property diceSound has not been initialized,
 * in DiceDialogFragment.dismiss (issue ccbc5e7c638618d3e6cc2357011deea8).
 *
 * DialogFragmentNavigator can dismiss a restored dialog before its view is created.
 * The sound is created with the view, so dismiss() read a lateinit property that was not set.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class DiceDialogDismissCrashTest : BaseCrashTest() {

    @Test
    fun diceDialog_dismissedBeforeViewIsCreated_doesNotCrash() {
        val dialog = DiceDialogFragment()
        onActivity { activity ->
            activity.supportFragmentManager.beginTransaction()
                .add(dialog, "dice")
                .setMaxLifecycle(dialog, Lifecycle.State.CREATED)
                .commitNow()
            dialog.dismiss()
        }
        settle()

        assertThat(onActivity { it.supportFragmentManager.findFragmentByTag("dice") }).isNull()
    }
}
