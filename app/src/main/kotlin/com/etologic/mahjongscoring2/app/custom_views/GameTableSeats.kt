/*
 *     Copyright © 2024  Ernesto Vega de la Iglesia Soria
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.etologic.mahjongscoring2.app.custom_views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.util.TypedValue.applyDimension
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getDrawable
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.model.SeatStates
import com.etologic.mahjongscoring2.app.model.SeatStates.DISABLED
import com.etologic.mahjongscoring2.app.model.SeatStates.NORMAL
import com.etologic.mahjongscoring2.app.model.SeatStates.SELECTED
import com.etologic.mahjongscoring2.app.utils.setOnSecureClickListener
import com.etologic.mahjongscoring2.business.model.dtos.SeatDiffs
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.model.enums.SeatOrientation
import com.etologic.mahjongscoring2.business.model.enums.SeatOrientation.DOWN
import com.etologic.mahjongscoring2.business.model.enums.SeatOrientation.OUT
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.EAST
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NONE
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NORTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.SOUTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.WEST
import com.etologic.mahjongscoring2.databinding.CustomTableSeatsBinding
import java.util.Locale.getDefault

class GameTableSeats(context: Context, attributeSet: AttributeSet) : RelativeLayout(context, attributeSet) {

    interface GameTableSeatsListener {
        fun onSeatClick(wind: TableWinds)
        fun toggleDiffsView(shouldShowDiffs: Boolean)
    }

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

    private var selectedPlayer: TableWinds = NONE
    private var areSeatsDisabled: Boolean = false
    private var margin = 0

    private var _binding: CustomTableSeatsBinding? = null
    private val binding get() = _binding!!

    private var listener: GameTableSeatsListener? = null

    init {
        _binding = CustomTableSeatsBinding.inflate(LayoutInflater.from(context), this, true)

        eastIcon = getDrawable(context, R.drawable.ic_east)
        southIcon = getDrawable(context, R.drawable.ic_south)
        westIcon = getDrawable(context, R.drawable.ic_west)
        northIcon = getDrawable(context, R.drawable.ic_north)
        grayNormalColor = getColor(context, R.color.grayMM)
        grayDisabledColor = getColor(context, R.color.gray)
        accentColor = getColor(context, R.color.colorAccent)
        redColor = getColor(context, R.color.red)
        greenColor = getColor(context, R.color.colorPrimary)
        purplePenalty = getColor(context, R.color.purplePenalty)
        margin = applyDimension(COMPLEX_UNIT_DIP, 16f, resources.displayMetrics).toInt()

        setListeners()
    }

    fun setTableSeatsListener(gameTableSeatsListener: GameTableSeatsListener) {
        listener = gameTableSeatsListener
    }

    fun updateSeatState(wind: TableWinds) {
        selectedPlayer = wind
        setStates(getSeatsStates())
    }

    fun setSeats(game: UiGame, isUserFontTooBig: Boolean) {
        if (game.gameId != UiGame.NOT_SET_GAME_ID) {
            selectedPlayer = NONE
            setStates(getSeatsStates(game))
            val playersTotalPointsByCurrentSeat = game.getPlayersTotalPointsByCurrentSeat()
            setPoints(playersTotalPointsByCurrentSeat.map { it.toString() })
            setWinds(game.getSeatsCurrentWind(game.uiRounds.size))
            setNames(game.getPlayersNamesByCurrentSeat())
            setPenalties(game.getPlayersPenaltiesByCurrentSeat(), game.isEnded)
            setPointsDiffs(game, isUserFontTooBig)
        }
    }

    private fun getSeatsStates(): Array<SeatStates> =
        when (selectedPlayer) {
            NONE -> arrayOf(NORMAL, NORMAL, NORMAL, NORMAL)
            EAST -> arrayOf(SELECTED, NORMAL, NORMAL, NORMAL)
            SOUTH -> arrayOf(NORMAL, SELECTED, NORMAL, NORMAL)
            WEST -> arrayOf(NORMAL, NORMAL, SELECTED, NORMAL)
            NORTH -> arrayOf(NORMAL, NORMAL, NORMAL, SELECTED)
        }

    private fun getSeatsStates(uiGame: UiGame): Array<SeatStates> =
        if (uiGame.isEnded) arrayOf(DISABLED, DISABLED, DISABLED, DISABLED) else getSeatsStates()

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

    private fun setPointsDiffs(uiGame: UiGame?, isUserFontTooBig: Boolean) {
        if (!isUserFontTooBig) {
            val tableDiffs = uiGame?.getTableDiffs()
            if (tableDiffs != null) {
                with(binding.iGameTableSeatEast.iGameTableSeatEastDiffs) {
                    setSeatDiffs(
                        tableDiffs.seatsDiffs[EAST.code],
                        tvTableSeatDiffsFirstSelfPick, tvTableSeatDiffsFirstDirectHu, tvTableSeatDiffsFirstIndirectHu, tlTableSeatDiffs,
                        tvTableSeatDiffsSecondSelfPick, tvTableSeatDiffsSecondDirectHu, tvTableSeatDiffsSecondIndirectHu, trTableSeatDiffsSecond,
                        tvTableSeatDiffsThirdSelfPick, tvTableSeatDiffsThirdDirectHu, tvTableSeatDiffsThirdIndirectHu, trTableSeatDiffsThird,
                    )
                }
                with(binding.iGameTableSeatSouth.iGameTableSeatSouthDiffs) {
                    setSeatDiffs(
                        tableDiffs.seatsDiffs[SOUTH.code],
                        tvTableSeatDiffsFirstSelfPick, tvTableSeatDiffsFirstDirectHu, tvTableSeatDiffsFirstIndirectHu, tlTableSeatDiffs,
                        tvTableSeatDiffsSecondSelfPick, tvTableSeatDiffsSecondDirectHu, tvTableSeatDiffsSecondIndirectHu, trTableSeatDiffsSecond,
                        tvTableSeatDiffsThirdSelfPick, tvTableSeatDiffsThirdDirectHu, tvTableSeatDiffsThirdIndirectHu, trTableSeatDiffsThird,
                    )
                }
                with(binding.iGameTableSeatWest.iGameTableSeatWestDiffs) {
                    setSeatDiffs(
                        tableDiffs.seatsDiffs[WEST.code],
                        tvTableSeatDiffsFirstSelfPick, tvTableSeatDiffsFirstDirectHu, tvTableSeatDiffsFirstIndirectHu, tlTableSeatDiffs,
                        tvTableSeatDiffsSecondSelfPick, tvTableSeatDiffsSecondDirectHu, tvTableSeatDiffsSecondIndirectHu, trTableSeatDiffsSecond,
                        tvTableSeatDiffsThirdSelfPick, tvTableSeatDiffsThirdDirectHu, tvTableSeatDiffsThirdIndirectHu, trTableSeatDiffsThird,
                    )
                }
                with(binding.iGameTableSeatNorth.iGameTableSeatNorthDiffs) {
                    setSeatDiffs(
                        tableDiffs.seatsDiffs[NORTH.code],
                        tvTableSeatDiffsFirstSelfPick, tvTableSeatDiffsFirstDirectHu, tvTableSeatDiffsFirstIndirectHu, tlTableSeatDiffs,
                        tvTableSeatDiffsSecondSelfPick, tvTableSeatDiffsSecondDirectHu, tvTableSeatDiffsSecondIndirectHu, trTableSeatDiffsSecond,
                        tvTableSeatDiffsThirdSelfPick, tvTableSeatDiffsThirdDirectHu, tvTableSeatDiffsThirdIndirectHu, trTableSeatDiffsThird,
                    )
                }
            } else {
                toggleDiffs(false)
            }
        } else {
            toggleDiffs(false)
        }
    }

    private fun setSeatDiffs(
        seatDiffs: SeatDiffs,
        tvFirstSelfPick: TextView, tvFirstDirectHu: TextView, tvFirstIndirectHu: TextView, tlGameTable: TableLayout,
        tvSecondSelfPick: TextView, tvSecondDirectHu: TextView, tvSecondIndirectHu: TextView, trDiffsSecond: TableRow,
        tvThirdSelfPick: TextView, tvThirdDirectHu: TextView, tvThirdIndirectHu: TextView, trDiffsThird: TableRow,
    ) {
        tlGameTable.visibility = GONE
        trDiffsSecond.visibility = GONE
        trDiffsThird.visibility = GONE

        val pointsToBeFirst = seatDiffs.pointsToBeFirst
        if (pointsToBeFirst == null) {
            tlGameTable.visibility = GONE
            return
        } else {
            tlGameTable.visibility = VISIBLE
            tvFirstSelfPick.text = pointsToBeFirst.bySelfPick.toString()
            tvFirstDirectHu.text = pointsToBeFirst.byDirectHu.toString()
            tvFirstIndirectHu.text = pointsToBeFirst.byIndirectHu.toString()
        }

        val pointsToBeSecond = seatDiffs.pointsToBeSecond
        if (pointsToBeSecond == null) {
            trDiffsSecond.visibility = GONE
        } else {
            trDiffsSecond.visibility = VISIBLE
            tvSecondSelfPick.text = pointsToBeSecond.bySelfPick.toString()
            tvSecondDirectHu.text = pointsToBeSecond.byDirectHu.toString()
            tvSecondIndirectHu.text = pointsToBeSecond.byIndirectHu.toString()
        }

        val pointsToBeThird = seatDiffs.pointsToBeThird
        if (pointsToBeThird == null) {
            trDiffsThird.visibility = GONE
        } else {
            trDiffsThird.visibility = VISIBLE
            tvThirdSelfPick.text = pointsToBeThird.bySelfPick.toString()
            tvThirdDirectHu.text = pointsToBeThird.byDirectHu.toString()
            tvThirdIndirectHu.text = pointsToBeThird.byIndirectHu.toString()
        }
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
        if (imageView?.visibility != VISIBLE) {
            imageView?.visibility = VISIBLE
        }
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


    @SuppressLint("ClickableViewAccessibility")
    private fun setListeners() {
        with(binding) {
            fun eastClick() { if (!areSeatsDisabled) listener?.onSeatClick(EAST) }
            iGameTableSeatEast.ivTableSeatEastSeatWindIcon.setOnSecureClickListener { eastClick() }
            iGameTableSeatEast.tvTableSeatEastName.setOnSecureClickListener { eastClick() }
            iGameTableSeatEast.tvTableSeatEastPoints.setOnSecureClickListener { eastClick() }
            iGameTableSeatEast.vGameTableSeatEastAuxStart.setOnSecureClickListener { eastClick() }
            iGameTableSeatEast.vGameTableSeatEastAuxEnd.setOnSecureClickListener { eastClick() }
            iGameTableSeatEast.tvTableSeatEastPenaltyPoints.setOnSecureClickListener { eastClick() }
            iGameTableSeatEast.iGameTableSeatEastDiffs.tlTableSeatDiffs.setOnSecureClickListener { eastClick() }

            fun southClick() { if (!areSeatsDisabled) listener?.onSeatClick(SOUTH) }
            iGameTableSeatSouth.ivTableSeatSouthSeatWindIcon.setOnSecureClickListener { southClick() }
            iGameTableSeatSouth.tvTableSeatSouthName.setOnSecureClickListener { southClick() }
            iGameTableSeatSouth.tvTableSeatSouthPoints.setOnSecureClickListener { southClick() }
            iGameTableSeatSouth.vGameTableSeatSouthAuxStart.setOnSecureClickListener { southClick() }
            iGameTableSeatSouth.vGameTableSeatSouthAuxEnd.setOnSecureClickListener { southClick() }
            iGameTableSeatSouth.tvTableSeatSouthPenaltyPoints.setOnSecureClickListener { southClick() }
            iGameTableSeatSouth.iGameTableSeatSouthDiffs.tlTableSeatDiffs.setOnSecureClickListener { southClick() }

            fun westClick() { if (!areSeatsDisabled) listener?.onSeatClick(WEST) }
            iGameTableSeatWest.ivTableSeatWestSeatWindIcon.setOnSecureClickListener { westClick() }
            iGameTableSeatWest.tvTableSeatWestName.setOnSecureClickListener { westClick() }
            iGameTableSeatWest.tvTableSeatWestPoints.setOnSecureClickListener { westClick() }
            iGameTableSeatWest.vGameTableSeatWestAuxStart.setOnSecureClickListener { westClick() }
            iGameTableSeatWest.vGameTableSeatWestAuxEnd.setOnSecureClickListener { westClick() }
            iGameTableSeatWest.tvTableSeatWestPenaltyPoints.setOnSecureClickListener { westClick() }
            iGameTableSeatWest.iGameTableSeatWestDiffs.tlTableSeatDiffs.setOnSecureClickListener { westClick() }

            fun northClick() { if (!areSeatsDisabled) listener?.onSeatClick(NORTH) }
            iGameTableSeatNorth.ivTableSeatNorthSeatWindIcon.setOnSecureClickListener { northClick() }
            iGameTableSeatNorth.tvTableSeatNorthName.setOnSecureClickListener { northClick() }
            iGameTableSeatNorth.tvTableSeatNorthPoints.setOnSecureClickListener { northClick() }
            iGameTableSeatNorth.vGameTableSeatNorthAuxStart.setOnSecureClickListener { northClick() }
            iGameTableSeatNorth.vGameTableSeatNorthAuxEnd.setOnSecureClickListener { northClick() }
            iGameTableSeatNorth.tvTableSeatNorthPenaltyPoints.setOnSecureClickListener { northClick() }
            iGameTableSeatNorth.iGameTableSeatNorthDiffs.tlTableSeatDiffs.setOnSecureClickListener { northClick() }

            btTableSeatsShowDiffs.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> listener?.toggleDiffsView(true)
                    MotionEvent.ACTION_UP -> listener?.toggleDiffsView(false)
                    else -> return@setOnTouchListener false
                }
                return@setOnTouchListener true
            }
        }
    }

    fun updateSeatsOrientation(seatOrientation: SeatOrientation) {
        when (seatOrientation) {
            OUT -> {
                binding.iGameTableSeatEast.rlTableSeatEastContainer.setPadding(0, margin, 0, 0)
                binding.iGameTableSeatWest.rlTableSeatWestContainer.setPadding(0, margin, 0, 0)
                binding.iGameTableSeatSouth.rlTableSeatSouthContainer.setPadding(0, 0, 0, margin)
                binding.iGameTableSeatNorth.rlTableSeatNorthContainer.setPadding(0, 0, 0, margin)
                binding.iGameTableSeatSouth.rlTableSeatSouthContainer.rotation = -90f
                binding.iGameTableSeatWest.rlTableSeatWestContainer.rotation = 180f
                binding.iGameTableSeatNorth.rlTableSeatNorthContainer.rotation = 90f
            }

            DOWN -> {
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

    fun toggleDiffsButton(areDiffsEnabled: Boolean) {
        if (areDiffsEnabled) {
            binding.btTableSeatsShowDiffs.visibility = VISIBLE
        } else {
            binding.btTableSeatsShowDiffs.visibility = INVISIBLE
            toggleDiffs(false)
        }
    }

    fun toggleDiffs(shouldShowDiffs: Boolean) {
        with(binding) {
            if (shouldShowDiffs) {
                iGameTableSeatEast.iGameTableSeatEastDiffs.llTableSeatDiffsContainer.visibility = VISIBLE
                iGameTableSeatSouth.iGameTableSeatSouthDiffs.llTableSeatDiffsContainer.visibility = VISIBLE
                iGameTableSeatWest.iGameTableSeatWestDiffs.llTableSeatDiffsContainer.visibility = VISIBLE
                iGameTableSeatNorth.iGameTableSeatNorthDiffs.llTableSeatDiffsContainer.visibility = VISIBLE
            } else {
                iGameTableSeatEast.iGameTableSeatEastDiffs.llTableSeatDiffsContainer.visibility = GONE
                iGameTableSeatSouth.iGameTableSeatSouthDiffs.llTableSeatDiffsContainer.visibility = GONE
                iGameTableSeatWest.iGameTableSeatWestDiffs.llTableSeatDiffsContainer.visibility = GONE
                iGameTableSeatNorth.iGameTableSeatNorthDiffs.llTableSeatDiffsContainer.visibility = GONE
            }
        }
    }
}
