package com.etologic.mahjongscoring2.app.game.activity

import androidx.appcompat.app.AlertDialog
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.game.dialogs.hand.ActionDialogFragment
import com.etologic.mahjongscoring2.app.game.dialogs.hand.DiscarderDialogFragment
import com.etologic.mahjongscoring2.app.game.dialogs.hand.PointsHuDialogFragment
import com.etologic.mahjongscoring2.app.game.dialogs.hand.PenaltyDialogFragment
import com.etologic.mahjongscoring2.app.game.dialogs.players.PlayersDialogFragment
import com.etologic.mahjongscoring2.app.game.dialogs.ranking.RankingDialogFragment
import com.etologic.mahjongscoring2.app.game.dialogs.roll_dice.RollDiceDialogFragment
import com.etologic.mahjongscoring2.app.model.DialogType
import com.etologic.mahjongscoring2.app.model.DialogType.*

object GameNavigator {
    
    internal fun showDialog(dialogType: DialogType, activity: GameActivity) {
        when (dialogType) {
            PLAYERS -> PlayersDialogFragment().show(activity.supportFragmentManager, PlayersDialogFragment.TAG)
            DICE -> RollDiceDialogFragment().show(activity.supportFragmentManager, RollDiceDialogFragment.TAG)
            HAND_ACTION -> ActionDialogFragment().show(activity.supportFragmentManager, ActionDialogFragment.TAG)
            HU -> PointsHuDialogFragment().show(activity.supportFragmentManager, PointsHuDialogFragment.TAG)
            CONFIRM_AND_DISCARDER -> DiscarderDialogFragment().show(activity.supportFragmentManager, DiscarderDialogFragment.TAG)
            PENALTY -> PenaltyDialogFragment().show(activity.supportFragmentManager, PenaltyDialogFragment.TAG)
            RANKING -> RankingDialogFragment().show(activity.supportFragmentManager, RankingDialogFragment.TAG)
            EXIT -> showDialogExitGame(activity)
            else -> return
        }
    }
    
    private fun showDialogExitGame(activity: GameActivity) {
        AlertDialog.Builder(activity, R.style.AlertDialogStyleMM)
            .setTitle(R.string.exit_game)
            .setMessage(R.string.are_you_sure)
            .setPositiveButton(R.string.exit) { _, _ -> activity.finish() }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
            .show()
    }
}