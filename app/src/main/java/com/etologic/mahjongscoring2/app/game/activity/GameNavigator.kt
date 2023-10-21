/*
    Copyright (C) 2020  Ernesto Vega de la Iglesia Soria
 
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.etologic.mahjongscoring2.app.game.activity

import android.app.Activity
import android.app.Activity.OVERRIDE_TRANSITION_CLOSE
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel.GameScreens
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel.GameScreens.*
import com.etologic.mahjongscoring2.app.game.dialogs.hand_actions.ActionDialogFragment
import com.etologic.mahjongscoring2.app.game.dialogs.hand_actions.DiscarderDialogFragment
import com.etologic.mahjongscoring2.app.game.dialogs.hand_actions.HuDialogFragment
import com.etologic.mahjongscoring2.app.game.dialogs.hand_actions.PenaltyDialogFragment
import com.etologic.mahjongscoring2.app.game.dialogs.names.NamesDialogFragment
import com.etologic.mahjongscoring2.app.game.dialogs.ranking.RankingDialogFragment
import com.etologic.mahjongscoring2.app.game.dialogs.roll_dice.RollDiceDialogFragment
import com.etologic.mahjongscoring2.app.main.combinations.CombinationsActivity

object GameNavigator {
    
    internal fun showDialog(screen: GameScreens, activity: GameActivity) {
        when (screen) {
            COMBINATIONS -> activity.startActivity(Intent(activity, CombinationsActivity::class.java))
            PLAYERS -> NamesDialogFragment().show(activity.supportFragmentManager, NamesDialogFragment.TAG)
            DICE -> RollDiceDialogFragment().show(activity.supportFragmentManager, RollDiceDialogFragment.TAG)
            HAND_ACTION -> ActionDialogFragment()
                .show(activity.supportFragmentManager, ActionDialogFragment.TAG)
            HU -> HuDialogFragment()
                .show(activity.supportFragmentManager, HuDialogFragment.TAG)
            DISCARDER -> DiscarderDialogFragment()
                .show(activity.supportFragmentManager, DiscarderDialogFragment.TAG)
            PENALTY -> PenaltyDialogFragment()
                .show(activity.supportFragmentManager, PenaltyDialogFragment.TAG)
            RANKING -> RankingDialogFragment().show(activity.supportFragmentManager, RankingDialogFragment.TAG)
            EXIT -> exitGame(activity)
        }
    }
    
    private fun exitGame(activity: GameActivity) {
        activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        activity.finish()
    }
}