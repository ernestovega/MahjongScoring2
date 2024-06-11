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
package com.etologic.mahjongscoring2.app.custom_views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.model.Seat
import com.etologic.mahjongscoring2.app.utils.second
import com.etologic.mahjongscoring2.app.utils.setOnSecureClickListener
import com.etologic.mahjongscoring2.app.utils.third
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NONE
import com.etologic.mahjongscoring2.databinding.CustomDiscarderSelectorBinding

class CustomDiscarderSelector(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet) {

    private var eastIcon: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_east)
    private var southIcon: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_south)
    private var westIcon: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_west)
    private var northIcon: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_north)
    private var selectedBg: Drawable? = ContextCompat.getDrawable(context, R.drawable.shape_rounded_corners_gray)
    private var accentColor: Int? = ContextCompat.getColor(context, R.color.colorAccent)
    private var grayMMColor: Int? = ContextCompat.getColor(context, R.color.grayMM)
    private lateinit var seats: List<Seat>
    var selectedSeatWind = NONE
        private set

    private var _binding: CustomDiscarderSelectorBinding? = null
    private val binding get() = _binding!!

    init {
        _binding = CustomDiscarderSelectorBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun initPlayers(seats: List<Seat>) {
        this.seats = seats
        setNames(seats.map { it.name })
        setIcons(seats.map { it.seatWind })
        setListeners()
    }

    private fun setNames(namesList: List<String>) {
        with(binding) {
            iDiscarderSelectorPlayer1Container.tvTableSeatSmallName.text = namesList.first()
            iDiscarderSelectorPlayer2Container.tvTableSeatSmallName.text = namesList.second()
            iDiscarderSelectorPlayer3Container.tvTableSeatSmallName.text = namesList.third()
            iDiscarderSelectorPlayer1Container.tvTableSeatSmallName.setTextColor(grayMMColor!!)
            iDiscarderSelectorPlayer2Container.tvTableSeatSmallName.setTextColor(grayMMColor!!)
            iDiscarderSelectorPlayer3Container.tvTableSeatSmallName.setTextColor(grayMMColor!!)
        }
    }

    private fun setIcons(seatsWinds: List<TableWinds>) {
        fun getWindIcon(wind: TableWinds?) = when (wind) {
            TableWinds.EAST -> eastIcon
            TableWinds.SOUTH -> southIcon
            TableWinds.WEST -> westIcon
            TableWinds.NORTH -> northIcon
            else -> null
        }

        with(binding) {
            iDiscarderSelectorPlayer1Container.ivTableSeatSmallSeatWind.setImageDrawable(getWindIcon(seatsWinds.first()))
            iDiscarderSelectorPlayer2Container.ivTableSeatSmallSeatWind.setImageDrawable(getWindIcon(seatsWinds.second()))
            iDiscarderSelectorPlayer3Container.ivTableSeatSmallSeatWind.setImageDrawable(getWindIcon(seatsWinds.third()))
            iDiscarderSelectorPlayer1Container.ivTableSeatSmallSeatWind.setColorFilter(grayMMColor!!)
            iDiscarderSelectorPlayer2Container.ivTableSeatSmallSeatWind.setColorFilter(grayMMColor!!)
            iDiscarderSelectorPlayer3Container.ivTableSeatSmallSeatWind.setColorFilter(grayMMColor!!)
        }
    }

    private fun setListeners() {
        with(binding) {
            iDiscarderSelectorPlayer1Container.llTableSeatSmallContainer.setOnSecureClickListener { selectDiscarder(0) }
            iDiscarderSelectorPlayer2Container.llTableSeatSmallContainer.setOnSecureClickListener { selectDiscarder(1) }
            iDiscarderSelectorPlayer3Container.llTableSeatSmallContainer.setOnSecureClickListener { selectDiscarder(2) }
        }
    }

    private fun selectDiscarder(seatIndex: Int) {
        if (seats[seatIndex].seatWind == selectedSeatWind) {
            selectedSeatWind = NONE
            unselectAll()
        } else {
            selectedSeatWind = seats[seatIndex].seatWind
            setupSelectedSeat()
        }
    }

    private fun unselectAll() {
        binding.iDiscarderSelectorPlayer1Container.llTableSeatSmallContainer.background = null
        binding.iDiscarderSelectorPlayer2Container.llTableSeatSmallContainer.background = null
        binding.iDiscarderSelectorPlayer3Container.llTableSeatSmallContainer.background = null
        binding.iDiscarderSelectorPlayer1Container.tvTableSeatSmallName.setTextColor(grayMMColor!!)
        binding.iDiscarderSelectorPlayer2Container.tvTableSeatSmallName.setTextColor(grayMMColor!!)
        binding.iDiscarderSelectorPlayer3Container.tvTableSeatSmallName.setTextColor(grayMMColor!!)
        binding.iDiscarderSelectorPlayer1Container.ivTableSeatSmallSeatWind.setColorFilter(grayMMColor!!)
        binding.iDiscarderSelectorPlayer2Container.ivTableSeatSmallSeatWind.setColorFilter(grayMMColor!!)
        binding.iDiscarderSelectorPlayer3Container.ivTableSeatSmallSeatWind.setColorFilter(grayMMColor!!)
    }

    private fun setupSelectedSeat() {
        val seatIndex = seats.indexOfFirst { it.seatWind == selectedSeatWind }

        unselectAll()

        when (seatIndex) {
            0 -> binding.iDiscarderSelectorPlayer1Container.llTableSeatSmallContainer
            1 -> binding.iDiscarderSelectorPlayer2Container.llTableSeatSmallContainer
            else -> binding.iDiscarderSelectorPlayer3Container.llTableSeatSmallContainer
        }.background = selectedBg

        when (seatIndex) {
            0 -> binding.iDiscarderSelectorPlayer1Container.tvTableSeatSmallName
            1 -> binding.iDiscarderSelectorPlayer2Container.tvTableSeatSmallName
            else -> binding.iDiscarderSelectorPlayer3Container.tvTableSeatSmallName
        }.setTextColor(accentColor!!)

        when (seatIndex) {
            0 -> binding.iDiscarderSelectorPlayer1Container.ivTableSeatSmallSeatWind
            1 -> binding.iDiscarderSelectorPlayer2Container.ivTableSeatSmallSeatWind
            else -> binding.iDiscarderSelectorPlayer3Container.ivTableSeatSmallSeatWind
        }.setColorFilter(accentColor!!)
    }
}
