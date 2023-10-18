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
package com.etologic.mahjongscoring2.app.game.game_table

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.util.TypedValue.applyDimension
import android.view.Gravity.BOTTOM
import android.view.Gravity.TOP
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout.GONE
import android.widget.RelativeLayout.INVISIBLE
import android.widget.RelativeLayout.VISIBLE
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getDrawable
import androidx.fragment.app.Fragment
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.R.color
import com.etologic.mahjongscoring2.app.extensions.setOnSecureClickListener
import com.etologic.mahjongscoring2.app.model.SeatStates
import com.etologic.mahjongscoring2.app.model.SeatStates.DISABLED
import com.etologic.mahjongscoring2.app.model.SeatStates.NORMAL
import com.etologic.mahjongscoring2.app.model.SeatStates.SELECTED
import com.etologic.mahjongscoring2.business.model.entities.Table
import com.etologic.mahjongscoring2.business.model.enums.ScreenOrientation
import com.etologic.mahjongscoring2.business.model.enums.ScreenOrientation.LANDSCAPE
import com.etologic.mahjongscoring2.business.model.enums.ScreenOrientation.PORTRAIT
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.EAST
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NONE
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NORTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.SOUTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.WEST
import com.etologic.mahjongscoring2.databinding.GameTableSeatEastBinding
import com.etologic.mahjongscoring2.databinding.GameTableSeatNorthBinding
import com.etologic.mahjongscoring2.databinding.GameTableSeatSouthBinding
import com.etologic.mahjongscoring2.databinding.GameTableSeatWestBinding
import com.etologic.mahjongscoring2.databinding.GameTableSeatsFragmentBinding
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
    private var margin = 0

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

        with(binding) {
            areSeatsDisabled = states[0] == DISABLED && states[1] == DISABLED && states[2] == DISABLED && states[3] == DISABLED
            setState(bindingEast.ivTableSeatEastSeatWindIcon, bindingEast.tvTableSeatEastName, bindingEast.tvTableSeatEastPoints, states[0])
            setState(bindingSouth.ivTableSeatSouthSeatWindIcon, bindingSouth.tvTableSeatSouthName, bindingSouth.tvTableSeatSouthPoints, states[1])
            setState(bindingWest.ivTableSeatWestSeatWindIcon, bindingWest.tvTableSeatWestName, bindingWest.tvTableSeatWestPoints, states[2])
            setState(bindingNorth.ivTableSeatNorthSeatWindIcon, bindingNorth.tvTableSeatNorthName, bindingNorth.tvTableSeatNorthPoints, states[3])
        }
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
        bindingEast.tvTableSeatEastPoints.text = points[EAST.code]
        bindingSouth.tvTableSeatSouthPoints.text = points[SOUTH.code]
        bindingWest.tvTableSeatWestPoints.text = points[WEST.code]
        bindingNorth.tvTableSeatNorthPoints.text = points[NORTH.code]
    }

    private fun setPenalties(penalties: IntArray) {
        setPenaltyPoints(bindingEast.tvTableSeatEastPenaltyPoints, penalties[EAST.code])
        setPenaltyPoints(bindingSouth.tvTableSeatSouthPenaltyPoints, penalties[SOUTH.code])
        setPenaltyPoints(bindingWest.tvTableSeatWestPenaltyPoints, penalties[WEST.code])
        setPenaltyPoints(bindingNorth.tvTableSeatNorthPenaltyPoints, penalties[NORTH.code])
    }

    private fun setPenaltyPoints(textView: TextView, penaltyPoints: Int) {
        textView.text = String.format(getDefault(), "%+d", penaltyPoints)
        (if (penaltyPoints < 0) redColor else greenColor)?.let { textView.setTextColor(it) }
        textView.visibility = if (penaltyPoints != 0) VISIBLE else GONE
    }

    private fun setWinds(winds: Array<TableWinds>) {
        setWindIcon(bindingEast.ivTableSeatEastSeatWindIcon, winds[EAST.code])
        setWindIcon(bindingSouth.ivTableSeatSouthSeatWindIcon, winds[SOUTH.code])
        setWindIcon(bindingWest.ivTableSeatWestSeatWindIcon, winds[WEST.code])
        setWindIcon(bindingNorth.ivTableSeatNorthSeatWindIcon, winds[NORTH.code])
    }

    private fun setWindIcon(imageView: ImageView?, wind: TableWinds?) {
        when (wind) {
            EAST -> setWind(imageView, eastIcon)
            SOUTH -> setWind(imageView, southIcon)
            WEST -> setWind(imageView, westIcon)
            NORTH -> setWind(imageView, northIcon)
            else -> imageView?.visibility = INVISIBLE
        }
    }

    private fun setWind(imageView: ImageView?, icon: Drawable?) {
        if (imageView?.visibility != VISIBLE)
            imageView?.visibility = VISIBLE
        imageView?.setImageDrawable(icon)
    }

    private fun setNames(names: Array<String>) {
        setName(bindingEast.tvTableSeatEastName, names[EAST.code])
        setName(bindingSouth.tvTableSeatSouthName, names[SOUTH.code])
        setName(bindingWest.tvTableSeatWestName, names[WEST.code])
        setName(bindingNorth.tvTableSeatNorthName, names[NORTH.code])
    }

    private fun setName(textView: TextView, name: String?) {
        textView.text = name ?: ""
    }

    //LIFECYCLE
    override fun onAttach(context: Context) {
        super.onAttach(context)
        margin = applyDimension(COMPLEX_UNIT_DIP, 16f, resources.displayMetrics).toInt()
    }

    private var _binding: GameTableSeatsFragmentBinding? = null
    private val binding get() = _binding!!
    private var _bindingEast: GameTableSeatEastBinding? = null
    private val bindingEast get() = _bindingEast!!
    private var _bindingSouth: GameTableSeatSouthBinding? = null
    private val bindingSouth get() = _bindingSouth!!
    private var _bindingWest: GameTableSeatWestBinding? = null
    private val bindingWest get() = _bindingWest!!
    private var _bindingNorth: GameTableSeatNorthBinding? = null
    private val bindingNorth get() = _bindingNorth!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GameTableSeatsFragmentBinding.inflate(inflater, container, false)
        _bindingEast = GameTableSeatEastBinding.inflate(inflater, container, false)
        _bindingSouth = GameTableSeatSouthBinding.inflate(inflater, container, false)
        _bindingWest = GameTableSeatWestBinding.inflate(inflater, container, false)
        _bindingNorth = GameTableSeatNorthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _bindingEast = null
        _bindingSouth = null
        _bindingWest = null
        _bindingNorth = null
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
        bindingEast.ivTableSeatEastSeatWindIcon.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(EAST) }
        bindingEast.tvTableSeatEastName.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(EAST) }
        bindingEast.tvTableSeatEastPoints.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(EAST) }
        bindingEast.vGameTableSeatEastAuxStart.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(EAST) }
        bindingEast.vGameTableSeatEastAuxEnd.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(EAST) }
        bindingEast.tvTableSeatEastPenaltyPoints.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(EAST) }
        bindingSouth.ivTableSeatSouthSeatWindIcon.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(SOUTH) }
        bindingSouth.tvTableSeatSouthName.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(SOUTH) }
        bindingSouth.tvTableSeatSouthPoints.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(SOUTH) }
        bindingSouth.vGameTableSeatSouthAuxStart.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(SOUTH) }
        bindingSouth.vGameTableSeatSouthAuxEnd.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(SOUTH) }
        bindingSouth.tvTableSeatSouthPenaltyPoints.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(SOUTH) }
        bindingWest.ivTableSeatWestSeatWindIcon.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(WEST) }
        bindingWest.tvTableSeatWestName.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(WEST) }
        bindingWest.tvTableSeatWestPoints.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(WEST) }
        bindingWest.vGameTableSeatWestAuxStart.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(WEST) }
        bindingWest.vGameTableSeatWestAuxEnd.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(WEST) }
        bindingWest.tvTableSeatWestPenaltyPoints.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(WEST) }
        bindingNorth.ivTableSeatNorthSeatWindIcon.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(NORTH) }
        bindingNorth.tvTableSeatNorthName.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(NORTH) }
        bindingNorth.tvTableSeatNorthPoints.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(NORTH) }
        bindingNorth.vGameTableSeatNorthAuxStart.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(NORTH) }
        bindingNorth.vGameTableSeatNorthAuxEnd.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(NORTH) }
        bindingNorth.tvTableSeatNorthPenaltyPoints.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(NORTH) }
    }

    internal fun updateSeatsOrientation(screenOrientation: ScreenOrientation) {
        when (screenOrientation) {
            PORTRAIT -> {
                bindingEast.rlTableSeatEastContainer.setPadding(0, margin * 3, 0, 0)
                bindingWest.rlTableSeatWestContainer.setPadding(0, 0, 0, margin * 3)
                bindingSouth.rlTableSeatSouthContainer.setPadding(0, 0, 0, 0)
                bindingNorth.rlTableSeatNorthContainer.setPadding(0, 0, 0, 0)
                bindingWest.rlTableSeatWestContainer.gravity = BOTTOM
                bindingSouth.rlTableSeatSouthContainer.rotation = 0f
                bindingWest.rlTableSeatWestContainer.rotation = 0f
                bindingNorth.rlTableSeatNorthContainer.rotation = 0f
            }

            LANDSCAPE -> {
                bindingEast.rlTableSeatEastContainer.setPadding(0, 0, 0, 0)
                bindingWest.rlTableSeatWestContainer.setPadding(0, 0, 0, 0)
                bindingSouth.rlTableSeatSouthContainer.setPadding(0, 0, 0, margin)
                bindingNorth.rlTableSeatNorthContainer.setPadding(0, 0, 0, margin)
                bindingWest.rlTableSeatWestContainer.gravity = TOP
                bindingSouth.rlTableSeatSouthContainer.rotation = -90f
                bindingWest.rlTableSeatWestContainer.rotation = 180f
                bindingNorth.rlTableSeatNorthContainer.rotation = 90f
            }
        }
    }
}
