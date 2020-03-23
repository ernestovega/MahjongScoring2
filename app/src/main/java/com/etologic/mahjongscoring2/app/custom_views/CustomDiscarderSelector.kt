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
        if (wind != winnerWind) {
            resetPlayers()
            
            if (looserWind == wind)
                looserWind = NONE
            else {
                looserWind = wind
    
                when (wind) {
                    NONE,
                    EAST -> {
                        llDiscarderSelectorEast.tag = true
                        tvDiscarderSelectorEastName.setTextColor(redColor)
                        tvDiscarderSelectorEastPoints.setTextColor(redColor)
                    }
                    SOUTH -> {
                        llDiscarderSelectorSouth.tag = true
                        tvDiscarderSelectorSouthName.setTextColor(redColor)
                        tvDiscarderSelectorSouthPoints.setTextColor(redColor)
                    }
                    WEST -> {
                        llDiscarderSelectorWest.tag = true
                        tvDiscarderSelectorWestName.setTextColor(redColor)
                        tvDiscarderSelectorWestPoints.setTextColor(redColor)
                    }
                    NORTH -> {
                        llDiscarderSelectorNorth.tag = true
                        tvDiscarderSelectorNorthName.setTextColor(redColor)
                        tvDiscarderSelectorNorthPoints.setTextColor(redColor)
                    }
                }
            }
        }
    }
    
    private fun resetPlayers() {
        llDiscarderSelectorEast.tag = false
        tvDiscarderSelectorEastName.setTextColor(if(winnerWind == EAST) greenColor else grayColor)
        tvDiscarderSelectorEastPoints.setTextColor(if(winnerWind == EAST) greenColor else grayColor)
        
        llDiscarderSelectorSouth.tag = false
        tvDiscarderSelectorSouthName.setTextColor(if(winnerWind == SOUTH) greenColor else grayColor)
        tvDiscarderSelectorSouthPoints.setTextColor(if(winnerWind == SOUTH) greenColor else grayColor)
        
        llDiscarderSelectorWest.tag = false
        tvDiscarderSelectorWestName.setTextColor(if(winnerWind == WEST) greenColor else grayColor)
        tvDiscarderSelectorWestPoints.setTextColor(if(winnerWind == WEST) greenColor else grayColor)
        
        llDiscarderSelectorNorth.tag = false
        tvDiscarderSelectorNorthName.setTextColor(if(winnerWind == NORTH) greenColor else grayColor)
        tvDiscarderSelectorNorthPoints.setTextColor(if(winnerWind == NORTH) greenColor else grayColor)
    }
    
    internal fun initPlayers(namesList: Array<String>, winnerWind: TableWinds) {
        this.winnerWind = winnerWind
        resetPlayers()
        
        tvDiscarderSelectorEastName?.text = namesList[0]
        tvDiscarderSelectorSouthName?.text = namesList[1]
        tvDiscarderSelectorWestName?.text = namesList[2]
        tvDiscarderSelectorNorthName?.text = namesList[3]
        
        tvDiscarderSelectorNorthPoints.text = "0"
        tvDiscarderSelectorWestPoints.text = "0"
        tvDiscarderSelectorSouthPoints.text = "0"
        tvDiscarderSelectorEastPoints.text = "0"
    }
    
    internal fun isRonOrTsumo(): WinType =
        if (tvDiscarderSelectorEastName?.tag == true ||
            tvDiscarderSelectorSouthName?.tag == true ||
            tvDiscarderSelectorWestName?.tag == true ||
            tvDiscarderSelectorNorthName?.tag == true
        ) RON else TSUMO
    
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
    
    fun getDiscarderCurrentSeat(): TableWinds =
        when {
            tvDiscarderSelectorEastName?.tag == true -> EAST
            tvDiscarderSelectorSouthName?.tag == true -> SOUTH
            tvDiscarderSelectorWestName?.tag == true -> WEST
            tvDiscarderSelectorNorthName?.tag == true -> NORTH
            else -> NONE
        }
}