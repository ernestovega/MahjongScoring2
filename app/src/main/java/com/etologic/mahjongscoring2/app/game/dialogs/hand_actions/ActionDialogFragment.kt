package com.etologic.mahjongscoring2.app.game.dialogs.hand_actions

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.extensions.setOnSecureClickListener
import com.etologic.mahjongscoring2.app.game.activity.GameActivityViewModel.GameScreens.HU
import com.etologic.mahjongscoring2.app.game.activity.GameActivityViewModel.GameScreens.PENALTY
import com.etologic.mahjongscoring2.app.game.base.BaseGameDialogFragment
import kotlinx.android.synthetic.main.game_hand_action_selector_fragment.*

internal class ActionDialogFragment : BaseGameDialogFragment() {
    
    companion object {
        const val TAG = "HandActionsDialogFragment"
    }
    
    private var isDialogCancelled = true
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.game_hand_action_selector_fragment, container, false)
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
        btHandActionsDialogPenaltiesCancel?.setOnSecureClickListener {
            AlertDialog.Builder(activity, R.style.AlertDialogStyleMM)
                .setTitle(R.string.cancel_penalties)
                .setMessage(R.string.are_you_sure)
                .setPositiveButton(R.string.ok) { _, _ ->
                    activityViewModel?.cancelPenalties()
                    this.dismiss()
                }
                .setNegativeButton(R.string.close, null)
                .create()
                .show()
        }
        btHandActionsDialogPenalty?.setOnSecureClickListener {
            activityViewModel?.navigateTo(PENALTY)
            isDialogCancelled = false
            dismiss()
        }
        btHandActionsDialogHu?.setOnSecureClickListener {
            activityViewModel?.navigateTo(HU)
            isDialogCancelled = false
            dismiss()
        }
        btHandActionsDialogDraw?.setOnSecureClickListener {
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
        if(isDialogCancelled)
            activityViewModel?.unselectedSelectedSeat()
        super.onDismiss(dialog)
    }
}