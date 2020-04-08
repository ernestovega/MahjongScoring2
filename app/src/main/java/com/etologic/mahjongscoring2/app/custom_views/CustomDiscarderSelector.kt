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
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.extensions.setOnSecureClickListener
import com.etologic.mahjongscoring2.business.model.entities.Table.Companion.POINTS_DISCARD_NEUTRAL_PLAYERS
import com.etologic.mahjongscoring2.business.model.entities.Table.Companion.getHuDiscardDiscarderPoints
import com.etologic.mahjongscoring2.business.model.entities.Table.Companion.getHuDiscardWinnerPoints
import com.etologic.mahjongscoring2.business.model.entities.Table.Companion.getHuSelfpickDiscarderPoints
import com.etologic.mahjongscoring2.business.model.entities.Table.Companion.getHuSelfpickWinnerPoints
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.*
import kotlinx.android.synthetic.main.custom_discarder_selector.view.*

class CustomDiscarderSelector(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet) {
    
    private var grayColor = ContextCompat.getColor(context, R.color.grayMM)
    private var greenColor = ContextCompat.getColor(context, R.color.colorPrimary)
    private var redColor = ContextCompat.getColor(context, R.color.red)
    private var winnerWind = NONE
    private var looserWind = NONE
    private var huPoints = 0
    
    init {
        LayoutInflater.from(context).inflate(R.layout.custom_discarder_selector, this, true)
    }
    
    internal fun getDiscarderCurrentSeat(): TableWinds = looserWind
    
    internal fun initPlayers(namesList: Array<String>, huPoints: Int, winnerWind: TableWinds) {
        this.huPoints = huPoints
        this.winnerWind = winnerWind
        setNames(namesList)
        setupSelfpick()
        setListeners()
    }
    
    private fun setNames(namesList: Array<String>) {
        tvDiscarderSelectorEastName?.text = namesList[EAST.code]
        tvDiscarderSelectorSouthName?.text = namesList[SOUTH.code]
        tvDiscarderSelectorWestName?.text = namesList[WEST.code]
        tvDiscarderSelectorNorthName?.text = namesList[NORTH.code]
    }
    
    private fun setListeners() {
        llDiscarderSelectorEast.setOnSecureClickListener { selectDiscarder(EAST) }
        llDiscarderSelectorSouth.setOnSecureClickListener { selectDiscarder(SOUTH) }
        llDiscarderSelectorWest.setOnSecureClickListener { selectDiscarder(WEST) }
        llDiscarderSelectorNorth.setOnSecureClickListener { selectDiscarder(NORTH) }
    }
    
    private fun setupSelfpick() {
        setSelfpickSeats(tvDiscarderSelectorEastName, tvDiscarderSelectorEastPoints, EAST)
        setSelfpickSeats(tvDiscarderSelectorSouthName, tvDiscarderSelectorSouthPoints, SOUTH)
        setSelfpickSeats(tvDiscarderSelectorWestName, tvDiscarderSelectorWestPoints, WEST)
        setSelfpickSeats(tvDiscarderSelectorNorthName, tvDiscarderSelectorNorthPoints, NORTH)
    }
    
    private fun setSelfpickSeats(tvName: TextView?, tvPoints: TextView?, wind: TableWinds) {
        if (wind == winnerWind) setSeatTextsStyles(tvName, tvPoints, greenColor, BOLD_ITALIC)
        else setSeatTextsStyles(tvName, tvPoints, grayColor, NORMAL)
        tvPoints?.text = getSelfpickSeatPointsText(wind)
    }
    
    private fun setSeatTextsStyles(tvName: TextView?, tvPoints: TextView?, color: Int, textStyle: Int) {
        tvName?.setTextColor(color)
        tvPoints?.setTextColor(color)
        tvName?.setTypeface(null, textStyle)
        tvPoints?.setTypeface(null, textStyle)
    }
    
    private fun getSelfpickSeatPointsText(wind: TableWinds): String {
        return String.format(
            "%+d",
            if (wind == winnerWind) getHuSelfpickWinnerPoints(huPoints)
            else getHuSelfpickDiscarderPoints(huPoints)
        )
    }
    
    private fun selectDiscarder(wind: TableWinds) {
        if (wind == winnerWind || wind == looserWind) {
            looserWind = NONE
            setupSelfpick()
        } else {
            looserWind = wind
            setupDiscard()
        }
    }
    
    private fun setupDiscard() {
        setDiscardSeat(tvDiscarderSelectorEastName, tvDiscarderSelectorEastPoints, EAST)
        setDiscardSeat(tvDiscarderSelectorSouthName, tvDiscarderSelectorSouthPoints, SOUTH)
        setDiscardSeat(tvDiscarderSelectorWestName, tvDiscarderSelectorWestPoints, WEST)
        setDiscardSeat(tvDiscarderSelectorNorthName, tvDiscarderSelectorNorthPoints, NORTH)
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
}