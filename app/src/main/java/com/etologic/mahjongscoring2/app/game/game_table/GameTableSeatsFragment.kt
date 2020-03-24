package com.etologic.mahjongscoring2.app.game.game_table

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getDrawable
import androidx.fragment.app.Fragment
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.R.color
import com.etologic.mahjongscoring2.R.layout
import com.etologic.mahjongscoring2.app.model.Seat
import com.etologic.mahjongscoring2.app.model.SeatStates
import com.etologic.mahjongscoring2.app.model.SeatStates.*
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
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
        fun onEastSeatClick()
        fun onSouthSeatClick()
        fun onWestSeatClick()
        fun onNorthSeatClick()
    }
    
    private var eastIcon: Drawable? = null
    private var southIcon: Drawable? = null
    private var westIcon: Drawable? = null
    private var northIcon: Drawable? = null
    private var grayColor: Int? = null
    private var accentColor: Int? = null
    private var redColor: Int? = null
    private var greenColor: Int? = null
    private var purplePenalty: Int? = null
    private var listener: TableSeatsListener? = null
    private var eastSeatState: SeatStates = NORMAL
    private var southSeatState: SeatStates = NORMAL
    private var westSeatState: SeatStates = NORMAL
    private var northSeatState: SeatStates = NORMAL
    
    //PUBLIC
    internal fun setTableSeatsListener(tableSeatsListener: TableSeatsListener) {
        listener = tableSeatsListener
    }
    
    internal fun setEastSeat(seat: Seat) {
        setWindIcon(ivTableSeatEastSeatWindIcon, seat.wind)
        setName(tvTableSeatEastName, seat.name)
        setPoints(tvTableSeatEastPoints, seat.points)
        setPenaltyPoints(tvTableSeatEastPenaltyPoints, seat.penalty)
        setState(ivTableSeatEastSeatWindIcon, tvTableSeatEastName, tvTableSeatEastPoints, tvTableSeatEastPenaltyPoints, seat.state)
        eastSeatState = seat.state
    }
    
    internal fun setSouthSeat(seat: Seat) {
        setWindIcon(ivTableSeatSouthSeatWindIcon, seat.wind)
        setName(tvTableSeatSouthName, seat.name)
        setPoints(tvTableSeatSouthPoints, seat.points)
        setPenaltyPoints(tvTableSeatSouthPenaltyPoints, seat.penalty)
        setState(ivTableSeatSouthSeatWindIcon, tvTableSeatSouthName, tvTableSeatSouthPoints, tvTableSeatSouthPenaltyPoints, seat.state)
        southSeatState = seat.state
    }
    
    internal fun setWestSeat(seat: Seat) {
        setWindIcon(ivTableSeatWestSeatWindIcon, seat.wind)
        setName(tvTableSeatWestName, seat.name)
        setPoints(tvTableSeatWestPoints, seat.points)
        setPenaltyPoints(tvTableSeatWestPenaltyPoints, seat.penalty)
        setState(ivTableSeatWestSeatWindIcon, tvTableSeatWestName, tvTableSeatWestPoints, tvTableSeatWestPenaltyPoints, seat.state)
        westSeatState = seat.state
    }
    
    internal fun setNorthSeat(seat: Seat) {
        setWindIcon(ivTableSeatNorthSeatWindIcon, seat.wind)
        setName(tvTableSeatNorthName, seat.name)
        setPoints(tvTableSeatNorthPoints, seat.points)
        setPenaltyPoints(tvTableSeatNorthPenaltyPoints, seat.penalty)
        setState(ivTableSeatNorthSeatWindIcon, tvTableSeatNorthName, tvTableSeatNorthPoints, tvTableSeatNorthPenaltyPoints, seat.state)
        northSeatState = seat.state
    }
    
    private fun setWindIcon(imageView: ImageView?, wind: TableWinds?) {
        when (wind) {
            TableWinds.EAST -> imageView?.setImageDrawable(eastIcon)
            TableWinds.SOUTH -> imageView?.setImageDrawable(southIcon)
            TableWinds.WEST -> imageView?.setImageDrawable(westIcon)
            TableWinds.NORTH -> imageView?.setImageDrawable(northIcon)
            else -> return
        }
    }
    
    private fun setName(textView: TextView, name: String?) {
        textView.text = name ?: ""
    }
    
    private fun setPoints(textView: TextView, points: Int) {
        textView.text = String.format(getDefault(), "%d", points)
    }
    
    private fun setPenaltyPoints(textView: TextView, penaltyPoints: Int) {
        textView.text = String.format(getDefault(), "%+d", penaltyPoints)
        (if (penaltyPoints < 0) redColor else greenColor)?.let { textView.setTextColor(it) }
        textView.visibility = if (penaltyPoints != 0) VISIBLE else GONE
    }
    
    private fun setState(ivWind: ImageView?, tvName: TextView, tvPoints: TextView, tvPenaltyPoints: TextView, state: SeatStates?) {
        when (state) {
            NORMAL -> {
                ivWind?.clearColorFilter()
                grayColor?.let {
                    tvName.setTextColor(it)
                    tvPoints.setTextColor(it)
                }
                purplePenalty?.let { tvPenaltyPoints.setTextColor(it) }
            }
            SELECTED -> {
                accentColor?.let {
                    ivWind?.setColorFilter(it)
                    tvName.setTextColor(it)
                    tvPoints.setTextColor(it)
                    tvPenaltyPoints.setTextColor(it)
                }
            }
            DISABLED -> {
                greenColor?.let {
                    ivWind?.setColorFilter(it)
                    tvName.setTextColor(it)
                    tvPoints.setTextColor(it)
                    tvPenaltyPoints.setTextColor(it)
                }
            }
        }
    }
    
    private fun disableListeners() {
        ivTableSeatEastSeatWindIcon?.setOnClickListener {}
        tvTableSeatEastName?.setOnClickListener {}
        tvTableSeatEastPoints?.setOnClickListener {}
        tvTableSeatEastPenaltyPoints?.setOnClickListener {}
        
        ivTableSeatSouthSeatWindIcon?.setOnClickListener {}
        tvTableSeatSouthName?.setOnClickListener {}
        tvTableSeatSouthPoints?.setOnClickListener {}
        tvTableSeatSouthPenaltyPoints?.setOnClickListener {}
        
        ivTableSeatWestSeatWindIcon?.setOnClickListener {}
        tvTableSeatWestName?.setOnClickListener {}
        tvTableSeatWestPoints?.setOnClickListener {}
        tvTableSeatWestPenaltyPoints?.setOnClickListener {}
        
        ivTableSeatNorthSeatWindIcon?.setOnClickListener {}
        tvTableSeatNorthName?.setOnClickListener {}
        tvTableSeatNorthPoints?.setOnClickListener {}
        tvTableSeatNorthPenaltyPoints?.setOnClickListener {}
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
            grayColor = getColor(it, color.grayMM)
            accentColor = getColor(it, color.colorAccent)
            redColor = getColor(it, color.red)
            greenColor = getColor(it, color.colorPrimary)
            purplePenalty = getColor(it, color.purplePenalty)
        }
        initListeners()
    }
    
    private fun initListeners() {
        ivTableSeatEastSeatWindIcon?.setOnClickListener { if (eastSeatState != DISABLED) listener?.onEastSeatClick() }
        tvTableSeatEastName?.setOnClickListener { if (eastSeatState != DISABLED) listener?.onEastSeatClick() }
        tvTableSeatEastPoints?.setOnClickListener { if (eastSeatState != DISABLED) listener?.onEastSeatClick() }
        tvTableSeatEastPenaltyPoints?.setOnClickListener { if (eastSeatState != DISABLED) listener?.onEastSeatClick() }
        ivTableSeatSouthSeatWindIcon?.setOnClickListener { if (southSeatState != DISABLED) listener?.onSouthSeatClick() }
        tvTableSeatSouthName?.setOnClickListener { if (southSeatState != DISABLED) listener?.onSouthSeatClick() }
        tvTableSeatSouthPoints?.setOnClickListener { if (southSeatState != DISABLED) listener?.onSouthSeatClick() }
        tvTableSeatSouthPenaltyPoints?.setOnClickListener { if (southSeatState != DISABLED) listener?.onSouthSeatClick() }
        ivTableSeatWestSeatWindIcon?.setOnClickListener { if (westSeatState != DISABLED) listener?.onWestSeatClick() }
        tvTableSeatWestName?.setOnClickListener { if (westSeatState != DISABLED) listener?.onWestSeatClick() }
        tvTableSeatWestPoints?.setOnClickListener { if (westSeatState != DISABLED) listener?.onWestSeatClick() }
        tvTableSeatWestPenaltyPoints?.setOnClickListener { if (westSeatState != DISABLED) listener?.onWestSeatClick() }
        ivTableSeatNorthSeatWindIcon?.setOnClickListener { if (northSeatState != DISABLED) listener?.onNorthSeatClick() }
        tvTableSeatNorthName?.setOnClickListener { if (northSeatState != DISABLED) listener?.onNorthSeatClick() }
        tvTableSeatNorthPoints?.setOnClickListener { if (northSeatState != DISABLED) listener?.onNorthSeatClick() }
        tvTableSeatNorthPenaltyPoints?.setOnClickListener { if (northSeatState != DISABLED) listener?.onNorthSeatClick() }
    }
}
