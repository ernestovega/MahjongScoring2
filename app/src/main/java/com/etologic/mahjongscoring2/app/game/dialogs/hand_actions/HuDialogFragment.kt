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
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.extensions.setOnSecureClickListener
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel
import com.etologic.mahjongscoring2.app.model.Seat
import com.etologic.mahjongscoring2.business.model.entities.Table.Companion.MAX_MCR_POINTS
import com.etologic.mahjongscoring2.business.model.entities.Table.Companion.MIN_MCR_POINTS
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.EAST
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NONE
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NORTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.SOUTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.WEST
import com.etologic.mahjongscoring2.databinding.GameHuDialogFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HuDialogFragment : AppCompatDialogFragment() {

    companion object {
        const val TAG = "HuDialogFragment"
    }

    private var greenColor: Int? = null
    private var eastIcon: Drawable? = null
    private var southIcon: Drawable? = null
    private var westIcon: Drawable? = null
    private var northIcon: Drawable? = null
    private var isDialogCancelled = true

    private var _binding: GameHuDialogFragmentBinding? = null
    private val binding get() = _binding!!

    private val activityViewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        isCancelable = false
        _binding = GameHuDialogFragmentBinding.inflate(inflater, container, false)
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
        initViews()
        setListeners()
    }

    private fun initViews() {
        val playersNamesByCurrentSeat = activityViewModel.getCurrentTable().value?.getPlayersNamesByCurrentSeat()
        val winnerSeat = activityViewModel.getSelectedSeat().value ?: NONE
        val looser1Seat = TableWinds.asArray[(if (winnerSeat == EAST) SOUTH else EAST).code]
        val looser2Seat = TableWinds.asArray[(if (winnerSeat in listOf(EAST, SOUTH)) WEST else SOUTH).code]
        val looser3Seat = TableWinds.asArray[(if (winnerSeat in listOf(EAST, SOUTH, WEST)) NORTH else WEST).code]

        binding.iGameHuDialogWinnerContainer.ivTableSeatMediumSeatWind.setImageDrawable(getWindIcon(winnerSeat))
        binding.iGameHuDialogWinnerContainer.tvTableSeatMediumName.text = winnerSeat.code.let { playersNamesByCurrentSeat?.get(it) ?: "" }

        binding.cdsGameHuDialog.initPlayers(
            listOf(
                Seat(name = playersNamesByCurrentSeat?.get(looser1Seat.code) ?: "", seatWind = looser1Seat),
                Seat(name = playersNamesByCurrentSeat?.get(looser2Seat.code) ?: "", seatWind = looser2Seat),
                Seat(name = playersNamesByCurrentSeat?.get(looser3Seat.code) ?: "", seatWind = looser3Seat),
            )
        )
    }

    private fun getWindIcon(wind: TableWinds?) =
        when (wind) {
            EAST -> eastIcon
            SOUTH -> southIcon
            WEST -> westIcon
            NORTH -> northIcon
            else -> null
        }

    private fun setListeners() {
        with(binding) {
            btGameHuDialogCancel.setOnSecureClickListener { dismiss() }
            btGameHuDialogOk.setOnSecureClickListener {
                val winnerHandPoints = cnpGameHuDialog.getPoints()
                if (winnerHandPoints == null || winnerHandPoints < MIN_MCR_POINTS || winnerHandPoints > MAX_MCR_POINTS) {
                    cnpGameHuDialog.setError()
                } else {
                    if (cdsGameHuDialog.selectedSeatWind == NONE) {
                        activityViewModel.saveTsumoRound(winnerHandPoints)
                    } else {
                        activityViewModel.saveRonRound(cdsGameHuDialog.selectedSeatWind, winnerHandPoints)
                    }
                    isDialogCancelled = false
                    dismiss()
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        if (isDialogCancelled) {
            activityViewModel.unselectSelectedSeat()
        }
        super.onDismiss(dialog)
    }
}