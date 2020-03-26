package com.etologic.mahjongscoring2.app.game.dialogs.hand

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.game.activity.GameActivityViewModel.GameScreens.HU
import com.etologic.mahjongscoring2.app.game.activity.GameActivityViewModel.GameScreens.PENALTY
import com.etologic.mahjongscoring2.app.game.base.BaseGameDialogFragment
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
        setListeners()
    }
    
    private fun initViews() {
        btHandActionsDialogPenaltiesCancel?.visibility = if(activityViewModel?.thereArePenaltiesCurrently() == true) VISIBLE else GONE
    }
    
    private fun setListeners() {
        btHandActionsDialogPenaltiesCancel?.setOnClickListener {
            isDialogCancelled = false
            AlertDialog.Builder(activity, R.style.AlertDialogStyleMM)
                .setTitle(R.string.cancel_penalties)
                .setMessage(R.string.are_you_sure)
                .setPositiveButton(android.R.string.ok) { _, _ -> activityViewModel?.cancelPenalties() }
                .setNegativeButton(R.string.close, null)
                .setOnDismissListener { this.dismiss() }
                .create()
                .show()
        }
        btHandActionsDialogPenalty?.setOnClickListener {
            activityViewModel?.navigateTo(PENALTY)
            isDialogCancelled = false
            dismiss()
        }
        btHandActionsDialogHu?.setOnClickListener {
            activityViewModel?.navigateTo(HU)
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