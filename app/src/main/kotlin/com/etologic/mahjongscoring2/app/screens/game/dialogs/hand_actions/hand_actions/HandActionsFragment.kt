/*
 *     Copyright © 2024  Ernesto Vega de la Iglesia Soria
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

package com.etologic.mahjongscoring2.app.screens.game.dialogs.hand_actions.hand_actions

import android.app.AlertDialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.base.BaseGameHandActionsDialogFragment
import com.etologic.mahjongscoring2.app.screens.game.GameUiState
import com.etologic.mahjongscoring2.app.screens.game.dialogs.hand_actions.HandActionsViewPagerAdapter.HandActions.HU
import com.etologic.mahjongscoring2.app.screens.game.dialogs.hand_actions.HandActionsViewPagerAdapter.HandActions.PENALTY
import com.etologic.mahjongscoring2.app.utils.setOnSecureClickListener
import com.etologic.mahjongscoring2.app.utils.toStringOrHyphen
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.databinding.GameDialogHandActionsFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HandActionsFragment : BaseGameHandActionsDialogFragment() {

    companion object {
        const val TAG = "HandActionsFragment"
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let {
            eastIcon = ContextCompat.getDrawable(it, R.drawable.ic_east)
            southIcon = ContextCompat.getDrawable(it, R.drawable.ic_south)
            westIcon = ContextCompat.getDrawable(it, R.drawable.ic_west)
            northIcon = ContextCompat.getDrawable(it, R.drawable.ic_north)
        }
        setListeners()
        initViews(gameViewModel.gameUiStateFlow.value as GameUiState.Loaded)
    }

    private fun initViews(uiGameState: GameUiState.Loaded) {
        setPlayer(uiGameState.game, uiGameState.selectedSeat, uiGameState.isDiffsCalcsFeatureEnabled)
        setButtons(uiGameState.game)
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
        with(binding.btHandActionsDialogPenaltiesCancel) {
            if (game.ongoingRound.areTherePenalties()) {
                setOnSecureClickListener {
                    AlertDialog.Builder(activity, R.style.AlertDialogStyleMM)
                        .setTitle(R.string.cancel_penalties)
                        .setMessage(R.string.are_you_sure)
                        .setPositiveButton(R.string.ok) { _, _ ->
                            gameViewModel.cancelPenalties()
                            dismissDialog()
                        }
                        .setNegativeButton(R.string.close, null)
                        .create()
                        .show()
                }
                visibility = VISIBLE
            } else {
                visibility = GONE
            }
        }
    }

    private fun setListeners() {
        with(binding) {
            root.setOnSecureClickListener { dismissDialog() }
            btHandActionsDialogHu.setOnSecureClickListener { showPage(HU) }
            btHandActionsDialogDraw.setOnSecureClickListener {
                gameViewModel.saveDrawRound()
                isDialogCancelled = false
                dismissDialog()
            }
            btHandActionsDialogPenalty.setOnSecureClickListener { showPage(PENALTY) }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}