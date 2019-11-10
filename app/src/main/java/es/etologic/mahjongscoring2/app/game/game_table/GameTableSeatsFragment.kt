package es.etologic.mahjongscoring2.app.game.game_table

import android.R.color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import es.etologic.mahjongscoring2.R
import es.etologic.mahjongscoring2.R.drawable
import es.etologic.mahjongscoring2.app.model.Seat
import es.etologic.mahjongscoring2.app.model.SeatStates
import es.etologic.mahjongscoring2.app.model.SeatStates.*
import es.etologic.mahjongscoring2.domain.model.enums.TableWinds
import kotlinx.android.synthetic.main.game_table_seat_east.*
import kotlinx.android.synthetic.main.game_table_seat_east.view.*
import kotlinx.android.synthetic.main.game_table_seat_north.*
import kotlinx.android.synthetic.main.game_table_seat_north.view.*
import kotlinx.android.synthetic.main.game_table_seat_south.*
import kotlinx.android.synthetic.main.game_table_seat_south.view.*
import kotlinx.android.synthetic.main.game_table_seat_west.*
import kotlinx.android.synthetic.main.game_table_seat_west.view.*
import java.util.*

class GameTableSeatsFragment : androidx.fragment.app.Fragment() {
    
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
    private var penaltyIcon: Drawable? = null
    private var tsumoIcon: Drawable? = null
    private var ronIcon: Drawable? = null
    private var whiteColor: Int = 0
    private var grayColor: Int = 0
    private var accentColor: Int = 0
    private var redColor: Int = 0
    private var greenColor: Int = 0
    private var purplePenalty: Int = 0
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
        textView.text = String.format(Locale.getDefault(), "%d", points)
    }
    
    private fun setPenaltyPoints(textView: TextView, penaltyPoints: Int) {
        textView.text = String.format(Locale.getDefault(), "%+d", penaltyPoints)
        textView.setTextColor(if (penaltyPoints < 0) redColor else greenColor)
        textView.visibility = if (penaltyPoints != 0) VISIBLE else GONE
    }
    
    private fun setState(
        imageViewWind: ImageView?,
        textViewName: TextView,
        textViewPoints: TextView,
        textViewPenaltyPoints: TextView,
        state: SeatStates?
    ) {
        if (state == NORMAL) {
            imageViewWind?.clearColorFilter()
            textViewName.setTextColor(grayColor)
            textViewPoints.setTextColor(grayColor)
            textViewPenaltyPoints.setTextColor(purplePenalty)
        } else if (state == SELECTED) {
            imageViewWind?.setColorFilter(accentColor)
            textViewName.setTextColor(accentColor)
            textViewPoints.setTextColor(accentColor)
            textViewPenaltyPoints.setTextColor(accentColor)
        }
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
    
    //LIFECYCLE
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.game_table_seats_fragment, container, false)
        initResources()
        initListeners(view)
        return view
    }
    
    private fun initResources() {
        activity?.let {
            eastIcon = ContextCompat.getDrawable(it, drawable.ic_east)
            southIcon = ContextCompat.getDrawable(it, drawable.ic_south)
            westIcon = ContextCompat.getDrawable(it, drawable.ic_west)
            northIcon = ContextCompat.getDrawable(it, drawable.ic_north)
            penaltyIcon = ContextCompat.getDrawable(it, drawable.ic_penalty_white)
            tsumoIcon = ContextCompat.getDrawable(it, drawable.ic_trophy_white_18dp)
            ronIcon = ContextCompat.getDrawable(it, drawable.ic_medal_white)
            whiteColor = ContextCompat.getColor(it, color.white)
            grayColor = ContextCompat.getColor(it, R.color.grayMM)
            accentColor = ContextCompat.getColor(it, R.color.colorAccent)
            redColor = ContextCompat.getColor(it, R.color.red)
            greenColor = ContextCompat.getColor(it, R.color.colorPrimary)
            purplePenalty = ContextCompat.getColor(it, R.color.purplePenalty)
        }
    }
    
    private fun initListeners(view: View) {
        view.ivTableSeatEastSeatWindIcon?.setOnClickListener { if (eastSeatState != DISABLED) listener?.onEastSeatClick() }
        view.tvTableSeatEastName?.setOnClickListener { if (eastSeatState != DISABLED) listener?.onEastSeatClick() }
        view.tvTableSeatEastPoints?.setOnClickListener { if (eastSeatState != DISABLED) listener?.onEastSeatClick() }
        view.tvTableSeatEastPenaltyPoints?.setOnClickListener { if (eastSeatState != DISABLED) listener?.onEastSeatClick() }
        view.ivTableSeatSouthSeatWindIcon?.setOnClickListener { if (southSeatState != DISABLED) listener?.onSouthSeatClick() }
        view.tvTableSeatSouthName?.setOnClickListener { if (southSeatState != DISABLED) listener?.onSouthSeatClick() }
        view.tvTableSeatSouthPoints?.setOnClickListener { if (southSeatState != DISABLED) listener?.onSouthSeatClick() }
        view.tvTableSeatSouthPenaltyPoints?.setOnClickListener { if (southSeatState != DISABLED) listener?.onSouthSeatClick() }
        view.ivTableSeatWestSeatWindIcon?.setOnClickListener { if (westSeatState != DISABLED) listener?.onWestSeatClick() }
        view.tvTableSeatWestName?.setOnClickListener { if (westSeatState != DISABLED) listener?.onWestSeatClick() }
        view.tvTableSeatWestPoints?.setOnClickListener { if (westSeatState != DISABLED) listener?.onWestSeatClick() }
        view.tvTableSeatWestPenaltyPoints?.setOnClickListener { if (westSeatState != DISABLED) listener?.onWestSeatClick() }
        view.ivTableSeatNorthSeatWindIcon?.setOnClickListener { if (northSeatState != DISABLED) listener?.onNorthSeatClick() }
        view.tvTableSeatNorthName?.setOnClickListener { if (northSeatState != DISABLED) listener?.onNorthSeatClick() }
        view.tvTableSeatNorthPoints?.setOnClickListener { if (northSeatState != DISABLED) listener?.onNorthSeatClick() }
        view.tvTableSeatNorthPenaltyPoints?.setOnClickListener { if (northSeatState != DISABLED) listener?.onNorthSeatClick() }
    }
}
