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
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel
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
        setPoints(table.getPlayersTotalPointsByCurrentSeat().map {
            String.format(getDefault(), "%d", it)
        })
        setWinds(table.getSeatsCurrentWind(table.rounds.size))
        setNames(table.getPlayersNamesByCurrentSeat())
        setPenalties(table.getPlayersPenaltiesByCurrentSeat(), table.rounds.last().isEnded)
    }

    private fun getSeatsStates(table: Table): Array<SeatStates> =
        if (table.rounds.last().isEnded) {
            arrayOf(DISABLED, DISABLED, DISABLED, DISABLED)
        } else {
            getSeatsStates()
        }

    private fun setStates(states: Array<SeatStates>) {
        areSeatsDisabled = states[0] == DISABLED && states[1] == DISABLED && states[2] == DISABLED && states[3] == DISABLED
        setState(
            ivWind = binding.iGameTableSeatEast.ivTableSeatEastSeatWindIcon,
            tvName = binding.iGameTableSeatEast.tvTableSeatEastName,
            tvPoints = binding.iGameTableSeatEast.tvTableSeatEastPoints,
            state = states[0]
        )
        setState(
            ivWind = binding.iGameTableSeatSouth.ivTableSeatSouthSeatWindIcon,
            tvName = binding.iGameTableSeatSouth.tvTableSeatSouthName,
            tvPoints = binding.iGameTableSeatSouth.tvTableSeatSouthPoints,
            state = states[1]
        )
        setState(
            ivWind = binding.iGameTableSeatWest.ivTableSeatWestSeatWindIcon,
            tvName = binding.iGameTableSeatWest.tvTableSeatWestName,
            tvPoints = binding.iGameTableSeatWest.tvTableSeatWestPoints,
            state = states[2]
        )
        setState(
            ivWind = binding.iGameTableSeatNorth.ivTableSeatNorthSeatWindIcon,
            tvName = binding.iGameTableSeatNorth.tvTableSeatNorthName,
            tvPoints = binding.iGameTableSeatNorth.tvTableSeatNorthPoints,
            state = states[3]
        )
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
        binding.iGameTableSeatEast.tvTableSeatEastPoints.text = points[EAST.code]
        binding.iGameTableSeatSouth.tvTableSeatSouthPoints.text = points[SOUTH.code]
        binding.iGameTableSeatWest.tvTableSeatWestPoints.text = points[WEST.code]
        binding.iGameTableSeatNorth.tvTableSeatNorthPoints.text = points[NORTH.code]
    }

    private fun setPenalties(penalties: IntArray, isEnded: Boolean) {
        if (!isEnded) {
            setPenaltyPoints(binding.iGameTableSeatEast.tvTableSeatEastPenaltyPoints, penalties[EAST.code])
            setPenaltyPoints(binding.iGameTableSeatSouth.tvTableSeatSouthPenaltyPoints, penalties[SOUTH.code])
            setPenaltyPoints(binding.iGameTableSeatWest.tvTableSeatWestPenaltyPoints, penalties[WEST.code])
            setPenaltyPoints(binding.iGameTableSeatNorth.tvTableSeatNorthPenaltyPoints, penalties[NORTH.code])
        } else {
            binding.iGameTableSeatEast.tvTableSeatEastPenaltyPoints.visibility = GONE
            binding.iGameTableSeatSouth.tvTableSeatSouthPenaltyPoints.visibility = GONE
            binding.iGameTableSeatWest.tvTableSeatWestPenaltyPoints.visibility = GONE
            binding.iGameTableSeatNorth.tvTableSeatNorthPenaltyPoints.visibility = GONE
        }
    }

    private fun setPenaltyPoints(textView: TextView, penaltyPoints: Int) {
        textView.text = String.format(getDefault(), "%+d", penaltyPoints)
        (if (penaltyPoints < 0) redColor else greenColor)?.let { textView.setTextColor(it) }
        textView.visibility = if (penaltyPoints != 0) VISIBLE else GONE
    }

    private fun setWinds(winds: Array<TableWinds>) {
        setWindIcon(binding.iGameTableSeatEast.ivTableSeatEastSeatWindIcon, winds[EAST.code])
        setWindIcon(binding.iGameTableSeatSouth.ivTableSeatSouthSeatWindIcon, winds[SOUTH.code])
        setWindIcon(binding.iGameTableSeatWest.ivTableSeatWestSeatWindIcon, winds[WEST.code])
        setWindIcon(binding.iGameTableSeatNorth.ivTableSeatNorthSeatWindIcon, winds[NORTH.code])
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
        setName(binding.iGameTableSeatEast.tvTableSeatEastName, names[EAST.code])
        setName(binding.iGameTableSeatSouth.tvTableSeatSouthName, names[SOUTH.code])
        setName(binding.iGameTableSeatWest.tvTableSeatWestName, names[WEST.code])
        setName(binding.iGameTableSeatNorth.tvTableSeatNorthName, names[NORTH.code])
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
    protected var activityViewModel: GameViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GameTableSeatsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
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
        binding.iGameTableSeatEast.ivTableSeatEastSeatWindIcon.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(EAST) }
        binding.iGameTableSeatEast.tvTableSeatEastName.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(EAST) }
        binding.iGameTableSeatEast.tvTableSeatEastPoints.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(EAST) }
        binding.iGameTableSeatEast.vGameTableSeatEastAuxStart.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(EAST) }
        binding.iGameTableSeatEast.vGameTableSeatEastAuxEnd.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(EAST) }
        binding.iGameTableSeatEast.tvTableSeatEastPenaltyPoints.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(EAST) }
        binding.iGameTableSeatSouth.ivTableSeatSouthSeatWindIcon.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(SOUTH) }
        binding.iGameTableSeatSouth.tvTableSeatSouthName.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(SOUTH) }
        binding.iGameTableSeatSouth.tvTableSeatSouthPoints.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(SOUTH) }
        binding.iGameTableSeatSouth.vGameTableSeatSouthAuxStart.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(SOUTH) }
        binding.iGameTableSeatSouth.vGameTableSeatSouthAuxEnd.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(SOUTH) }
        binding.iGameTableSeatSouth.tvTableSeatSouthPenaltyPoints.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(SOUTH) }
        binding.iGameTableSeatWest.ivTableSeatWestSeatWindIcon.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(WEST) }
        binding.iGameTableSeatWest.tvTableSeatWestName.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(WEST) }
        binding.iGameTableSeatWest.tvTableSeatWestPoints.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(WEST) }
        binding.iGameTableSeatWest.vGameTableSeatWestAuxStart.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(WEST) }
        binding.iGameTableSeatWest.vGameTableSeatWestAuxEnd.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(WEST) }
        binding.iGameTableSeatWest.tvTableSeatWestPenaltyPoints.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(WEST) }
        binding.iGameTableSeatNorth.ivTableSeatNorthSeatWindIcon.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(NORTH) }
        binding.iGameTableSeatNorth.tvTableSeatNorthName.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(NORTH) }
        binding.iGameTableSeatNorth.tvTableSeatNorthPoints.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(NORTH) }
        binding.iGameTableSeatNorth.vGameTableSeatNorthAuxStart.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(NORTH) }
        binding.iGameTableSeatNorth.vGameTableSeatNorthAuxEnd.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(NORTH) }
        binding.iGameTableSeatNorth.tvTableSeatNorthPenaltyPoints.setOnSecureClickListener { if (!areSeatsDisabled) listener?.onSeatClick(NORTH) }
    }

    internal fun updateSeatsOrientation(screenOrientation: ScreenOrientation) {
        when (screenOrientation) {
            PORTRAIT -> {
                binding.iGameTableSeatEast.rlTableSeatEastContainer.setPadding(0, margin, 0, 0)
                binding.iGameTableSeatWest.rlTableSeatWestContainer.setPadding(0, margin, 0, 0)
                binding.iGameTableSeatSouth.rlTableSeatSouthContainer.setPadding(0, 0, 0, margin)
                binding.iGameTableSeatNorth.rlTableSeatNorthContainer.setPadding(0, 0, 0, margin)
                binding.iGameTableSeatSouth.rlTableSeatSouthContainer.rotation = -90f
                binding.iGameTableSeatWest.rlTableSeatWestContainer.rotation = 180f
                binding.iGameTableSeatNorth.rlTableSeatNorthContainer.rotation = 90f
            }

            LANDSCAPE -> {
                binding.iGameTableSeatEast.rlTableSeatEastContainer.setPadding(0, margin * 3, 0, 0)
                binding.iGameTableSeatWest.rlTableSeatWestContainer.setPadding(0, 0, 0, margin * 3)
                binding.iGameTableSeatSouth.rlTableSeatSouthContainer.setPadding(0, 0, 0, 0)
                binding.iGameTableSeatNorth.rlTableSeatNorthContainer.setPadding(0, 0, 0, 0)
                binding.iGameTableSeatSouth.rlTableSeatSouthContainer.rotation = 0f
                binding.iGameTableSeatWest.rlTableSeatWestContainer.rotation = 0f
                binding.iGameTableSeatNorth.rlTableSeatNorthContainer.rotation = 0f
            }
        }
    }
}
