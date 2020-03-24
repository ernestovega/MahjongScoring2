package com.etologic.mahjongscoring2.app.game.dialogs.hand

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.game.base.BaseGameDialogFragment
import com.etologic.mahjongscoring2.app.model.DialogType.HU
import com.etologic.mahjongscoring2.app.model.DialogType.PENALTY
import kotlinx.android.synthetic.main.game_hand_actions_dialog_fragment.*

internal class ActionDialogFragment : BaseGameDialogFragment() {
    
    companion object {
        const val TAG = "HandActionsDialogFragment"
    }
    
    private var isDialogCancelled = true
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.game_hand_actions_dialog_fragment, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }
    
    private fun initViews() {
        btHandActionsDialogPenalty?.setOnClickListener {
            activityViewModel?.showDialog(PENALTY)
            isDialogCancelled = false
            dismiss()
        }
        btHandActionsDialogHu?.setOnClickListener {
            activityViewModel?.showDialog(HU)
            isDialogCancelled = false
            dismiss()
        }
        btHandActionsDialogDraw?.setOnClickListener {
            activityViewModel?.saveDrawRound()
            isDialogCancelled = false
            dismiss()
        }
    }
    
    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
    
    override fun onDismiss(dialog: DialogInterface) {
        if(isDialogCancelled) activityViewModel?.handDialogCanceled()
        super.onDismiss(dialog)
    }
}