package com.etologic.mahjongscoring2.app.game.dialogs.hand

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.game.base.BaseGameDialogFragment
import com.etologic.mahjongscoring2.business.model.enums.WinType.RON
import kotlinx.android.synthetic.main.game_points_dialog_fragment.*

internal class HuDialogFragment : BaseGameDialogFragment() {
    
    companion object {
        const val TAG = "PointsFragment"
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.game_points_dialog_fragment, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activityViewModel?.getSelectedSeat()?.let {
            activityViewModel?.getListNames()?.value?.let { names ->
                cdsPointsDialog.initPlayers(names, it)
            }
        }
        setListeners()
    }
    
    private fun setListeners() {
        btPointsDialogCancel.setOnClickListener { dismiss() }
        btPointsDialogOk.setOnClickListener {
            cnpPointsDialog?.getPoints()?.let {
                if (cdsPointsDialog?.isRonOrTsumo() == RON)
                    activityViewModel?.saveRonRound(it, cdsPointsDialog.getDiscarderCurrentSeat())
                else
                    activityViewModel?.saveTsumoRound(it)
            }
            dismiss()
        }
    }
    
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }
}