package com.etologic.mahjongscoring2.app.custom_views

import android.content.Context
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
import java.util.*

class CustomDiscarderSelector(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet) {
    
    companion object {
        private const val FORMAT_PLACEHOLDER_SIGNED_NUMBER = "%+d"
    }
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
        setSelfpickSeat(tvDiscarderSelectorEastName, tvDiscarderSelectorEastPoints, EAST)
        setSelfpickSeat(tvDiscarderSelectorSouthName, tvDiscarderSelectorSouthPoints, SOUTH)
        setSelfpickSeat(tvDiscarderSelectorWestName, tvDiscarderSelectorWestPoints, WEST)
        setSelfpickSeat(tvDiscarderSelectorNorthName, tvDiscarderSelectorNorthPoints, NORTH)
    }
    
    private fun setSelfpickSeat(textView: TextView?, tvPoints: TextView?, wind: TableWinds) {
        val color = if (wind == winnerWind) greenColor else grayColor
        textView?.setTextColor(color)
        tvPoints?.setTextColor(color)
        tvPoints?.text =
            String.format(
                Locale.getDefault(),
                FORMAT_PLACEHOLDER_SIGNED_NUMBER,
                if (wind == winnerWind)
                    getHuSelfpickWinnerPoints(huPoints)
                else
                    getHuSelfpickDiscarderPoints(huPoints)
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
        val color = if (wind == winnerWind) greenColor else if (wind == looserWind) redColor else grayColor
        tvName?.setTextColor(color)
        tvPoints?.setTextColor(color)
        tvPoints?.text = String.format(
            Locale.getDefault(),
            FORMAT_PLACEHOLDER_SIGNED_NUMBER,
            when (wind) {
                winnerWind -> getHuDiscardWinnerPoints(huPoints)
                looserWind -> getHuDiscardDiscarderPoints(huPoints)
                else -> POINTS_DISCARD_NEUTRAL_PLAYERS
            }
        )
    }
}