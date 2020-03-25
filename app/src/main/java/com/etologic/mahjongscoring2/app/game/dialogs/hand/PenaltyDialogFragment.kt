package com.etologic.mahjongscoring2.app.game.dialogs.hand

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.game.base.BaseGameDialogFragment
import com.etologic.mahjongscoring2.app.utils.KeyboardUtils.hideKeyboard
import com.etologic.mahjongscoring2.app.utils.KeyboardUtils.showKeyboard
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
        tietPenaltyDialog?.selectAll()
        tietPenaltyDialog?.requestFocus()
        showKeyboard(view)
    }
    
    private fun setOnClickListeners() {
        btPenaltyDialogSave?.setOnClickListener {
            val penaltyPoints = Integer.valueOf(tietPenaltyDialog?.text.toString())
            if (penaltyPoints > 0) {
                if (cbPenaltyDialog?.isChecked == true) {
                    if (penaltyPoints % 3 == 0) {
                        saveAndFinish(penaltyPoints, true)
                    } else {
                        tietPenaltyDialog?.error = ""
                        cbPenaltyDialog?.error = ""
                    }
                } else
                    saveAndFinish(penaltyPoints, false)
            } else
                tietPenaltyDialog.error = ""
        }
        btPenaltyDialogCancel?.setOnClickListener {
            hideKeyboard(tietPenaltyDialog)
            dismiss()
        }
    }
    
    private fun saveAndFinish(penaltyPoints: Int, isDivided: Boolean) {
        activityViewModel?.savePenalty(penaltyPoints, isDivided)
        tietPenaltyDialog?.let { hideKeyboard(it) }
        isDialogCancelled = false
        dismiss()
    }
    
    override fun onDismiss(dialog: DialogInterface) {
        if (isDialogCancelled) activityViewModel?.handDialogCanceled()
        super.onDismiss(dialog)
    }
}