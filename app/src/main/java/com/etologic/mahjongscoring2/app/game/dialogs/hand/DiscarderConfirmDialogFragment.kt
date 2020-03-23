package com.etologic.mahjongscoring2.app.game.dialogs.hand

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.game.base.BaseGameDialogFragment
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.EAST
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NONE
import kotlinx.android.synthetic.main.game_discarder_confirm_dialog_fragment.*
import kotlinx.android.synthetic.main.game_points_dialog_fragment.*

internal class DiscarderConfirmDialogFragment : BaseGameDialogFragment() {
    
    companion object {
        const val TAG = "DiscarderConfirmDialogFragment"
    }
    
    private var winnerWind: TableWinds = NONE
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.game_discarder_confirm_dialog_fragment, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        if (activityViewModel != null && activityViewModel!!.getListNames().value != null && cdsDiscarderConfirmDialog != null) {
            winnerWind = activityViewModel!!.getSelectedSeat()
            cdsDiscarderConfirmDialog.initPlayers(activityViewModel!!.getListNames().value!!, activityViewModel!!.huPoints!!, winnerWind)
        }
    }
    
    private fun setListeners() {
        btDiscarderConfirmDialogCancel.setOnClickListener { dismiss() }
        btDiscarderConfirmDialogOk.setOnClickListener {
            val discarderCurrentSeat: TableWinds = cdsDiscarderConfirmDialog?.getDiscarderCurrentSeat() ?: NONE
            if (activityViewModel?.huPoints != null) {
                if (discarderCurrentSeat == NONE)
                    activityViewModel?.saveTsumoRound(activityViewModel?.huPoints!!)
                else
                    activityViewModel?.saveRonRound(discarderCurrentSeat, activityViewModel?.huPoints!!)
                dismiss()
            }
        }
    }
}