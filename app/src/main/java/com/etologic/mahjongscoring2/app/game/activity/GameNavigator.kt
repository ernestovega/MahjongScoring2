package com.etologic.mahjongscoring2.app.game.activity

import com.etologic.mahjongscoring2.app.game.activity.GameActivityViewModel.GameScreens
import com.etologic.mahjongscoring2.app.game.activity.GameActivityViewModel.GameScreens.*
import com.etologic.mahjongscoring2.app.game.dialogs.hand.ActionDialogFragment
import com.etologic.mahjongscoring2.app.game.dialogs.hand.DiscarderDialogFragment
import com.etologic.mahjongscoring2.app.game.dialogs.hand.PointsHuDialogFragment
import com.etologic.mahjongscoring2.app.game.dialogs.hand.PenaltyDialogFragment
import com.etologic.mahjongscoring2.app.game.dialogs.names.NamesDialogFragment
import com.etologic.mahjongscoring2.app.game.dialogs.ranking.RankingDialogFragment
import com.etologic.mahjongscoring2.app.game.dialogs.roll_dice.RollDiceDialogFragment

object GameNavigator {
    
    internal fun showDialog(screen: GameScreens, activity: GameActivity) {
        when (screen) {
            PLAYERS -> NamesDialogFragment().show(activity.supportFragmentManager, NamesDialogFragment.TAG)
            DICE -> RollDiceDialogFragment().show(activity.supportFragmentManager, RollDiceDialogFragment.TAG)
            HAND_ACTION -> ActionDialogFragment().show(activity.supportFragmentManager, ActionDialogFragment.TAG)
            HU -> PointsHuDialogFragment().show(activity.supportFragmentManager, PointsHuDialogFragment.TAG)
            DISCARDER -> DiscarderDialogFragment().show(activity.supportFragmentManager, DiscarderDialogFragment.TAG)
            PENALTY -> PenaltyDialogFragment().show(activity.supportFragmentManager, PenaltyDialogFragment.TAG)
            RANKING -> RankingDialogFragment().show(activity.supportFragmentManager, RankingDialogFragment.TAG)
            EXIT -> exitGame(activity)
            else -> return
        }
    }
    
    private fun exitGame(activity: GameActivity) {
        activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        activity.finish()
    }
}