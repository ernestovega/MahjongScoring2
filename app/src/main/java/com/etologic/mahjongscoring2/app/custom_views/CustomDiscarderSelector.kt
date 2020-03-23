package com.etologic.mahjongscoring2.app.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.*
import com.etologic.mahjongscoring2.business.model.enums.WinType
import com.etologic.mahjongscoring2.business.model.enums.WinType.RON
import com.etologic.mahjongscoring2.business.model.enums.WinType.TSUMO
import kotlinx.android.synthetic.main.custom_discarder_selector.view.*

class CustomDiscarderSelector(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet) {
    
    private val grayColor = ContextCompat.getColor(context, R.color.grayMM)
    private val greenColor = ContextCompat.getColor(context, R.color.colorPrimary)
    private val redColor = ContextCompat.getColor(context, R.color.red)
    private var winnerWind = NONE
    private var looserWind = NONE
    private var huPoints = 0
    
    init {
        LayoutInflater.from(context).inflate(R.layout.custom_discarder_selector, this, true)
        setListeners()
    }
    
    private fun setListeners() {
        llDiscarderSelectorEast.setOnClickListener { selectDiscarder(EAST) }
        llDiscarderSelectorSouth.setOnClickListener { selectDiscarder(SOUTH) }
        llDiscarderSelectorWest.setOnClickListener { selectDiscarder(WEST) }
        llDiscarderSelectorNorth.setOnClickListener { selectDiscarder(NORTH) }
    }
    
    private fun selectDiscarder(wind: TableWinds) {
        if (wind == winnerWind || wind == looserWind) {
            looserWind = NONE
            resetPlayers()
        } else {
            looserWind = wind
            setRonPoints()
        }
    }
    
    private fun resetPlayers() {
        llDiscarderSelectorEast.tag = false
        llDiscarderSelectorSouth.tag = false
        llDiscarderSelectorWest.tag = false
        llDiscarderSelectorNorth.tag = false
        setTsumoPoints()
    }
    
    private fun setTsumoPoints() {
        tvDiscarderSelectorEastName.setTextColor(if (EAST == winnerWind) greenColor else grayColor)
        tvDiscarderSelectorSouthName.setTextColor(if (SOUTH == winnerWind) greenColor else grayColor)
        tvDiscarderSelectorWestName.setTextColor(if (WEST == winnerWind) greenColor else grayColor)
        tvDiscarderSelectorNorthName.setTextColor(if (NORTH == winnerWind) greenColor else grayColor)
        
        tvDiscarderSelectorEastPoints.setTextColor(if (EAST == winnerWind) greenColor else grayColor)
        tvDiscarderSelectorSouthPoints.setTextColor(if (SOUTH == winnerWind) greenColor else grayColor)
        tvDiscarderSelectorWestPoints.setTextColor(if (WEST == winnerWind) greenColor else grayColor)
        tvDiscarderSelectorNorthPoints.setTextColor(if (NORTH == winnerWind) greenColor else grayColor)
        
        tvDiscarderSelectorEastPoints.text = if (EAST == winnerWind) "+${(huPoints + 8) * 3}" else "-${(huPoints + 8)}"
        tvDiscarderSelectorSouthPoints.text = if (SOUTH == winnerWind) "+${(huPoints + 8) * 3}" else "-${(huPoints + 8)}"
        tvDiscarderSelectorWestPoints.text = if (WEST == winnerWind) "+${(huPoints + 8) * 3}" else "-${(huPoints + 8)}"
        tvDiscarderSelectorNorthPoints.text = if (NORTH == winnerWind) "+${(huPoints + 8) * 3}" else "-${(huPoints + 8)}"
    }
    
    private fun setRonPoints() {
        tvDiscarderSelectorEastName.setTextColor(if (EAST == winnerWind) greenColor else if (EAST == looserWind) redColor else grayColor)
        tvDiscarderSelectorSouthName.setTextColor(if (SOUTH == winnerWind) greenColor else if (SOUTH == looserWind) redColor else grayColor)
        tvDiscarderSelectorWestName.setTextColor(if (WEST == winnerWind) greenColor else if (WEST == looserWind) redColor else grayColor)
        tvDiscarderSelectorNorthName.setTextColor(if (NORTH == winnerWind) greenColor else if (NORTH == looserWind) redColor else grayColor)
        
        tvDiscarderSelectorEastPoints.setTextColor(if (EAST == winnerWind) greenColor else if (EAST == looserWind) redColor else grayColor)
        tvDiscarderSelectorSouthPoints.setTextColor(if (SOUTH == winnerWind) greenColor else if (SOUTH == looserWind) redColor else grayColor)
        tvDiscarderSelectorWestPoints.setTextColor(if (WEST == winnerWind) greenColor else if (WEST == looserWind) redColor else grayColor)
        tvDiscarderSelectorNorthPoints.setTextColor(if (NORTH == winnerWind) greenColor else if (NORTH == looserWind) redColor else grayColor)
        
        tvDiscarderSelectorEastPoints.text =
            if (EAST == winnerWind) "+${huPoints + 8 * 3}" else if (EAST == looserWind) "-${(huPoints + 8)}" else "-8"
        tvDiscarderSelectorSouthPoints.text =
            if (SOUTH == winnerWind) "+${(huPoints + 8) * 3}" else if (SOUTH == looserWind) "-${(huPoints + 8)}" else "-8"
        tvDiscarderSelectorWestPoints.text =
            if (WEST == winnerWind) "+${(huPoints + 8) * 3}" else if (WEST == looserWind) "-${(huPoints + 8)}" else "-8"
        tvDiscarderSelectorNorthPoints.text =
            if (NORTH == winnerWind) "+${(huPoints + 8) * 3}" else if (NORTH == looserWind) "-${(huPoints + 8)}" else "-8"
    }
    
    internal fun initPlayers(namesList: Array<String>, huPoints: Int, winnerWind: TableWinds) {
        this.huPoints = huPoints
        this.winnerWind = winnerWind
        resetPlayers()
        
        tvDiscarderSelectorEastName?.text = namesList[0]
        tvDiscarderSelectorSouthName?.text = namesList[1]
        tvDiscarderSelectorWestName?.text = namesList[2]
        tvDiscarderSelectorNorthName?.text = namesList[3]
        
        setTsumoPoints()
    }
    
    internal fun isRonOrTsumo(): WinType = if (looserWind == NONE) TSUMO else RON
    
    internal fun togglePoints(isVisible: Boolean) {
        if (isVisible) {
            tvDiscarderSelectorEastPoints.visibility = VISIBLE
            tvDiscarderSelectorSouthPoints.visibility = VISIBLE
            tvDiscarderSelectorWestPoints.visibility = VISIBLE
            tvDiscarderSelectorNorthPoints.visibility = VISIBLE
        } else {
            tvDiscarderSelectorEastPoints.visibility = GONE
            tvDiscarderSelectorSouthPoints.visibility = GONE
            tvDiscarderSelectorWestPoints.visibility = GONE
            tvDiscarderSelectorNorthPoints.visibility = GONE
        }
    }
    
    internal fun getDiscarderCurrentSeat(): TableWinds = looserWind
}