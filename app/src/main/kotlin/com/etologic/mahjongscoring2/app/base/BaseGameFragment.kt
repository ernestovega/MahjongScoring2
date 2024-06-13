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

import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.etologic.mahjongscoring2.app.screens.game.GameFragment
import com.etologic.mahjongscoring2.app.screens.game.GameViewModel
import com.etologic.mahjongscoring2.app.screens.game.dialogs.hand_actions.HandActionsDialogFragment

abstract class BaseGameFragment : Fragment() {

    protected val gameViewModel by viewModels<GameViewModel>(ownerProducer = { requireParentFragment() })
}

abstract class BaseGameDialogFragment : AppCompatDialogFragment() {

    protected val gameViewModel by viewModels<GameViewModel>(ownerProducer = { findParentGameFragment() })

}

private fun Fragment.findParentGameFragment() =
    requireParentFragment().childFragmentManager.fragments.filterIsInstance<GameFragment>().firstOrNull()
        ?: throw IllegalStateException()

abstract class BaseGameHandActionsDialogFragment : Fragment() {

    protected val gameViewModel by viewModels<GameViewModel>(ownerProducer = { requireParentFragment().findParentGameFragment() })

    enum class HandActionsPages(val index: Int) {
        ACTIONS(0),
        HU(1),
        PENALTY(2);
    }

    protected fun Fragment.showPage(page: HandActionsPages) {
        (parentFragment as? HandActionsDialogFragment)?.showPage(page.index)
    }

    protected fun Fragment.dismissDialog() {
        (parentFragment as? HandActionsDialogFragment)?.dismiss()
    }
}
