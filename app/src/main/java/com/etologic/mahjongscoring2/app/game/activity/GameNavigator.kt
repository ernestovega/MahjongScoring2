package com.etologic.mahjongscoring2.app.game.activity

import android.content.Intent
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
            COMBINATIONS -> activity.goToActivity(Intent(activity, CombinationsActivity::class.java), CombinationsActivity.CODE)
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