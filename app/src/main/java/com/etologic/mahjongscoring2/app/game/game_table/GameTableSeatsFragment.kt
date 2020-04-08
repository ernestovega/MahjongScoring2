package com.etologic.mahjongscoring2.app.game.game_table

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity.BOTTOM
import android.view.Gravity.TOP
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getDrawable
import androidx.fragment.app.Fragment
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.R.color
import com.etologic.mahjongscoring2.R.layout
import com.etologic.mahjongscoring2.app.extensions.setOnSecureClickListener
import com.etologic.mahjongscoring2.app.model.SeatStates
import com.etologic.mahjongscoring2.app.model.SeatStates.*
import com.etologic.mahjongscoring2.business.model.entities.Table
import com.etologic.mahjongscoring2.business.model.enums.ScreenOrientation
import com.etologic.mahjongscoring2.business.model.enums.ScreenOrientation.LANDSCAPE
import com.etologic.mahjongscoring2.business.model.enums.ScreenOrientation.PORTRAIT
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.*
import kotlinx.android.synthetic.main.game_table_seat_east.*
import kotlinx.android.synthetic.main.game_table_seat_north.*
import kotlinx.android.synthetic.main.game_table_seat_south.*
import kotlinx.android.synthetic.main.game_table_seat_west.*
import java.util.Locale.getDefault

class GameTableSeatsFragment : Fragment() {
    
    companion object {
        internal const val TAG = "GameTableSeatsFragment"
    }
    
    internal interface TableSeatsListener {
        fun onSeatClick(wind: TableWinds)
    }
    
    //RESOURCES
    private var eastIcon: Drawable? = null
    private var southIcon: Drawable? = null
    private var westIcon: Drawable? = null
    private var northIcon: Drawable? = null
    private var grayNormalColor: Int? = null
    private var grayDisabledColor: Int? = null
    private var accentColor: Int? = null
    private var redColor: Int? = null
    private var greenColor: Int? = null
    private var purplePenalty: Int? = null
    
    //FIELDS
    private var listener: TableSeatsListener? = null
    private var selectedPlayer: TableWinds = NONE
    private var areSeatsDisabled: Boolean = false
    
    //PUBLIC
    internal fun setTableSeatsListener(tableSeatsListener: TableSeatsListener) {
        listener = tableSeatsListener
    }
    
    internal fun updateSeatState(wind: TableWinds) {
        selectedPlayer = wind
        setStates(getSeatsStates())
    }
    
    private fun getSeatsStates(): Array<SeatStates> =
        when (selectedPlayer) {
            NONE -> arrayOf(NORMAL, NORMAL, NORMAL, NORMAL)
            EAST -> arrayOf(SELECTED, NORMAL, NORMAL, NORMAL)
            SOUTH -> arrayOf(NORMAL, SELECTED, NORMAL, NORMAL)
            WEST -> arrayOf(NORMAL, NORMAL, SELECTED, NORMAL)
            NORTH -> arrayOf(NORMAL, NORMAL, NORMAL, SELECTED)
        }
    
    internal fun setSeats(table: Table) {
        selectedPlayer = NONE
        setStates(getSeatsStates(table))
        setPoints(table.getPlayersTotalPointsStringByCurrentSeat())
        setPenalties(table.getPlayersPenaltiesByCurrentSeat())
        setWinds(table.getSeatsCurrentWind(table.rounds.size))
        setNames(table.getPlayersNamesByCurrentSeat())
    }
    
    private fun getSeatsStates(table: Table): Array<SeatStates> =
        if (table.rounds.last().isEnded)
            arrayOf(DISABLED, DISABLED, DISABLED, DISABLED)
        else
            getSeatsStates()
    
    private fun setStates(states: Array<SeatStates>) {
        areSeatsDisabled = states[0] == DISABLED && states[1] == DISABLED && states[2] == DISABLED && states[3] == DISABLED
        setState(ivTableSeatEastSeatWindIcon, tvTableSeatEastName, tvTableSeatEastPoints, states[0])
        setState(ivTableSeatSouthSeatWindIcon, tvTableSeatSouthName, tvTableSeatSouthPoints, states[1])
        setState(ivTableSeatWestSeatWindIcon, tvTableSeatWestName, tvTableSeatWestPoints, states[2])
        setState(ivTableSeatNorthSeatWindIcon, tvTableSeatNorthName, tvTableSeatNorthPoints, states[3])
    }
    
    private fun setState(ivWind: ImageView?, tvName: TextView, tvPoints: TextView, state: SeatStates?) {
        val stateColor = when (state) {
            SELECTED -> accentColor
            DISABLED -> grayDisabledColor
            else -> grayNormalColor
        }
        stateColor?.let {
            ivWind?.setColorFilter(it)
            tvName.setTextColor(it)
            tvPoints.setTextColor(it)
        }
    }
    
    private fun setPoints(points: List<String>) {
        tvTableSeatEastPoints.text = points[EAST.code]
        tvTableSeatSouthPoints.text = points[SOUTH.code]
        tvTableSeatWestPoints.text = points[WEST.code]
        tvTableSeatNorthPoints.text = points[NORTH.code]
    }
    
    private fun setPenalties(penalties: IntArray) {
        setPenaltyPoints(tvTableSeatEastPenaltyPoints, penalties[EAST.code])
        setPenaltyPoints(tvTableSeatSouthPenaltyPoints, penalties[SOUTH.code])
        setPenaltyPoints(tvTableSeatWestPenaltyPoints, penalties[WEST.code])
        setPenaltyPoints(tvTableSeatNorthPenaltyPoints, penalties[NORTH.code])
    }
    
