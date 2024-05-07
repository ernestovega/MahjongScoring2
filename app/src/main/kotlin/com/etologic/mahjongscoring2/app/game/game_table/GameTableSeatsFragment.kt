/*
 *     Copyright © 2023  Ernesto Vega de la Iglesia Soria
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
package com.etologic.mahjongscoring2.app.game.game_table

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.Settings
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.util.TypedValue.applyDimension
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout.GONE
import android.widget.RelativeLayout.INVISIBLE
import android.widget.RelativeLayout.VISIBLE
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getDrawable
import androidx.fragment.app.activityViewModels
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.R.color
import com.etologic.mahjongscoring2.app.base.BaseFragment
import com.etologic.mahjongscoring2.app.extensions.setOnSecureClickListener
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel
import com.etologic.mahjongscoring2.app.model.SeatStates
import com.etologic.mahjongscoring2.app.model.SeatStates.DISABLED
import com.etologic.mahjongscoring2.app.model.SeatStates.NORMAL
import com.etologic.mahjongscoring2.app.model.SeatStates.SELECTED
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
import com.etologic.mahjongscoring2.databinding.GameTableSeatsFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale.getDefault

@AndroidEntryPoint
class GameTableSeatsFragment : BaseFragment() {

    companion object {
        const val TAG = "GameTableSeatsFragment"
    }

    interface TableSeatsListener {
        fun onSeatClick(wind: TableWinds)
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

    private val activityViewModel: GameViewModel by activityViewModels()

    private var listener: TableSeatsListener? = null
    private var selectedPlayer: TableWinds = NONE
    private var areSeatsDisabled: Boolean = false
    private var margin = 0

    fun setTableSeatsListener(tableSeatsListener: TableSeatsListener) {
        listener = tableSeatsListener
    }

    fun updateSeatState(wind: TableWinds) {
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

    fun setSeats(uiGame: UiGame) {
        selectedPlayer = NONE
        setStates(getSeatsStates(uiGame))
        val playersTotalPointsByCurrentSeat = uiGame.getPlayersTotalPointsByCurrentSeat()
        setPoints(playersTotalPointsByCurrentSeat.map { String.format(getDefault(), "%d", it) })
        setPointsDiffs(uiGame)
        setWinds(uiGame.getSeatsCurrentWind(uiGame.uiRounds.size))
        setNames(uiGame.getPlayersNamesByCurrentRoundSeat())
        setPenalties(uiGame.getPlayersPenaltiesByCurrentSeat(), uiGame.dbGame.isEnded)
        setPointsDiffs(activityViewModel.game)
    }

    private fun getSeatsStates(uiGame: UiGame): Array<SeatStates> =
        if (uiGame.dbGame.isEnded) arrayOf(DISABLED, DISABLED, DISABLED, DISABLED) else getSeatsStates()

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

    private fun setPointsDiffs(uiGame: UiGame?) {
        if (!isUserFontTooBig()) {
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

                isUserFontTooBig()

            } else {
                toggleDiffs(false)
            }
        } else {
            toggleDiffs(false)
        }
    }

    private fun isUserFontTooBig(): Boolean =
        Settings.System.getFloat(
            activity?.contentResolver,
            Settings.System.FONT_SCALE,
            1f
        ) > 1.5f

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        margin = applyDimension(COMPLEX_UNIT_DIP, 16f, resources.displayMetrics).toInt()
    }

    private var _binding: GameTableSeatsFragmentBinding? = null
    private val binding get() = _binding!!

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

    @SuppressLint("ClickableViewAccessibility")
    private fun initListeners() {
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
                return@setOnTouchListener when (event.action) {
                    MotionEvent.ACTION_DOWN -> { activityViewModel.showDiffs(); true }
                    MotionEvent.ACTION_UP -> { activityViewModel.hideDiffs(); true }
                    else -> false
                }
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
