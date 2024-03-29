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

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.util.TypedValue.applyDimension
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.RelativeLayout.ALIGN_PARENT_BOTTOM
import android.widget.RelativeLayout.ALIGN_PARENT_END
import android.widget.RelativeLayout.ALIGN_PARENT_START
import android.widget.RelativeLayout.ALIGN_PARENT_TOP
import android.widget.RelativeLayout.GONE
import android.widget.RelativeLayout.LayoutParams
import androidx.core.content.ContextCompat.getDrawable
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.R.drawable.ic_dice_multiple_white_24dp
import com.etologic.mahjongscoring2.R.drawable.ic_east
import com.etologic.mahjongscoring2.R.drawable.ic_north
import com.etologic.mahjongscoring2.R.drawable.ic_south
import com.etologic.mahjongscoring2.R.drawable.ic_trophy_white_18dp
import com.etologic.mahjongscoring2.R.drawable.ic_west
import com.etologic.mahjongscoring2.R.string
import com.etologic.mahjongscoring2.app.base.BaseFragment
import com.etologic.mahjongscoring2.app.extensions.setOnSecureClickListener
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel.GameScreens.DICE
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel.GameScreens.RANKING
import com.etologic.mahjongscoring2.app.game.game_table.GameTableSeatsFragment.Companion.TAG
import com.etologic.mahjongscoring2.app.game.game_table.GameTableSeatsFragment.TableSeatsListener
import com.etologic.mahjongscoring2.business.model.entities.UIGame
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.databinding.GameTableFragmentBinding
import com.google.android.material.badge.BadgeDrawable.BOTTOM_END
import com.google.android.material.badge.BadgeDrawable.BOTTOM_START
import com.google.android.material.badge.BadgeDrawable.TOP_END
import com.google.android.material.badge.BadgeDrawable.TOP_START
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GameTableFragment : BaseFragment(), TableSeatsListener {

    enum class GameTablePages(val code: Int) {
        TABLE(0),
        LIST(1);

        companion object {
            fun getFromCode(code: Int): GameTablePages = if (code == 1) LIST else TABLE
        }
    }

    private var tableSeats: GameTableSeatsFragment = GameTableSeatsFragment()
    private var eastIcon: Drawable? = null
    private var southIcon: Drawable? = null
    private var westIcon: Drawable? = null
    private var northIcon: Drawable? = null
    private var _binding: GameTableFragmentBinding? = null
    private val binding get() = _binding!!

    private val activityViewModel: GameViewModel by activityViewModels()

    override fun onSeatClick(wind: TableWinds) {
        activityViewModel.onSeatClicked(wind)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GameTableFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initResources()
        initTableSeats()
        setOnClickListeners()
        startObservingTable()
    }

    private fun initResources() {
        eastIcon = context?.let { getDrawable(it, ic_east) }
        southIcon = context?.let { getDrawable(it, ic_south) }
        westIcon = context?.let { getDrawable(it, ic_west) }
        northIcon = context?.let { getDrawable(it, ic_north) }
    }

    private fun initTableSeats() {
        childFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .add(R.id.fcvGameTableSeats, tableSeats, TAG)
            .commit()
    }

    private fun setOnClickListeners() {
        tableSeats.setTableSeatsListener(this)
        binding.fabGameTable.setOnSecureClickListener { activityViewModel.navigateTo(DICE) }
    }

    private fun startObservingTable() {
        activityViewModel.getSelectedSeat().observe(viewLifecycleOwner) { tableSeats.updateSeatState(it) }
        activityViewModel.getSeatsOrientation().observe(viewLifecycleOwner) { tableSeats.updateSeatsOrientation(it) }
        activityViewModel.shouldShowDiffs().observe(viewLifecycleOwner) { tableSeats.toggleDiffs(it) }

        lifecycleScope.launch { activityViewModel.gameFlow.collect(::gameObserver) }
    }

    private fun gameObserver(it: UIGame) {
        tableSeats.setSeats(it)
        this.setRoundStuff(it)
    }

    private fun setRoundStuff(uiGame: UIGame) {
        val isGameEnded: Boolean = uiGame.dbGame.isEnded
        val roundNumber: Int = uiGame.rounds.size
        setFab(isGameEnded, roundNumber)
        setRoundNumsAndWinds(isGameEnded, roundNumber)
    }

    private fun setFab(isGameEnded: Boolean, roundNumber: Int) {
        with(binding) {
            if (isGameEnded) {
                if (fabGameTable.tag != "ic_trophy_white_18dp") {
                    fabGameTable.tag = "ic_trophy_white_18dp"
                    fabGameTable.setImageResource(ic_trophy_white_18dp)
                    fabGameTable.setOnSecureClickListener { activityViewModel.navigateTo(RANKING) }
                    setFabPosition(BOTTOM_END)
                }

            } else {
                if (fabGameTable.tag != "ic_dice_multiple_white_24dp") {
                    fabGameTable.tag = "ic_dice_multiple_white_24dp"
                    fabGameTable.setImageResource(ic_dice_multiple_white_24dp)
                    fabGameTable.setOnSecureClickListener { activityViewModel.navigateTo(DICE) }
                }
                moveDice(roundNumber)
            }
            if (fabGameTable.visibility != VISIBLE) fabGameTable.visibility = VISIBLE
        }
    }

    private fun moveDice(roundNumber: Int) {
        when (roundNumber) {
            1 -> setFabPosition(BOTTOM_END)
            2 -> setFabPosition(TOP_END)
            3 -> setFabPosition(TOP_START)
            4 -> setFabPosition(BOTTOM_START)
            5 -> setFabPosition(BOTTOM_END)
            6 -> setFabPosition(TOP_END)
            7 -> setFabPosition(TOP_START)
            8 -> setFabPosition(BOTTOM_START)
            9 -> setFabPosition(BOTTOM_END)
            10 -> setFabPosition(TOP_END)
            11 -> setFabPosition(TOP_START)
            12 -> setFabPosition(BOTTOM_START)
            13 -> setFabPosition(BOTTOM_END)
            14 -> setFabPosition(TOP_END)
            15 -> setFabPosition(TOP_START)
            16 -> setFabPosition(BOTTOM_START)
        }
    }

    private fun setFabPosition(position: Int) {
        with(binding) {
            val params = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            val standardMarginInPx = applyDimension(COMPLEX_UNIT_DIP, 16f, resources.displayMetrics).toInt()
            params.setMargins(standardMarginInPx, standardMarginInPx, standardMarginInPx, standardMarginInPx)
            when (position) {
                BOTTOM_END -> {
                    tvGameTableRoundNumberBottomEnd.visibility = GONE
                    tvGameTableRoundNumberTopStart.visibility = VISIBLE
                    tvGameTableRoundNumberTopEnd.visibility = VISIBLE
                    tvGameTableRoundNumberBottomStart.visibility = VISIBLE
                    params.addRule(ALIGN_PARENT_BOTTOM)
                    params.addRule(ALIGN_PARENT_END)
                }

                TOP_START -> {
                    tvGameTableRoundNumberBottomEnd.visibility = VISIBLE
                    tvGameTableRoundNumberTopStart.visibility = GONE
                    tvGameTableRoundNumberTopEnd.visibility = VISIBLE
                    tvGameTableRoundNumberBottomStart.visibility = VISIBLE
                    params.addRule(ALIGN_PARENT_TOP)
                    params.addRule(ALIGN_PARENT_START)
                }

                TOP_END -> {
                    tvGameTableRoundNumberBottomEnd.visibility = VISIBLE
                    tvGameTableRoundNumberTopStart.visibility = VISIBLE
                    tvGameTableRoundNumberTopEnd.visibility = GONE
                    tvGameTableRoundNumberBottomStart.visibility = VISIBLE
                    params.addRule(ALIGN_PARENT_TOP)
                    params.addRule(ALIGN_PARENT_END)
                }

                BOTTOM_START -> {
                    tvGameTableRoundNumberBottomEnd.visibility = VISIBLE
                    tvGameTableRoundNumberTopStart.visibility = VISIBLE
                    tvGameTableRoundNumberTopEnd.visibility = VISIBLE
                    tvGameTableRoundNumberBottomStart.visibility = GONE
                    params.addRule(ALIGN_PARENT_BOTTOM)
                    params.addRule(ALIGN_PARENT_START)
                }
            }
            fabGameTable.layoutParams = params
        }
    }

    private fun setRoundNumsAndWinds(isGameEnded: Boolean, roundNumber: Int) {
        with(binding) {
            if (isGameEnded) {
                val endString = getString(string.end)

                tvGameTableRoundNumberTopStart.text = endString
                tvGameTableRoundNumberTopEnd.text = endString
                tvGameTableRoundNumberBottomStart.text = endString
                tvGameTableRoundNumberBottomEnd.text = endString

                tvGameTableRoundNumberTopStart
                    .setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                tvGameTableRoundNumberTopEnd
                    .setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                tvGameTableRoundNumberBottomStart
                    .setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                tvGameTableRoundNumberBottomEnd
                    .setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            } else {
                roundNumber.toString().let {
                    tvGameTableRoundNumberTopStart.text = it
                    tvGameTableRoundNumberTopEnd.text = it
                    tvGameTableRoundNumberBottomStart.text = it
                    tvGameTableRoundNumberBottomEnd.text = it
                }

                when {
                    roundNumber < 5 -> {
                        tvGameTableRoundNumberTopStart
                            .setCompoundDrawablesWithIntrinsicBounds(null, null, null, eastIcon)
                        tvGameTableRoundNumberTopEnd
                            .setCompoundDrawablesWithIntrinsicBounds(null, null, null, eastIcon)
                        tvGameTableRoundNumberBottomStart
                            .setCompoundDrawablesWithIntrinsicBounds(null, null, null, eastIcon)
                        tvGameTableRoundNumberBottomEnd
                            .setCompoundDrawablesWithIntrinsicBounds(null, null, null, eastIcon)
                    }

                    roundNumber < 9 -> {
                        tvGameTableRoundNumberTopStart
                            .setCompoundDrawablesWithIntrinsicBounds(null, null, null, southIcon)
                        tvGameTableRoundNumberTopEnd
                            .setCompoundDrawablesWithIntrinsicBounds(null, null, null, southIcon)
                        tvGameTableRoundNumberBottomStart
                            .setCompoundDrawablesWithIntrinsicBounds(null, null, null, southIcon)
                        tvGameTableRoundNumberBottomEnd
                            .setCompoundDrawablesWithIntrinsicBounds(null, null, null, southIcon)
                    }

                    roundNumber < 13 -> {
                        tvGameTableRoundNumberTopStart
                            .setCompoundDrawablesWithIntrinsicBounds(null, null, null, westIcon)
                        tvGameTableRoundNumberTopEnd
                            .setCompoundDrawablesWithIntrinsicBounds(null, null, null, westIcon)
                        tvGameTableRoundNumberBottomStart
                            .setCompoundDrawablesWithIntrinsicBounds(null, null, null, westIcon)
                        tvGameTableRoundNumberBottomEnd
                            .setCompoundDrawablesWithIntrinsicBounds(null, null, null, westIcon)
                    }

                    else -> {
                        tvGameTableRoundNumberTopStart
                            .setCompoundDrawablesWithIntrinsicBounds(null, null, null, northIcon)
                        tvGameTableRoundNumberTopEnd
                            .setCompoundDrawablesWithIntrinsicBounds(null, null, null, northIcon)
                        tvGameTableRoundNumberBottomStart
                            .setCompoundDrawablesWithIntrinsicBounds(null, null, null, northIcon)
                        tvGameTableRoundNumberBottomEnd
                            .setCompoundDrawablesWithIntrinsicBounds(null, null, null, northIcon)
                    }
                }
            }
        }
    }
}
