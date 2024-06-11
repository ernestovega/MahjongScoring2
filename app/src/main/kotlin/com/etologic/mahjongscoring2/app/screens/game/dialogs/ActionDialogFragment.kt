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
package com.etologic.mahjongscoring2.app.screens.game.dialogs

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.base.BaseGameDialogFragment
import com.etologic.mahjongscoring2.app.screens.game.openHuDialog
import com.etologic.mahjongscoring2.app.screens.game.openPenaltyDialog
import com.etologic.mahjongscoring2.app.utils.setOnSecureClickListener
import com.etologic.mahjongscoring2.app.utils.toStringOrHyphen
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.databinding.GameDialogHandActionsFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ActionDialogFragment : BaseGameDialogFragment() {

    companion object {
        const val TAG = "ActionsDialogFragment"
    }

    private var eastIcon: Drawable? = null
    private var southIcon: Drawable? = null
    private var westIcon: Drawable? = null
    private var northIcon: Drawable? = null
    private var isDialogCancelled = true
    private var _binding: GameDialogHandActionsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GameDialogHandActionsFragmentBinding.inflate(inflater, container, false)
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
        setListeners()
        startObservingTable()
    }

    private fun startObservingTable() {
        Log.d(
            "ActionDialogFragment",
            "GameViewModel: ${gameViewModel.hashCode()} - parentFragment.parentFragment: ${parentFragment?.parentFragment.hashCode()}"
        )

        fun toScreenData(
            game: UiGame,
            selectedSeat: TableWinds,
            isDiffsCalcsFeatureEnabled: Boolean
        ) = Triple(game, selectedSeat, isDiffsCalcsFeatureEnabled)

        with(viewLifecycleOwner.lifecycleScope) {
            launch {
                repeatOnLifecycle(STARTED) {
                    combine(
                        flow = gameViewModel.gameFlow,
                        flow2 = gameViewModel.selectedSeatFlow,
                        flow3 = gameViewModel.isDiffsCalcsFeatureEnabledFlow,
                        transform = ::toScreenData
                    ).first().let { initViews(it) }
                }
            }
        }
    }


    private fun initViews(screenData: Triple<UiGame, TableWinds, Boolean>) {
        val (game, selectedSeat, isDiffsCalcsFeatureEnabled) = screenData
        setPlayer(game, selectedSeat, isDiffsCalcsFeatureEnabled)
        setButtons(game)
    }

    private fun setPlayer(game: UiGame, selectedSeat: TableWinds, isDiffsCalcsFeatureEnabled: Boolean) {
        binding.iGameHuDialogWinnerContainer.ivTableSeatMediumSeatWind.setImageDrawable(
            when (selectedSeat) {
                TableWinds.EAST -> eastIcon
                TableWinds.SOUTH -> southIcon
                TableWinds.WEST -> westIcon
                TableWinds.NORTH -> northIcon
                else -> null
            }
        )
        binding.iGameHuDialogWinnerContainer.tvTableSeatMediumName.text = game.getPlayersNamesByCurrentSeat()[selectedSeat.code]
        if (isDiffsCalcsFeatureEnabled) {
            setPlayerDiffs(game, selectedSeat)
        }
    }

    private fun setPlayerDiffs(game: UiGame, playerSeat: TableWinds) {
        with(binding) {
            val playerDiffs = game.getTableDiffs().seatsDiffs[playerSeat.code]
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

    private fun setButtons(game: UiGame) {
        binding.btHandActionsDialogPenaltiesCancel.visibility =
            if (game.ongoingRound.areTherePenalties()) VISIBLE else GONE
    }

    private fun setListeners() {
        with(binding) {
            root.setOnSecureClickListener { dismiss() }
            btHandActionsDialogHu.setOnSecureClickListener {
                openHuDialog()
                isDialogCancelled = false
                dismiss()
            }
            btHandActionsDialogDraw.setOnSecureClickListener {
                gameViewModel.saveDrawRound()
                isDialogCancelled = false
                dismiss()
            }
            btHandActionsDialogPenalty.setOnSecureClickListener {
                openPenaltyDialog()
                isDialogCancelled = false
                dismiss()
            }
            btHandActionsDialogPenaltiesCancel.setOnSecureClickListener {
                AlertDialog.Builder(activity, R.style.AlertDialogStyleMM)
                    .setTitle(R.string.cancel_penalties)
                    .setMessage(R.string.are_you_sure)
                    .setPositiveButton(R.string.ok) { _, _ ->
                        gameViewModel.cancelPenalties()
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
            gameViewModel.unselectSelectedSeat()
        }
        super.onDismiss(dialog)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}