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
package com.etologic.mahjongscoring2.app.screens.game

import androidx.fragment.app.Fragment
import com.etologic.mahjongscoring2.app.screens.game.dialogs.ActionDialogFragment
import com.etologic.mahjongscoring2.app.screens.game.dialogs.DiceDialogFragment
import com.etologic.mahjongscoring2.app.screens.game.dialogs.EditNamesDialogFragment
import com.etologic.mahjongscoring2.app.screens.game.dialogs.HuDialogFragment
import com.etologic.mahjongscoring2.app.screens.game.dialogs.PenaltyDialogFragment
import com.etologic.mahjongscoring2.app.screens.game.dialogs.RankingDialogFragment

fun Fragment.findGameFragment(): GameFragment =
    this.takeIf { this is GameFragment } as? GameFragment
        ?: this.parentFragment?.findGameFragment()
        ?: throw IllegalStateException()

fun Fragment.openHandActionsDialog() {
    ActionDialogFragment().show(findGameFragment().childFragmentManager, ActionDialogFragment.TAG)
}

fun Fragment.openEditNamesDialog() {
    EditNamesDialogFragment().show(findGameFragment().childFragmentManager, EditNamesDialogFragment.TAG)
}

fun Fragment.openHuDialog() {
    HuDialogFragment().show(findGameFragment().childFragmentManager, HuDialogFragment.TAG)
}

fun Fragment.openPenaltyDialog() {
    PenaltyDialogFragment().show(findGameFragment().childFragmentManager, PenaltyDialogFragment.TAG)
}

fun Fragment.openRankingDialog() {
    RankingDialogFragment().show(findGameFragment().childFragmentManager, RankingDialogFragment.TAG)
}

fun Fragment.openRollDiceDialog() {
    DiceDialogFragment().show(findGameFragment().childFragmentManager, DiceDialogFragment.TAG)
}
