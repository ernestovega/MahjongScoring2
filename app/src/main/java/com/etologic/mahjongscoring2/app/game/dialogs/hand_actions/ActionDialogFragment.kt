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
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.extensions.setOnSecureClickListener
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel.GameScreens.HU
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel.GameScreens.PENALTY
import com.etologic.mahjongscoring2.app.game.base.BaseGameDialogFragment
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.databinding.GameHandActionSelectorFragmentBinding

internal class ActionDialogFragment : BaseGameDialogFragment() {

    companion object {
        const val TAG = "HandActionsDialogFragment"
    }

    private var eastIcon: Drawable? = null
    private var southIcon: Drawable? = null
    private var westIcon: Drawable? = null
    private var northIcon: Drawable? = null
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

    override fun getTheme(): Int {
        return R.style.FullScreenDialogMM
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let {
            eastIcon = ContextCompat.getDrawable(it, R.drawable.ic_east)
            southIcon = ContextCompat.getDrawable(it, R.drawable.ic_south)
            westIcon = ContextCompat.getDrawable(it, R.drawable.ic_west)
            northIcon = ContextCompat.getDrawable(it, R.drawable.ic_north)
        }
        initViews()
        setListeners()
    }

    private fun initViews() {
        binding.ivHandActionsDialogPlayerSeatWind.setImageDrawable(
            when (activityViewModel?.getSelectedSeat()?.value) {
                TableWinds.EAST -> eastIcon
                TableWinds.SOUTH -> southIcon
                TableWinds.WEST -> westIcon
                TableWinds.NORTH -> northIcon
                else -> null
            }
        )
        binding.tvHandActionsDialogPlayerName.text = activityViewModel?.getSelectedSeat()?.value?.code?.let { windCode ->
            activityViewModel?.getCurrentTable()?.value?.getPlayersNamesByCurrentSeat()?.get(windCode) ?: ""
        } ?: ""

        binding.btHandActionsDialogPenaltiesCancel.visibility =
            if (activityViewModel?.thereArePenaltiesCurrently() == true) VISIBLE else GONE
    }

    private fun setListeners() {
        with(binding) {
            root.setOnSecureClickListener { dismiss() }
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

    override fun onDismiss(dialog: DialogInterface) {
        if (isDialogCancelled)
            activityViewModel?.unselectSelectedSeat()
        super.onDismiss(dialog)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}