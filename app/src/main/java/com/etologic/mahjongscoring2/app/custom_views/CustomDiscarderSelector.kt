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
package com.etologic.mahjongscoring2.app.custom_views

import android.content.Context
import android.graphics.Typeface.BOLD_ITALIC
import android.graphics.Typeface.NORMAL
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.extensions.setOnSecureClickListener
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel
import com.etologic.mahjongscoring2.business.model.entities.Table.Companion.POINTS_DISCARD_NEUTRAL_PLAYERS
import com.etologic.mahjongscoring2.business.model.entities.Table.Companion.getHuDiscardDiscarderPoints
import com.etologic.mahjongscoring2.business.model.entities.Table.Companion.getHuDiscardWinnerPoints
import com.etologic.mahjongscoring2.business.model.entities.Table.Companion.getHuSelfPickDiscarderPoints
import com.etologic.mahjongscoring2.business.model.entities.Table.Companion.getHuSelfPickWinnerPoints
import com.etologic.mahjongscoring2.business.model.enums.ScreenOrientation
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.EAST
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NONE
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NORTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.SOUTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.WEST
import com.etologic.mahjongscoring2.databinding.CustomDiscarderSelectorBinding

class CustomDiscarderSelector(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet) {

    private var grayColor = ContextCompat.getColor(context, R.color.grayMM)
    private var greenColor = ContextCompat.getColor(context, R.color.colorPrimary)
    private var redColor = ContextCompat.getColor(context, R.color.red)
    private var winnerWind = NONE
    private var looserWind = NONE
    private var huPoints = 0
    private var margin = 0

    private var _binding: CustomDiscarderSelectorBinding? = null
    private val binding get() = _binding!!

    init {
        _binding = CustomDiscarderSelectorBinding.inflate(LayoutInflater.from(context), this, true)
        margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, resources.displayMetrics).toInt()
    }

    internal fun getDiscarderCurrentSeat(): TableWinds = looserWind

    internal fun initPlayers(namesList: Array<String>, huPoints: Int, winnerWind: TableWinds) {
        this.huPoints = huPoints
        this.winnerWind = winnerWind
        setNames(namesList)
        setupSelfPick()
        setListeners()
    }

    private fun setNames(namesList: Array<String>) {
        with(binding) {
            tvDiscarderSelectorEastName.text = namesList[EAST.code]
            tvDiscarderSelectorSouthName.text = namesList[SOUTH.code]
            tvDiscarderSelectorWestName.text = namesList[WEST.code]
            tvDiscarderSelectorNorthName.text = namesList[NORTH.code]
        }
    }

    private fun setListeners() {
        with(binding) {
            llDiscarderSelectorEast.setOnSecureClickListener { selectDiscarder(EAST) }
            llDiscarderSelectorSouth.setOnSecureClickListener { selectDiscarder(SOUTH) }
            llDiscarderSelectorWest.setOnSecureClickListener { selectDiscarder(WEST) }
            llDiscarderSelectorNorth.setOnSecureClickListener { selectDiscarder(NORTH) }
        }
    }

    private fun setupSelfPick() {
        with(binding) {
            setSelfPickSeats(tvDiscarderSelectorEastName, tvDiscarderSelectorEastPoints, EAST)
            setSelfPickSeats(tvDiscarderSelectorSouthName, tvDiscarderSelectorSouthPoints, SOUTH)
            setSelfPickSeats(tvDiscarderSelectorWestName, tvDiscarderSelectorWestPoints, WEST)
            setSelfPickSeats(tvDiscarderSelectorNorthName, tvDiscarderSelectorNorthPoints, NORTH)
        }
    }

    private fun setSelfPickSeats(tvName: TextView?, tvPoints: TextView?, wind: TableWinds) {
        if (wind == winnerWind) setSeatTextsStyles(tvName, tvPoints, greenColor, BOLD_ITALIC)
        else setSeatTextsStyles(tvName, tvPoints, grayColor, NORMAL)
        tvPoints?.text = getSelfPickSeatPointsText(wind)
    }

    private fun setSeatTextsStyles(tvName: TextView?, tvPoints: TextView?, color: Int, textStyle: Int) {
        tvName?.setTextColor(color)
        tvPoints?.setTextColor(color)
        tvName?.setTypeface(null, textStyle)
        tvPoints?.setTypeface(null, textStyle)
    }

    private fun getSelfPickSeatPointsText(wind: TableWinds): String {
        return String.format(
            "%+d",
            if (wind == winnerWind) getHuSelfPickWinnerPoints(huPoints)
            else getHuSelfPickDiscarderPoints(huPoints)
        )
    }

    private fun selectDiscarder(wind: TableWinds) {
        if (wind == winnerWind || wind == looserWind) {
            looserWind = NONE
            setupSelfPick()
        } else {
            looserWind = wind
            setupDiscard()
        }
    }

    private fun setupDiscard() {
        with(binding) {
            setDiscardSeat(tvDiscarderSelectorEastName, tvDiscarderSelectorEastPoints, EAST)
            setDiscardSeat(tvDiscarderSelectorSouthName, tvDiscarderSelectorSouthPoints, SOUTH)
            setDiscardSeat(tvDiscarderSelectorWestName, tvDiscarderSelectorWestPoints, WEST)
            setDiscardSeat(tvDiscarderSelectorNorthName, tvDiscarderSelectorNorthPoints, NORTH)
        }
    }

    private fun setDiscardSeat(tvName: TextView?, tvPoints: TextView?, wind: TableWinds) {
        when (wind) {
            winnerWind -> setSeatTextsStyles(tvName, tvPoints, greenColor, BOLD_ITALIC)
            looserWind -> setSeatTextsStyles(tvName, tvPoints, redColor, BOLD_ITALIC)
            else -> setSeatTextsStyles(tvName, tvPoints, grayColor, NORMAL)
        }
        tvPoints?.text = getDiscardSeatPointsText(wind)
    }

    private fun getDiscardSeatPointsText(wind: TableWinds): String {
        return String.format(
            "%+d",
            when (wind) {
                winnerWind -> getHuDiscardWinnerPoints(huPoints)
                looserWind -> getHuDiscardDiscarderPoints(huPoints)
                else -> POINTS_DISCARD_NEUTRAL_PLAYERS
            }
        )
    }

    internal fun updateSeatsOrientation(screenOrientation: ScreenOrientation) {
        when (screenOrientation) {
            ScreenOrientation.PORTRAIT -> {
                binding.llDiscarderSelectorSouth.rotation = 0f
                binding.llDiscarderSelectorWest.rotation = 0f
                binding.llDiscarderSelectorNorth.rotation = 0f
            }

            ScreenOrientation.LANDSCAPE -> {
                binding.llDiscarderSelectorSouth.rotation = -90f
                binding.llDiscarderSelectorWest.rotation = 180f
                binding.llDiscarderSelectorNorth.rotation = 90f
            }
        }
    }
}