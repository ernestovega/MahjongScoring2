package com.etologic.mahjongscoring2.app.game.activity

import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.game.dialogs.hand.PenaltyDialogFragment
import com.etologic.mahjongscoring2.app.game.dialogs.hand.ActionDialogFragment
import com.etologic.mahjongscoring2.app.game.dialogs.hand.HuDialogFragment
import com.etologic.mahjongscoring2.app.game.dialogs.players.PlayersDialogFragment
import com.etologic.mahjongscoring2.app.game.dialogs.ranking.RankingDialogFragment
import com.etologic.mahjongscoring2.app.game.dialogs.roll_dice.RollDiceDialogFragment
import com.etologic.mahjongscoring2.app.model.DialogType
import com.etologic.mahjongscoring2.app.model.DialogType.*

object GameNavigator {
    
    internal fun showDialog(dialogType: DialogType, activity: GameActivity) {
        when (dialogType) {
            PLAYERS -> openDialog(PlayersDialogFragment(), PlayersDialogFragment.TAG, activity)
            DICE -> openDialog(RollDiceDialogFragment(), RollDiceDialogFragment.TAG, activity)
            HAND_ACTION -> openDialog(ActionDialogFragment(), ActionDialogFragment.TAG, activity)
            HU -> openDialog(HuDialogFragment(), HuDialogFragment.TAG, activity)
            PENALTY -> openDialog(PenaltyDialogFragment(), PenaltyDialogFragment.TAG, activity)
            RANKING -> openDialog(RankingDialogFragment(), RankingDialogFragment.TAG, activity)
            EXIT -> showDialogExitGame(activity)
            else -> return
        }
    }
    
    private fun openDialog(dialogFragment: DialogFragment, tag: String, activity: GameActivity) {
        val fragmentTransaction = activity.supportFragmentManager.beginTransaction()
        val prev = activity.supportFragmentManager.findFragmentByTag(tag)
        if (prev != null)
            fragmentTransaction.remove(prev)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
        dialogFragment.show(activity.supportFragmentManager, tag)
    }
    
    private fun showDialogExitGame(activity: GameActivity) {
        val builder = AlertDialog.Builder(activity, R.style.AppTheme)
        builder.setTitle(R.string.exit_game)
            .setMessage(R.string.are_you_sure)
            .setPositiveButton(R.string.exit) { _, _ -> /*activity.viewModel.endGame()*/ }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
            .show()
    }
}