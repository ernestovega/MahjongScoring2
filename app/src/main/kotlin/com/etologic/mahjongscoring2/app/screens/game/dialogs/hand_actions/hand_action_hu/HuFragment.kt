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

package com.etologic.mahjongscoring2.app.screens.game.dialogs.hand_actions.hand_action_hu

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.base.BaseGameHandActionsDialogFragment
import com.etologic.mahjongscoring2.app.model.Seat
import com.etologic.mahjongscoring2.app.screens.game.GameUiState
import com.etologic.mahjongscoring2.app.utils.setOnSecureClickListener
import com.etologic.mahjongscoring2.business.model.dtos.HuData
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.model.entities.UiGame.Companion.MAX_MCR_POINTS
import com.etologic.mahjongscoring2.business.model.entities.UiGame.Companion.MIN_MCR_POINTS
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.EAST
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NONE
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NORTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.SOUTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.WEST
import com.etologic.mahjongscoring2.databinding.GameDialogHuFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HuFragment : BaseGameHandActionsDialogFragment() {

    companion object {
        const val TAG = "HuFragment"
    }

    private var greenColor: Int? = null
    private var eastIcon: Drawable? = null
    private var southIcon: Drawable? = null
    private var westIcon: Drawable? = null
    private var northIcon: Drawable? = null

    private var _binding: GameDialogHuFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GameDialogHuFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let {
            greenColor = ContextCompat.getColor(it, R.color.colorPrimary)
            eastIcon = ContextCompat.getDrawable(it, R.drawable.ic_east)
            southIcon = ContextCompat.getDrawable(it, R.drawable.ic_south)
            westIcon = ContextCompat.getDrawable(it, R.drawable.ic_west)
            northIcon = ContextCompat.getDrawable(it, R.drawable.ic_north)
        }
        initViews(gameViewModel.gameUiStateFlow.value as GameUiState.Loaded)
    }

    private fun initViews(uiGameState: GameUiState.Loaded) {
        if (uiGameState.game.gameId != UiGame.NOT_SET_GAME_ID) {
            val playersNamesByCurrentSeat = uiGameState.game.getPlayersNamesByCurrentSeat()
            val looser1Seat = TableWinds.asArray[(if (uiGameState.selectedSeat == EAST) SOUTH else EAST).code]
            val looser2Seat = TableWinds.asArray[(if (uiGameState.selectedSeat in listOf(EAST, SOUTH)) WEST else SOUTH).code]
            val looser3Seat = TableWinds.asArray[(if (uiGameState.selectedSeat in listOf(EAST, SOUTH, WEST)) NORTH else WEST).code]

            binding.iGameHuDialogWinnerContainer.ivTableSeatMediumSeatWind.setImageDrawable(getWindIcon(uiGameState.selectedSeat))
            binding.iGameHuDialogWinnerContainer.tvTableSeatMediumName.text = playersNamesByCurrentSeat[uiGameState.selectedSeat.code]

            binding.cdsGameHuDialog.initPlayers(
                listOf(
                    Seat(name = playersNamesByCurrentSeat[looser1Seat.code], seatWind = looser1Seat),
                    Seat(name = playersNamesByCurrentSeat[looser2Seat.code], seatWind = looser2Seat),
                    Seat(name = playersNamesByCurrentSeat[looser3Seat.code], seatWind = looser3Seat),
                )
            )

            setListeners(uiGameState.game, uiGameState.selectedSeat)
        }
    }

    private fun getWindIcon(wind: TableWinds?) =
        when (wind) {
            EAST -> eastIcon
            SOUTH -> southIcon
            WEST -> westIcon
            NORTH -> northIcon
            else -> null
        }

    private fun setListeners(game: UiGame, penalizedSeat: TableWinds) {
        with(binding) {
            btGameHuDialogCancel.setOnSecureClickListener { dismissDialog() }
            btGameHuDialogOk.setOnSecureClickListener {
                val winnerHandPoints = cnpGameHuDialog.getPoints()
                if (winnerHandPoints == null || winnerHandPoints < MIN_MCR_POINTS || winnerHandPoints > MAX_MCR_POINTS) {
                    cnpGameHuDialog.setError()
                } else {
                    if (cdsGameHuDialog.selectedSeatWind == NONE) {
                        val huData = HuData(
                            points = winnerHandPoints,
                            winnerInitialSeat = game.getPlayerInitialSeatByCurrentSeat(penalizedSeat),
                        )
                        gameViewModel.saveHuSelfPickRound(huData)
                    } else {
                        val huData = HuData(
                            points = winnerHandPoints,
                            winnerInitialSeat = game.getPlayerInitialSeatByCurrentSeat(penalizedSeat),
                            discarderInitialSeat = game.getPlayerInitialSeatByCurrentSeat(cdsGameHuDialog.selectedSeatWind),
                        )
                        gameViewModel.saveHuDiscardRound(huData)
                    }
                    dismissDialog()
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}