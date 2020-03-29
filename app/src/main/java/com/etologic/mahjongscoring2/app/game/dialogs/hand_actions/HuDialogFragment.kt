package com.etologic.mahjongscoring2.app.game.dialogs.hand_actions

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.extensions.setOnSecureClickListener
import com.etologic.mahjongscoring2.app.game.activity.GameActivityViewModel.GameScreens.DISCARDER
import com.etologic.mahjongscoring2.app.game.base.BaseGameDialogFragment
import com.etologic.mahjongscoring2.business.model.entities.Table.Companion.MAX_MCR_POINTS
import com.etologic.mahjongscoring2.business.model.entities.Table.Companion.MIN_MCR_POINTS
import kotlinx.android.synthetic.main.game_points_dialog_fragment.*

internal class HuDialogFragment : BaseGameDialogFragment() {
    
    companion object {
        const val TAG = "PointsHuFragment"
    }
    
    private var isDialogCancelled = true
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.game_points_dialog_fragment, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        cnpPointsDialog?.setHint(MIN_MCR_POINTS)
    }
    
    private fun setListeners() {
        btPointsDialogCancel.setOnSecureClickListener { dismiss() }
        btPointsDialogOk.setOnSecureClickListener {
            val points = cnpPointsDialog?.getPoints()
            if(points == null || points < MIN_MCR_POINTS || points > MAX_MCR_POINTS)
                cnpPointsDialog?.setError()
            else {
                activityViewModel?.huPoints = points
                activityViewModel?.navigateTo(DISCARDER)
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