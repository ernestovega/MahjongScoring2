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
package com.etologic.mahjongscoring2.app.game.activity

import android.app.Activity.RESULT_OK
import android.content.Intent
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel.GameScreens
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel.GameScreens.COMBINATIONS
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel.GameScreens.DICE
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel.GameScreens.EDIT_NAMES
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel.GameScreens.EXIT
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel.GameScreens.HAND_ACTION
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel.GameScreens.HU
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel.GameScreens.PENALTY
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel.GameScreens.RANKING
import com.etologic.mahjongscoring2.app.game.dialogs.edit_names.EditNamesDialogFragment
import com.etologic.mahjongscoring2.app.game.dialogs.hand_actions.ActionDialogFragment
import com.etologic.mahjongscoring2.app.game.dialogs.hand_actions.HuDialogFragment
import com.etologic.mahjongscoring2.app.game.dialogs.hand_actions.PenaltyDialogFragment
import com.etologic.mahjongscoring2.app.game.dialogs.ranking.RankingDialogFragment
import com.etologic.mahjongscoring2.app.game.dialogs.roll_dice.RollDiceDialogFragment
import com.etologic.mahjongscoring2.app.main.activity.MainActivity.Companion.KEY_WAS_GAME_ENDED
import com.etologic.mahjongscoring2.app.main.combinations.CombinationsActivity

object GameNavigator {

    fun navigateTo(screen: GameScreens, activity: GameActivity, viewModel: GameViewModel) {
        when (screen) {
            COMBINATIONS -> activity.startActivity(Intent(activity, CombinationsActivity::class.java))
            EDIT_NAMES -> EditNamesDialogFragment().show(activity.supportFragmentManager, EditNamesDialogFragment.TAG)
            DICE -> RollDiceDialogFragment().show(activity.supportFragmentManager, RollDiceDialogFragment.TAG)
            HAND_ACTION -> ActionDialogFragment().show(activity.supportFragmentManager, ActionDialogFragment.TAG)
            HU -> HuDialogFragment().show(activity.supportFragmentManager, HuDialogFragment.TAG)
            PENALTY -> PenaltyDialogFragment().show(activity.supportFragmentManager, PenaltyDialogFragment.TAG)
            RANKING -> RankingDialogFragment().show(activity.supportFragmentManager, RankingDialogFragment.TAG)
            EXIT -> exitGame(activity, viewModel)
        }
    }

    private fun exitGame(activity: GameActivity, viewModel: GameViewModel) {
        activity.setResult(RESULT_OK, Intent().apply { putExtra(KEY_WAS_GAME_ENDED, viewModel.gameFlow.value.dbGame.isEnded) })
        activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        activity.finish()
    }
}