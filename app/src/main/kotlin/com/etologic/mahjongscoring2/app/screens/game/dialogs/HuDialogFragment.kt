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
package com.etologic.mahjongscoring2.app.screens.game.dialogs

import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.base.BaseGameDialogFragment
import com.etologic.mahjongscoring2.app.model.Seat
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HuDialogFragment : BaseGameDialogFragment() {

    companion object {
        const val TAG = "HuDialogFragment"
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
        isCancelable = false
        _binding = GameDialogHuFragmentBinding.inflate(inflater, container, false)
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
            greenColor = ContextCompat.getColor(it, R.color.colorPrimary)
            eastIcon = ContextCompat.getDrawable(it, R.drawable.ic_east)
            southIcon = ContextCompat.getDrawable(it, R.drawable.ic_south)
            westIcon = ContextCompat.getDrawable(it, R.drawable.ic_west)
            northIcon = ContextCompat.getDrawable(it, R.drawable.ic_north)
        }
        startObservingTable()
    }

    private fun startObservingTable() {
        Log.d("HuDialogFragment", "GameViewModel: ${gameViewModel.hashCode()} - parentFragment: ${parentFragment.hashCode()}")

        fun toScreenData(game: UiGame, penalizedSeat: TableWinds) = Pair(game, penalizedSeat)

        with(viewLifecycleOwner.lifecycleScope) {
            launch {
                repeatOnLifecycle(STARTED) {
                    combine(
                        flow = gameViewModel.gameFlow,
                        flow2 = gameViewModel.selectedSeatFlow,
                        transform = ::toScreenData,
                    ).first().let { initViews(it) }
                }
            }
        }
    }

    private suspend fun initViews(screenData: Pair<UiGame, TableWinds>) {
        val (game, penalizedSeat) = screenData
        if (game.gameId != UiGame.NOT_SET_GAME_ID) {
            val playersNamesByCurrentSeat = game.getPlayersNamesByCurrentSeat()
            val looser1Seat = TableWinds.asArray[(if (penalizedSeat == EAST) SOUTH else EAST).code]
            val looser2Seat = TableWinds.asArray[(if (penalizedSeat in listOf(EAST, SOUTH)) WEST else SOUTH).code]
            val looser3Seat = TableWinds.asArray[(if (penalizedSeat in listOf(EAST, SOUTH, WEST)) NORTH else WEST).code]

            binding.iGameHuDialogWinnerContainer.ivTableSeatMediumSeatWind.setImageDrawable(getWindIcon(penalizedSeat))
            binding.iGameHuDialogWinnerContainer.tvTableSeatMediumName.text = playersNamesByCurrentSeat[penalizedSeat.code]

            binding.cdsGameHuDialog.initPlayers(
                listOf(
                    Seat(name = playersNamesByCurrentSeat[looser1Seat.code], seatWind = looser1Seat),
                    Seat(name = playersNamesByCurrentSeat[looser2Seat.code], seatWind = looser2Seat),
                    Seat(name = playersNamesByCurrentSeat[looser3Seat.code], seatWind = looser3Seat),
                )
            )

            setListeners(game, penalizedSeat)
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
            btGameHuDialogCancel.setOnSecureClickListener { dismiss() }
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
                    dismiss()
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        gameViewModel.unselectSelectedSeat()
        super.onDismiss(dialog)
    }
}