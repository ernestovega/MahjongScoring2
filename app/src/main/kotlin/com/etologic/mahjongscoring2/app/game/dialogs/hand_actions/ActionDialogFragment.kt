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

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.extensions.setOnSecureClickListener
import com.etologic.mahjongscoring2.app.extensions.toStringOrHyphen
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel.GameScreens.HU
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel.GameScreens.PENALTY
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.databinding.GameActionDialogFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActionDialogFragment : AppCompatDialogFragment() {

    companion object {
        const val TAG = "ActionsDialogFragment"
    }

    private var eastIcon: Drawable? = null
    private var southIcon: Drawable? = null
    private var westIcon: Drawable? = null
    private var northIcon: Drawable? = null
    private var isDialogCancelled = true
    private var _binding: GameActionDialogFragmentBinding? = null
    private val binding get() = _binding!!

    private val activityViewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GameActionDialogFragmentBinding.inflate(inflater, container, false)
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
        setPlayer()
        setButtons()
    }

    private fun setPlayer() {
        val selectedSeat = activityViewModel.getSelectedSeat().value
        binding.ivHandActionsDialogPlayerSeatWind.setImageDrawable(
            when (selectedSeat) {
                TableWinds.EAST -> eastIcon
                TableWinds.SOUTH -> southIcon
                TableWinds.WEST -> westIcon
                TableWinds.NORTH -> northIcon
                else -> null
            }
        )
        binding.tvHandActionsDialogPlayerName.text = selectedSeat?.code?.let { windCode ->
            activityViewModel.gameFlow.value.getPlayersNamesByCurrentRoundSeat()[windCode]
        } ?: ""
        if (activityViewModel.isDiffsCalcsFeatureEnabledFlow.value) {
            selectedSeat?.let { setPlayerDiffs(it) }
        }
    }

    private fun setPlayerDiffs(playerSeat: TableWinds) {
        with(binding) {
            val playerDiffs = activityViewModel.gameFlow.value.getTableDiffs().seatsDiffs[playerSeat.code]
            with(iHandActionsDialogDiffs) {
                tlActionDialogDiffs.visibility = VISIBLE
                tvActionDialogDiffsFirstSelfPick.text = playerDiffs.pointsToBeFirst?.bySelfPick.toStringOrHyphen()
                tvActionDialogDiffsFirstDirectHu.text = playerDiffs.pointsToBeFirst?.byDirectHu.toStringOrHyphen()
                tvActionDialogDiffsFirstIndirectHu.text = playerDiffs.pointsToBeFirst?.byIndirectHu.toStringOrHyphen()

                tvActionDialogDiffsSecondSelfPick.text = playerDiffs.pointsToBeSecond?.bySelfPick.toStringOrHyphen()
                tvActionDialogDiffsSecondDirectHu.text = playerDiffs.pointsToBeSecond?.byDirectHu.toStringOrHyphen()
                tvActionDialogDiffsSecondIndirectHu.text = playerDiffs.pointsToBeSecond?.byIndirectHu.toStringOrHyphen()

                tvActionDialogDiffsThirdSelfPick.text = playerDiffs.pointsToBeThird?.bySelfPick.toStringOrHyphen()
                tvActionDialogDiffsThirdDirectHu.text = playerDiffs.pointsToBeThird?.byDirectHu.toStringOrHyphen()
                tvActionDialogDiffsThirdIndirectHu.text = playerDiffs.pointsToBeThird?.byIndirectHu.toStringOrHyphen()
            }
        }
    }

    private fun setButtons() {
        binding.btHandActionsDialogPenaltiesCancel.visibility =
            if (activityViewModel.gameFlow.value.currentRound.areTherePenalties()) VISIBLE else GONE
    }

    private fun setListeners() {
        with(binding) {
            root.setOnSecureClickListener { dismiss() }
            btHandActionsDialogHu.setOnSecureClickListener {
                activityViewModel.navigateTo(HU)
                isDialogCancelled = false
                dismiss()
            }
            btHandActionsDialogDraw.setOnSecureClickListener {
                activityViewModel.saveDrawRound()
                isDialogCancelled = false
                dismiss()
            }
            btHandActionsDialogPenalty.setOnSecureClickListener {
                activityViewModel.navigateTo(PENALTY)
                isDialogCancelled = false
                dismiss()
            }
            btHandActionsDialogPenaltiesCancel.setOnSecureClickListener {
                AlertDialog.Builder(activity, R.style.AlertDialogStyleMM)
                    .setTitle(R.string.cancel_penalties)
                    .setMessage(R.string.are_you_sure)
                    .setPositiveButton(R.string.ok) { _, _ ->
                        activityViewModel.cancelPenalties()
                        dismiss()
                    }
                    .setNegativeButton(R.string.close, null)
                    .create()
                    .show()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        if (isDialogCancelled) {
            activityViewModel.unselectSelectedSeat()
        }
        super.onDismiss(dialog)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}