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

package com.etologic.mahjongscoring2.app.screens.game.dialogs.hand_actions.hand_action_penalty

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.base.BaseGameHandActionsDialogFragment
import com.etologic.mahjongscoring2.app.screens.game.GameUiState
import com.etologic.mahjongscoring2.app.utils.setOnSecureClickListener
import com.etologic.mahjongscoring2.business.model.dtos.PenaltyData
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.model.entities.UiGame.Companion.NUM_NO_WINNER_PLAYERS
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.databinding.GameDialogPenaltyFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PenaltyFragment : BaseGameHandActionsDialogFragment() {

    companion object {
        const val TAG = "PenaltyFragment"
    }

    private var eastIcon: Drawable? = null
    private var southIcon: Drawable? = null
    private var westIcon: Drawable? = null
    private var northIcon: Drawable? = null

    private var _binding: GameDialogPenaltyFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GameDialogPenaltyFragmentBinding.inflate(inflater, container, false)
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
        initViews(gameViewModel.gameUiStateFlow.value as GameUiState.Loaded)
    }

    private fun initViews(uiGameState: GameUiState.Loaded) {
        if (uiGameState.game.gameId != UiGame.NOT_SET_GAME_ID) {
            with(binding.iPenaltyDialogPlayerContainer) {
                val playersNamesByCurrentSeat = uiGameState.game.getPlayersNamesByCurrentSeat()
                ivTableSeatMediumSeatWind.setImageDrawable(getWindIcon(uiGameState.selectedSeat))
                tvTableSeatMediumName.text = playersNamesByCurrentSeat[uiGameState.selectedSeat.code]
            }
            setListeners(uiGameState.game, uiGameState.selectedSeat)
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

    private fun setListeners(game: UiGame, selectedSeat: TableWinds) {
        with(binding) {
            btPenaltyDialogCancel.setOnSecureClickListener { dismissDialog() }
            btPenaltyDialogSave.setOnSecureClickListener {
                val penaltyPoints = cnpPenaltyDialog.getPoints() ?: 0
                if (penaltyPoints > 0) {
                    if (cbPenaltyDialog.isChecked) {
                        if (penaltyPoints % NUM_NO_WINNER_PLAYERS == 0)
                            saveAndFinish(game, selectedSeat, penaltyPoints, isDivided = true)
                        else {
                            cnpPenaltyDialog.setError()
                            cbPenaltyDialog.error = ""
                        }
                    } else
                        saveAndFinish(game, selectedSeat, penaltyPoints, isDivided = false)
                } else
                    cnpPenaltyDialog.setError()
            }
        }
    }

    private fun saveAndFinish(game: UiGame, selectedSeat: TableWinds, penaltyPoints: Int, isDivided: Boolean) {
        val penaltyData = PenaltyData(
            points = penaltyPoints,
            isDivided = isDivided,
            penalizedPlayerInitialSeat = game.getPlayerInitialSeatByCurrentSeat(selectedSeat)
        )
        gameViewModel.savePenalty(penaltyData)
        dismissDialog()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}