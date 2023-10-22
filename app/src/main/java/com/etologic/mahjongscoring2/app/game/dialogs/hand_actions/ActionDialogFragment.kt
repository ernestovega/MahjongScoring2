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

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.extensions.setOnSecureClickListener
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel.GameScreens.HU
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel.GameScreens.PENALTY
import com.etologic.mahjongscoring2.app.game.base.BaseGameDialogFragment
import com.etologic.mahjongscoring2.databinding.GameHandActionSelectorFragmentBinding

internal class ActionDialogFragment : BaseGameDialogFragment() {

    companion object {
        const val TAG = "HandActionsDialogFragment"
    }

    private var isDialogCancelled = true
    private var _binding: GameHandActionSelectorFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GameHandActionSelectorFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setListeners()
    }

    private fun initViews() {
        binding.btHandActionsDialogPenaltiesCancel.visibility =
            if (activityViewModel?.thereArePenaltiesCurrently() == true) VISIBLE else GONE
    }

    private fun setListeners() {
        with(binding) {
            btHandActionsDialogPenaltiesCancel.setOnSecureClickListener {
                AlertDialog.Builder(activity, R.style.AlertDialogStyleMM)
                    .setTitle(R.string.cancel_penalties)
                    .setMessage(R.string.are_you_sure)
                    .setPositiveButton(R.string.ok) { _, _ ->
                        activityViewModel?.cancelPenalties()
                        dismiss()
                    }
                    .setNegativeButton(R.string.close, null)
                    .create()
                    .show()
            }
            btHandActionsDialogPenalty.setOnSecureClickListener {
                activityViewModel?.navigateTo(PENALTY)
                isDialogCancelled = false
                dismiss()
            }
            btHandActionsDialogHu.setOnSecureClickListener {
                activityViewModel?.navigateTo(HU)
                isDialogCancelled = false
                dismiss()
            }
            btHandActionsDialogDraw.setOnSecureClickListener {
                activityViewModel?.saveDrawRound()
                isDialogCancelled = false
                dismiss()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onDismiss(dialog: DialogInterface) {
        if (isDialogCancelled)
            activityViewModel?.unselectedSelectedSeat()
        super.onDismiss(dialog)
    }
}