    private fun setPenaltyPoints(textView: TextView, penaltyPoints: Int) {
        textView.text = String.format(getDefault(), "%+d", penaltyPoints)
        (if (penaltyPoints < 0) redColor else greenColor)?.let { textView.setTextColor(it) }
        textView.visibility = if (penaltyPoints != 0) VISIBLE else GONE
    }
    
    private fun setWinds(winds: Array<TableWinds>) {
        setWindIcon(ivTableSeatEastSeatWindIcon, winds[EAST.code])
        setWindIcon(ivTableSeatSouthSeatWindIcon, winds[SOUTH.code])
        setWindIcon(ivTableSeatWestSeatWindIcon, winds[WEST.code])
        setWindIcon(ivTableSeatNorthSeatWindIcon, winds[NORTH.code])
    }
    
    private fun setWindIcon(imageView: ImageView?, wind: TableWinds?) {
        when (wind) {
            EAST -> setWind(imageView, eastIcon)
            SOUTH -> setWind(imageView, southIcon)
            WEST -> setWind(imageView, westIcon)
            NORTH -> setWind(imageView, northIcon)
            NONE -> imageView?.visibility = INVISIBLE
        }
    }
    
    private fun setWind(imageView: ImageView?, icon: Drawable?) {
        if (imageView?.visibility != VISIBLE)
            imageView?.visibility = VISIBLE
        imageView?.setImageDrawable(icon)
    }
    
    private fun setNames(names: Array<String>) {
        setName(tvTableSeatEastName, names[EAST.code])
        setName(tvTableSeatSouthName, names[SOUTH.code])
        setName(tvTableSeatWestName, names[WEST.code])
        setName(tvTableSeatNorthName, names[NORTH.code])
    }
    
    private fun setName(textView: TextView, name: String?) {
        textView.text = name ?: ""
    }
    
    //LIFECYCLE
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layout.game_table_seats_fragment, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let {
            eastIcon = getDrawable(it, R.drawable.ic_east)
            southIcon = getDrawable(it, R.drawable.ic_south)
            westIcon = getDrawable(it, R.drawable.ic_west)
            northIcon = getDrawable(it, R.drawable.ic_north)
            grayNormalColor = getColor(it, color.grayMM)
            grayDisabledColor = getColor(it, color.gray)
            accentColor = getColor(it, color.colorAccent)
            redColor = getColor(it, color.red)
            greenColor = getColor(it, color.colorPrimary)
            purplePenalty = getColor(it, color.purplePenalty)
        }
        initListeners()
    }
    
    private fun initListeners() {
        ivTableSeatEastSeatWindIcon?.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(EAST) }
        tvTableSeatEastName?.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(EAST) }
        tvTableSeatEastPoints?.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(EAST) }
        vGameTableSeatEastAuxStart?.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(EAST) }
        vGameTableSeatEastAuxEnd?.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(EAST) }
        tvTableSeatEastPenaltyPoints?.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(EAST) }
        ivTableSeatSouthSeatWindIcon?.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(SOUTH) }
        tvTableSeatSouthName?.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(SOUTH) }
        tvTableSeatSouthPoints?.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(SOUTH) }
        vGameTableSeatSouthAuxStart?.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(SOUTH) }
        vGameTableSeatSouthAuxEnd?.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(SOUTH) }
        tvTableSeatSouthPenaltyPoints?.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(SOUTH) }
        ivTableSeatWestSeatWindIcon?.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(WEST) }
        tvTableSeatWestName?.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(WEST) }
        tvTableSeatWestPoints?.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(WEST) }
        vGameTableSeatWestAuxStart?.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(WEST) }
        vGameTableSeatWestAuxEnd?.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(WEST) }
        tvTableSeatWestPenaltyPoints?.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(WEST) }
        ivTableSeatNorthSeatWindIcon?.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(NORTH) }
        tvTableSeatNorthName?.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(NORTH) }
        tvTableSeatNorthPoints?.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(NORTH) }
        vGameTableSeatNorthAuxStart?.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(NORTH) }
        vGameTableSeatNorthAuxEnd?.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(NORTH) }
        tvTableSeatNorthPenaltyPoints?.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(NORTH) }
    }
    
    internal fun updateSeatsOrientation(screenOrientation: ScreenOrientation) {
        when (screenOrientation) {
            PORTRAIT -> {
                val margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f * 2, resources.displayMetrics).toInt()
                rlTableSeatEastContainer?.setPadding(0, margin, 0, 0)
                rlTableSeatWestContainer?.setPadding(0, 0, 0, margin)
                rlTableSeatWestContainer?.gravity = BOTTOM
                rlTableSeatSouthContainer?.rotation = 0f
                rlTableSeatWestContainer?.rotation = 0f
                rlTableSeatNorthContainer?.rotation = 0f
            }
            LANDSCAPE -> {
                rlTableSeatEastContainer?.setPadding(0, 0, 0, 0)
                rlTableSeatWestContainer?.setPadding(0, 0, 0, 0)
                rlTableSeatWestContainer?.gravity = TOP
                rlTableSeatSouthContainer?.rotation = -90f
                rlTableSeatWestContainer?.rotation = 180f
                rlTableSeatNorthContainer?.rotation = 90f
            }
        }
    }
}
