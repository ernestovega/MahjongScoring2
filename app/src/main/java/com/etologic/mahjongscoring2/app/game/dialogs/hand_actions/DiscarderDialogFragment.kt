package com.etologic.mahjongscoring2.app.game.dialogs.hand_actions

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.extensions.setOnSecureClickListener
import com.etologic.mahjongscoring2.app.game.base.BaseGameDialogFragment
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NONE
import kotlinx.android.synthetic.main.game_discarder_dialog_fragment.*

internal class DiscarderDialogFragment : BaseGameDialogFragment() {
    
    companion object {
        const val TAG = "DiscarderConfirmDialogFragment"
    }
    
    private var isDialogCancelled = true
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.game_discarder_dialog_fragment, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        if (cdsDiscarderConfirmDialog != null && activityViewModel != null)
            cdsDiscarderConfirmDialog.initPlayers(
                activityViewModel!!.getNamesByCurrentSeat(),
                activityViewModel!!.huPoints,
                activityViewModel!!.selectedSeat)
        else
            dismiss()
    }
    
    private fun setListeners() {
        btDiscarderConfirmDialogCancel?.setOnSecureClickListener { dismiss() }
        btDiscarderConfirmDialogOk?.setOnSecureClickListener {
            val discarderCurrentSeat: TableWinds = cdsDiscarderConfirmDialog?.getDiscarderCurrentSeat() ?: NONE
            if (activityViewModel?.huPoints != null) {
                if (discarderCurrentSeat == NONE)
                    activityViewModel?.saveTsumoRound(activityViewModel?.huPoints!!)
                else
                    activityViewModel?.saveRonRound(discarderCurrentSeat, activityViewModel?.huPoints!!)
                isDialogCancelled = false
                dismiss()
            }
        }
    }
    
    override fun onDismiss(dialog: DialogInterface) {
        if(isDialogCancelled) activityViewModel?.unselectedSelectedSeat()
        super.onDismiss(dialog)
    }
}