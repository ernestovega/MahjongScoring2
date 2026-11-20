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
 * Crashlytics: java.lang.NullPointerException in getBinding, called from onDismiss in
 * - SetupNewGameDialogFragment (issue 726c7a015ca1437cf882809123731c95)
 * - EditNamesDialogFragment (issue 541e2ff106d77afe5435b146620e6f5d)
 *
 * When the activity is destroyed with the dialog open, onDestroyView() sets the binding to null.
 * Then DialogFragment.onDestroyView() calls onDismiss(), which reads the binding.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class DialogBindingOnDestroyCrashTest : BaseCrashTest() {

    @Test
    fun setupNewGameDialog_activityRecreated_doesNotCrash() {
        navigate(R.id.setupNewGameDialogFragment)
        settle()

        scenario.recreate()
        settle()

        assertThat(currentDestinationId()).isEqualTo(R.id.setupNewGameDialogFragment)
    }

    @Test
    fun editNamesDialog_activityRecreated_doesNotCrash() {
        openGame(createGame())
        navigate(R.id.editNamesDialogFragment)
        settle()

        scenario.recreate()
        settle()

        assertThat(currentDestinationId()).isEqualTo(R.id.editNamesDialogFragment)
    }
}
