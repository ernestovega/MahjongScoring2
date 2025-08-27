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
import com.etologic.mahjongscoring2.app.screens.game.dialogs.RankingTableHelper
import com.etologic.mahjongscoring2.app.utils.setOnSecureClickListener
import com.etologic.mahjongscoring2.business.model.dtos.RankingData
import com.etologic.mahjongscoring2.business.model.dtos.SeatDiffs
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.model.enums.SeatsArrangement
import com.etologic.mahjongscoring2.business.model.enums.SeatsArrangement.DOWN
import com.etologic.mahjongscoring2.business.model.enums.SeatsArrangement.OUT
import com.etologic.mahjongscoring2.business.model.enums.SeatsArrangement.RANKING
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.EAST
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NONE
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NORTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.SOUTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.WEST
import com.etologic.mahjongscoring2.databinding.CustomTableSeatByRankingBinding
import com.etologic.mahjongscoring2.databinding.CustomTableSeatsBinding
import java.util.Locale.getDefault

class GameTableSeats(context: Context, attributeSet: AttributeSet) :
    RelativeLayout(context, attributeSet) {

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

        setListenersBySeat()
//        setListenersByRanking()
    }

    fun setTableSeatsListener(gameTableSeatsListener: GameTableSeatsListener) {
        listener = gameTableSeatsListener
    }

    fun updateSeatState(wind: TableWinds, game: UiGame) {
        selectedPlayer = wind
        setStates(getSeatsStates(game))
    }

    fun setSeats(game: UiGame, isUserFontTooBig: Boolean) {
        selectedPlayer = NONE
        setSeatsBySeat(game, isUserFontTooBig)
        setSeatsByRanking(game, isUserFontTooBig)
        setStates(getSeatsStates(game)) // Always after setSeatsByRanking
    }

    private fun setSeatsBySeat(
        game: UiGame,
        isUserFontTooBig: Boolean
    ) {
        val playersTotalPointsByCurrentSeat = game.getPlayersTotalPointsByCurrentSeat()
        setPoints(playersTotalPointsByCurrentSeat.map { it.toString() })
        setWinds(game.getSeatsCurrentWind(game.uiRounds.size))
        setNames(game.getPlayersNamesByCurrentSeat())
        setPenalties(game.getPlayersPenaltiesByCurrentSeat(), game.isEnded)
        setDiffsBySeat(game, isUserFontTooBig)
    }

    private fun getSeatsStates(uiGame: UiGame): Array<SeatStates> =
        if (uiGame.isEnded) {
            arrayOf(DISABLED, DISABLED, DISABLED, DISABLED)
        } else {
            when (selectedPlayer) {
                NONE -> arrayOf(NORMAL, NORMAL, NORMAL, NORMAL)
                EAST -> arrayOf(SELECTED, NORMAL, NORMAL, NORMAL)
                SOUTH -> arrayOf(NORMAL, SELECTED, NORMAL, NORMAL)
                WEST -> arrayOf(NORMAL, NORMAL, SELECTED, NORMAL)
                NORTH -> arrayOf(NORMAL, NORMAL, NORMAL, SELECTED)
            }
        }

    private fun setStates(states: Array<SeatStates>) {
        areSeatsDisabled =
            states[0] == DISABLED && states[1] == DISABLED && states[2] == DISABLED && states[3] == DISABLED
        listOf(
            binding.iTableSeatsBySeat.iGameTableSeatEast,
            binding.iTableSeatsBySeat.iGameTableSeatSouth,
            binding.iTableSeatsBySeat.iGameTableSeatWest,
            binding.iTableSeatsBySeat.iGameTableSeatNorth,
        ).forEachIndexed { index, seat ->
            setState(
                ivWind = seat.ivTableSeatBySeatWindIcon,
                tvName = seat.tvTableSeatBySeatName,
                tvPoints = seat.tvTableSeatBySeatPoints,
                state = states[index]
            )
        }
        listOf(
            binding.iTableSeatsByRanking.iGameTableSeatByRankingFirst,
            binding.iTableSeatsByRanking.iGameTableSeatByRankingSecond,
            binding.iTableSeatsByRanking.iGameTableSeatByRankingThird,
            binding.iTableSeatsByRanking.iGameTableSeatByRankingFourth,
        ).forEach { seat ->
            setState(
                ivWind = seat.ivTableSeatRankingSeatWindIcon,
                tvName = seat.tvTableSeatRankingName,
                tvPoints = seat.tvTableSeatRankingPoints,
                state = states[when (seat.ivTableSeatRankingSeatWindIcon.drawable) {
                    eastIcon -> EAST.code
                    southIcon -> SOUTH.code
                    westIcon -> WEST.code
                    northIcon -> NORTH.code
                    else -> return
                }]
            )
        }
    }

    private fun setState(
        ivWind: ImageView?,
        tvName: TextView,
        tvPoints: TextView,
        state: SeatStates?
    ) {
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
        with(binding.iTableSeatsBySeat) {
            iGameTableSeatEast.tvTableSeatBySeatPoints.text = points[EAST.code]
            iGameTableSeatSouth.tvTableSeatBySeatPoints.text = points[SOUTH.code]
            iGameTableSeatWest.tvTableSeatBySeatPoints.text = points[WEST.code]
            iGameTableSeatNorth.tvTableSeatBySeatPoints.text = points[NORTH.code]
        }
    }

    private fun setDiffsBySeat(uiGame: UiGame?, isUserFontTooBig: Boolean) {
        val tableDiffs = uiGame?.getTableDiffs()
        if (isUserFontTooBig || tableDiffs == null) {
            toggleDiffsViews(false)
        } else {
            listOf(
                binding.iTableSeatsBySeat.iGameTableSeatEast.iGameTableSeatBySeatDiffs,
                binding.iTableSeatsBySeat.iGameTableSeatSouth.iGameTableSeatBySeatDiffs,
                binding.iTableSeatsBySeat.iGameTableSeatWest.iGameTableSeatBySeatDiffs,
                binding.iTableSeatsBySeat.iGameTableSeatNorth.iGameTableSeatBySeatDiffs,
            ).forEachIndexed { index, seat ->
                setSeatDiffs(
                    tableDiffs.seatsDiffs[index],
                    seat.tvTableSeatDiffsFirstSelfPick,
                    seat.tvTableSeatDiffsFirstDirectHu,
                    seat.tvTableSeatDiffsFirstIndirectHu,
                    seat.tlTableSeatDiffs,
                    seat.tvTableSeatDiffsSecondSelfPick,
                    seat.tvTableSeatDiffsSecondDirectHu,
                    seat.tvTableSeatDiffsSecondIndirectHu,
                    seat.trTableSeatDiffsSecond,
                    seat.tvTableSeatDiffsThirdSelfPick,
                    seat.tvTableSeatDiffsThirdDirectHu,
                    seat.tvTableSeatDiffsThirdIndirectHu,
                    seat.trTableSeatDiffsThird,
                )
            }
        }
    }

    private fun setSeatDiffs(
        seatDiffs: SeatDiffs,
        tvFirstSelfPick: TextView,
        tvFirstDirectHu: TextView,
        tvFirstIndirectHu: TextView,
        tlGameTable: TableLayout,
        tvSecondSelfPick: TextView,
        tvSecondDirectHu: TextView,
        tvSecondIndirectHu: TextView,
        trDiffsSecond: TableRow,
        tvThirdSelfPick: TextView,
        tvThirdDirectHu: TextView,
        tvThirdIndirectHu: TextView,
        trDiffsThird: TableRow,
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
        with(binding.iTableSeatsBySeat) {
            listOf(
                iGameTableSeatEast.tvTableSeatBySeatPenaltyPoints,
                iGameTableSeatSouth.tvTableSeatBySeatPenaltyPoints,
                iGameTableSeatWest.tvTableSeatBySeatPenaltyPoints,
                iGameTableSeatNorth.tvTableSeatBySeatPenaltyPoints,
            ).forEachIndexed { index, view -> setPenaltyPoints(isEnded, view, penalties[index]) }
        }
    }

    private fun setPenaltyPoints(isEnded: Boolean, textView: TextView, penaltyPoints: Int) {
        if (!isEnded) {
            textView.text = String.format(getDefault(), "%+d", penaltyPoints)
            (if (penaltyPoints < 0) redColor else greenColor)?.let { textView.setTextColor(it) }
            textView.visibility = if (penaltyPoints != 0) VISIBLE else GONE
        } else {
            textView.visibility = GONE
        }
    }

    private fun setWinds(winds: Array<TableWinds>) {
        with(binding.iTableSeatsBySeat) {
            setWindIcon(iGameTableSeatEast.ivTableSeatBySeatWindIcon, winds[EAST.code])
            setWindIcon(iGameTableSeatSouth.ivTableSeatBySeatWindIcon, winds[SOUTH.code])
            setWindIcon(iGameTableSeatWest.ivTableSeatBySeatWindIcon, winds[WEST.code])
            setWindIcon(iGameTableSeatNorth.ivTableSeatBySeatWindIcon, winds[NORTH.code])
        }
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
        with(binding.iTableSeatsBySeat) {
            setName(iGameTableSeatEast.tvTableSeatBySeatName, names[EAST.code])
            setName(iGameTableSeatSouth.tvTableSeatBySeatName, names[SOUTH.code])
            setName(iGameTableSeatWest.tvTableSeatBySeatName, names[WEST.code])
            setName(iGameTableSeatNorth.tvTableSeatBySeatName, names[NORTH.code])
        }
    }

    private fun setName(textView: TextView, name: String?) {
        textView.text = name ?: ""
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setListenersBySeat() {
        listOf(
            binding.iTableSeatsBySeat.iGameTableSeatEast,
            binding.iTableSeatsBySeat.iGameTableSeatSouth,
            binding.iTableSeatsBySeat.iGameTableSeatWest,
            binding.iTableSeatsBySeat.iGameTableSeatNorth,
        ).forEachIndexed { index, seat ->
            val onClick = {
                if (!areSeatsDisabled) TableWinds.from(index)?.let { listener?.onSeatClick(it) }
            }
            seat.ivTableSeatBySeatWindIcon.setOnSecureClickListener { onClick() }
            seat.tvTableSeatBySeatName.setOnSecureClickListener { onClick() }
            seat.tvTableSeatBySeatPoints.setOnSecureClickListener { onClick() }
            seat.tvTableSeatBySeatPenaltyPoints.setOnSecureClickListener { onClick() }
            seat.vGameTableSeatBySeatAuxStart.setOnSecureClickListener { onClick() }
            seat.vGameTableSeatBySeatAuxEnd.setOnSecureClickListener { onClick() }
            seat.iGameTableSeatBySeatDiffs.tlTableSeatDiffs.setOnSecureClickListener { onClick() }
        }
        binding.iTableSeatsBySeat.btTableSeatsShowDiffs.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> listener?.toggleDiffsView(true)
                MotionEvent.ACTION_UP -> listener?.toggleDiffsView(false)
                else -> return@setOnTouchListener false
            }
            return@setOnTouchListener true
        }
    }

    fun updateSeatsOrientation(seatsArrangement: SeatsArrangement) {
        with(binding.iTableSeatsBySeat) {
            when (seatsArrangement) {
                OUT -> {
                    binding.iTableSeatsByRanking.llTableSeatsByRanking.visibility = GONE
                    iGameTableSeatEast.rlTableSeatBySeatContainer.setPadding(0, margin, 0, 0)
                    iGameTableSeatWest.rlTableSeatBySeatContainer.setPadding(0, margin, 0, 0)
                    iGameTableSeatSouth.rlTableSeatBySeatContainer.setPadding(0, 0, 0, margin)
                    iGameTableSeatNorth.rlTableSeatBySeatContainer.setPadding(0, 0, 0, margin)
                    iGameTableSeatSouth.rlTableSeatBySeatContainer.rotation = -90f
                    iGameTableSeatWest.rlTableSeatBySeatContainer.rotation = 180f
                    iGameTableSeatNorth.rlTableSeatBySeatContainer.rotation = 90f
                    rlTableSeatsBySeat.visibility = VISIBLE
                }

                DOWN -> {
                    binding.iTableSeatsByRanking.llTableSeatsByRanking.visibility = GONE
                    iGameTableSeatEast.rlTableSeatBySeatContainer.setPadding(0, margin * 3, 0, 0)
                    iGameTableSeatWest.rlTableSeatBySeatContainer.setPadding(0, 0, 0, margin * 3)
                    iGameTableSeatSouth.rlTableSeatBySeatContainer.setPadding(0, 0, 0, 0)
                    iGameTableSeatNorth.rlTableSeatBySeatContainer.setPadding(0, 0, 0, 0)
                    iGameTableSeatSouth.rlTableSeatBySeatContainer.rotation = 0f
                    iGameTableSeatWest.rlTableSeatBySeatContainer.rotation = 0f
                    iGameTableSeatNorth.rlTableSeatBySeatContainer.rotation = 0f
                    rlTableSeatsBySeat.visibility = VISIBLE
                }

                RANKING -> {
                    rlTableSeatsBySeat.visibility = GONE
                    binding.iTableSeatsByRanking.llTableSeatsByRanking.visibility = VISIBLE
                }
            }
        }
    }

    fun toggleDiffsButton(areDiffsEnabled: Boolean) {
        with(binding) {
            if (areDiffsEnabled) {
                iTableSeatsBySeat.btTableSeatsShowDiffs.visibility = VISIBLE
                iTableSeatsByRanking.btTableSeatsByRankingShowDiffs.visibility = VISIBLE
            } else {
                iTableSeatsBySeat.btTableSeatsShowDiffs.visibility = INVISIBLE
                iTableSeatsByRanking.btTableSeatsByRankingShowDiffs.visibility = INVISIBLE
                toggleDiffsViews(false)
            }
        }
    }

    fun toggleDiffsViews(shouldShowDiffs: Boolean) {
        listOf(
            binding.iTableSeatsBySeat.iGameTableSeatEast.iGameTableSeatBySeatDiffs.llTableSeatDiffsContainer,
            binding.iTableSeatsBySeat.iGameTableSeatSouth.iGameTableSeatBySeatDiffs.llTableSeatDiffsContainer,
            binding.iTableSeatsBySeat.iGameTableSeatWest.iGameTableSeatBySeatDiffs.llTableSeatDiffsContainer,
            binding.iTableSeatsBySeat.iGameTableSeatNorth.iGameTableSeatBySeatDiffs.llTableSeatDiffsContainer,
            binding.iTableSeatsByRanking.iGameTableSeatByRankingFirst.iGameTableSeatRankingDiffs.llTableSeatDiffsContainer,
            binding.iTableSeatsByRanking.iGameTableSeatByRankingSecond.iGameTableSeatRankingDiffs.llTableSeatDiffsContainer,
            binding.iTableSeatsByRanking.iGameTableSeatByRankingThird.iGameTableSeatRankingDiffs.llTableSeatDiffsContainer,
            binding.iTableSeatsByRanking.iGameTableSeatByRankingFourth.iGameTableSeatRankingDiffs.llTableSeatDiffsContainer,
        ).map { it.visibility = if (shouldShowDiffs) VISIBLE else GONE }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setSeatsByRanking(game: UiGame, isUserFontTooBig: Boolean) {
        val rankingData: RankingData? = RankingTableHelper.generateRankingTable(game)
        if (rankingData == null) return
        val playersNamesByCurrentSeat = game.getPlayersNamesByCurrentSeat()
        val getPlayersPenaltiesByCurrentSeat = game.getPlayersPenaltiesByCurrentSeat()
        val tableDiffs = if (!isUserFontTooBig) {
            game.getTableDiffs()
        } else null
        if (tableDiffs == null) toggleDiffsViews(false)

        fun setSeatByRanking(container: CustomTableSeatByRankingBinding, index: Int) {
            val playerRankingData = rankingData.sortedPlayersRankings[index]
            val seat = TableWinds.from(playersNamesByCurrentSeat.indexOf(playerRankingData.name))
            if (seat == null) return
            val penaltyPoints = getPlayersPenaltiesByCurrentSeat[seat.code]
            // Views
            container.tvTableSeatRankingPosition.text = context.getString(
                when (index) {
                    0 -> R.string._1st
                    1 -> R.string._2nd
                    2 -> R.string._3rd
                    3 -> R.string._4th
                    else -> return
                }
            )
            container.tvTableSeatRankingPoints.text = playerRankingData.score.toString()
            setWindIcon(container.ivTableSeatRankingSeatWindIcon, seat)
            setPenaltyPoints(game.isEnded, container.tvTableSeatRankingPenaltyPoints, penaltyPoints)
            container.tvTableSeatRankingName.text = playerRankingData.name
            // Listener
            container.rlTableSeatRankingContainer.setOnSecureClickListener {
                if (!areSeatsDisabled) listener?.onSeatClick(seat)
            }
            // Diffs
            tableDiffs?.let {
                with(container.iGameTableSeatRankingDiffs) {
                    setSeatDiffs(
                        tableDiffs.seatsDiffs[seat.code],
                        tvTableSeatDiffsFirstSelfPick,
                        tvTableSeatDiffsFirstDirectHu,
                        tvTableSeatDiffsFirstIndirectHu,
                        tlTableSeatDiffs,
                        tvTableSeatDiffsSecondSelfPick,
                        tvTableSeatDiffsSecondDirectHu,
                        tvTableSeatDiffsSecondIndirectHu,
                        trTableSeatDiffsSecond,
                        tvTableSeatDiffsThirdSelfPick,
                        tvTableSeatDiffsThirdDirectHu,
                        tvTableSeatDiffsThirdIndirectHu,
                        trTableSeatDiffsThird,
                    )
                }
            }
        }

        with(binding.iTableSeatsByRanking) {
            listOf(
                iGameTableSeatByRankingFirst,
                iGameTableSeatByRankingSecond,
                iGameTableSeatByRankingThird,
                iGameTableSeatByRankingFourth,
            ).mapIndexed { index, container -> setSeatByRanking(container, index) }
            btTableSeatsByRankingShowDiffs.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> listener?.toggleDiffsView(true)
                    MotionEvent.ACTION_UP -> listener?.toggleDiffsView(false)
                    else -> return@setOnTouchListener false
                }
                return@setOnTouchListener true
            }
        }
    }
}
