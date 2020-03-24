package com.etologic.mahjongscoring2.app.game.dialogs.hand

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.game.base.BaseGameDialogFragment
import com.etologic.mahjongscoring2.app.model.DialogType.CONFIRM_AND_DISCARDER
import kotlinx.android.synthetic.main.game_points_dialog_fragment.*

internal class PointsHuDialogFragment : BaseGameDialogFragment() {
    
    companion object {
        const val TAG = "PointsFragment"
    }
    
    private var isDialogCancelled = true
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.game_points_dialog_fragment, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }
    
    private fun setListeners() {
        btPointsDialogCancel.setOnClickListener { dismiss() }
        btPointsDialogOk.setOnClickListener {
            val points = cnpPointsDialog?.getPoints()
            if(points == null || points < 8 || points > 9999)
                cnpPointsDialog?.setError()
            else {
                activityViewModel?.huPoints = points
                activityViewModel?.showDialog(CONFIRM_AND_DISCARDER)
                isDialogCancelled = false
                dismiss()
            }
        }
    }
    
    override fun onDismiss(dialog: DialogInterface) {
        if(isDialogCancelled) activityViewModel?.handDialogCanceled()
        super.onDismiss(dialog)
    }
}