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

package com.etologic.mahjongscoring2.app.base

import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.screens.game.GameViewModel
import com.etologic.mahjongscoring2.app.screens.game.dialogs.hand_actions.HandActionsDialogFragment
import com.etologic.mahjongscoring2.app.screens.game.dialogs.hand_actions.HandActionsViewPagerAdapter

abstract class BaseGameHandActionsDialogFragment : Fragment() {

    protected val gameViewModel: GameViewModel by hiltNavGraphViewModels(R.id.nav_graph_game)

    protected fun Fragment.showPage(page: HandActionsViewPagerAdapter.HandActions) {
        (parentFragment as? HandActionsDialogFragment)?.showPage(page)
    }

    protected fun Fragment.dismissDialog() {
        (parentFragment as? HandActionsDialogFragment)?.dismiss()
    }
}