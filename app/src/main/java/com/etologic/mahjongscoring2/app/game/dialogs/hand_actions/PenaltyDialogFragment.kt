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
import com.etologic.mahjongscoring2.app.game.base.BaseGameDialogFragment
import com.etologic.mahjongscoring2.business.model.dtos.PenaltyData
import com.etologic.mahjongscoring2.business.model.entities.Table.Companion.NUM_NO_WINNER_PLAYERS
import com.etologic.mahjongscoring2.databinding.GamePenaltyDialogFragmentBinding

internal class PenaltyDialogFragment : BaseGameDialogFragment() {

    companion object {
        const val TAG = "PointsFragment"
        private const val INITIAL_PENALTY_POINTS = 30
    }

    private var isDialogCancelled = true

    private var _binding: GamePenaltyDialogFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GamePenaltyDialogFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.FullScreenDialogMM
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
        binding.cnpPenaltyDialog.setHint(INITIAL_PENALTY_POINTS)
    }

    private fun setOnClickListeners() {
        with(binding) {
            btPenaltyDialogSave.setOnSecureClickListener {
                val penaltyPoints = cnpPenaltyDialog.getPoints() ?: 0
                if (penaltyPoints > 0) {
                    if (cbPenaltyDialog.isChecked) {
                        if (penaltyPoints % NUM_NO_WINNER_PLAYERS == 0)
                            saveAndFinish(penaltyPoints, true)
                        else {
                            cnpPenaltyDialog.setError()
                            cbPenaltyDialog.error = ""
                        }
                    } else
                        saveAndFinish(penaltyPoints, false)
                } else
                    cnpPenaltyDialog.setError()
            }
            btPenaltyDialogCancel.setOnSecureClickListener { dismiss() }
        }
    }

    private fun saveAndFinish(penaltyPoints: Int, isDivided: Boolean) {
        activityViewModel?.savePenalty(PenaltyData(penaltyPoints, isDivided))
        isDialogCancelled = false
        dismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        if (isDialogCancelled) activityViewModel?.unselectSelectedSeat()
        super.onDismiss(dialog)
    }
}