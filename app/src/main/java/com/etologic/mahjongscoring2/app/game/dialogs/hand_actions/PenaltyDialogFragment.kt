/*
 *     Copyright © 2023  Ernesto Vega de la Iglesia Soria
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.etologic.mahjongscoring2.app.game.dialogs.hand_actions

import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.extensions.setOnSecureClickListener
import com.etologic.mahjongscoring2.app.game.base.BaseGameDialogFragment
import com.etologic.mahjongscoring2.business.model.dtos.PenaltyData
import com.etologic.mahjongscoring2.business.model.entities.Table.Companion.NUM_NO_WINNER_PLAYERS
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.databinding.GamePenaltyDialogFragmentBinding

internal class PenaltyDialogFragment : BaseGameDialogFragment() {

    companion object {
        const val TAG = "PenaltyDialogFragment"
    }

    private var eastIcon: Drawable? = null
    private var southIcon: Drawable? = null
    private var westIcon: Drawable? = null
    private var northIcon: Drawable? = null
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
        context?.let {
            eastIcon = ContextCompat.getDrawable(it, R.drawable.ic_east)
            southIcon = ContextCompat.getDrawable(it, R.drawable.ic_south)
            westIcon = ContextCompat.getDrawable(it, R.drawable.ic_west)
            northIcon = ContextCompat.getDrawable(it, R.drawable.ic_north)
        }
        setOnClickListeners()
        initPlayerViews()
    }

    private fun initPlayerViews() {
        with(binding.iPenaltyDialogPlayerContainer) {
            val selectedSeat = activityViewModel?.getSelectedSeat()?.value ?: TableWinds.NONE
            val playersNamesByCurrentSeat = activityViewModel?.getCurrentTable()?.value?.getPlayersNamesByCurrentSeat()
            ivTableSeatMediumSeatWind.setImageDrawable(getWindIcon(selectedSeat))
            tvTableSeatMediumName.text = selectedSeat.code.let { playersNamesByCurrentSeat?.get(it) ?: "" }
        }
    }

    private fun getWindIcon(wind: TableWinds?) =
        when (wind) {
            TableWinds.EAST -> eastIcon
            TableWinds.SOUTH -> southIcon
            TableWinds.WEST -> westIcon
            TableWinds.NORTH -> northIcon
            else -> null
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