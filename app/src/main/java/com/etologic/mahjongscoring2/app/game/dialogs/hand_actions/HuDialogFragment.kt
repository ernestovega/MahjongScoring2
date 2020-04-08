/*
    Copyright (C) 2020  Ernesto Vega de la Iglesia Soria
 
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.etologic.mahjongscoring2.app.game.dialogs.hand_actions

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.extensions.setOnSecureClickListener
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel.GameScreens.DISCARDER
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
            if (points == null || points < MIN_MCR_POINTS || points > MAX_MCR_POINTS)
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
        if (isDialogCancelled) activityViewModel?.unselectedSelectedSeat()
        super.onDismiss(dialog)
    }
}