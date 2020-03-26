package com.etologic.mahjongscoring2.app.game.dialogs.hand

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.game.base.BaseGameDialogFragment
import com.etologic.mahjongscoring2.business.model.entities.GameWithRounds
import com.etologic.mahjongscoring2.business.model.entities.GameWithRounds.Companion.NUM_NO_WINNER_PLAYERS
import kotlinx.android.synthetic.main.game_penalty_dialog_fragment.*

internal class PenaltyDialogFragment : BaseGameDialogFragment() {
    
    companion object {
        const val TAG = "PointsFragment"
    }
    
    private var isDialogCancelled = true
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.game_penalty_dialog_fragment, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
    }
    
    private fun setOnClickListeners() {
        btPenaltyDialogSave?.setOnClickListener {
            val penaltyPoints = cnpPenaltyDialog?.getPoints() ?: 0
            if (penaltyPoints > 0) {
                if (cbPenaltyDialog?.isChecked == true) {
                    if (penaltyPoints % NUM_NO_WINNER_PLAYERS == 0)
                        saveAndFinish(penaltyPoints, true)
                    else {
                        cnpPenaltyDialog?.setError()
                        cbPenaltyDialog?.error = ""
                    }
                } else
                    saveAndFinish(penaltyPoints, false)
            } else
                cnpPenaltyDialog?.setError()
        }
        btPenaltyDialogCancel?.setOnClickListener { dismiss() }
    }
    
    private fun saveAndFinish(penaltyPoints: Int, isDivided: Boolean) {
        activityViewModel?.savePenalty(penaltyPoints, isDivided)
        isDialogCancelled = false
        dismiss()
    }
    
    override fun onDismiss(dialog: DialogInterface) {
        if (isDialogCancelled) activityViewModel?.handDialogCanceled()
        super.onDismiss(dialog)
    }
}