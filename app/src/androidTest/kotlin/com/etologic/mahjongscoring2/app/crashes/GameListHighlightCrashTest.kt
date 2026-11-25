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

import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.screens.game.GameFragment.GamePages
import com.etologic.mahjongscoring2.app.screens.game.GamePageActions
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Crashlytics: java.lang.NullPointerException, null cannot be cast to ItemViewHolder,
 * in GameListFragment.pageToShowObserver (issue f17b84de8f06bf9fe63bd0d87bd65c4a).
 *
 * The list highlights the last round. When that row is not laid out,
 * findViewHolderForAdapterPosition() returns null.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class GameListHighlightCrashTest : BaseCrashTest() {

    @Test
    fun gameList_highlightLastRoundNotLaidOut_doesNotCrash() {
        openGame(createGame(draws = 10))
        settle()
        // Leave space for one row only (as on a small screen or with a big font) and show the first row.
        // Then stop the layout, so the list cannot scroll and the row of the last round is not laid out.
        // This forces a state that happens only sometimes on real devices.
        val list = onActivity { it.findViewById<RecyclerView>(R.id.rvGameList) }
        onActivity {
            list.setPadding(0, list.height * 9 / 10, 0, 0)
            list.scrollToPosition(0)
        }
        waitForIdle()
        onActivity { list.suppressLayout(true) }

        val viewModel = gameViewModel()
        onActivity { viewModel.showPage(GamePages.LIST, GamePageActions.HIGHLIGHT_LAST_ROUND) }
        settle()
        onActivity { list.suppressLayout(false) }

        assertThat(currentDestinationId()).isEqualTo(R.id.gameFragment)
    }
}
