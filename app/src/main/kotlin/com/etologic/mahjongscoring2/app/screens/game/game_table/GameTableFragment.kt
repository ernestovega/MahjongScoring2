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

package com.etologic.mahjongscoring2.app.screens.game.game_table

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.Settings
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.util.TypedValue.applyDimension
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.RelativeLayout
import android.widget.RelativeLayout.ALIGN_PARENT_BOTTOM
import android.widget.RelativeLayout.ALIGN_PARENT_END
import android.widget.RelativeLayout.ALIGN_PARENT_START
import android.widget.RelativeLayout.GONE
import android.widget.RelativeLayout.LayoutParams
import androidx.core.content.ContextCompat.getDrawable
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.R.drawable.ic_dice
import com.etologic.mahjongscoring2.R.drawable.ic_east
import com.etologic.mahjongscoring2.R.drawable.ic_north
import com.etologic.mahjongscoring2.R.drawable.ic_south
import com.etologic.mahjongscoring2.R.drawable.ic_trophy_white
import com.etologic.mahjongscoring2.R.drawable.ic_west
import com.etologic.mahjongscoring2.R.string
import com.etologic.mahjongscoring2.app.custom_views.GameTableSeats
import com.etologic.mahjongscoring2.app.screens.game.GameFragmentDirections
import com.etologic.mahjongscoring2.app.screens.game.GameUiState
import com.etologic.mahjongscoring2.app.screens.game.GameViewModel
import com.etologic.mahjongscoring2.app.screens.game.navigateOnceToRanking
import com.etologic.mahjongscoring2.app.utils.setOnSecureClickListener
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.databinding.GameTableFragmentBinding
import com.google.android.material.badge.BadgeDrawable.BOTTOM_END
import com.google.android.material.badge.BadgeDrawable.BOTTOM_START
import com.google.android.material.badge.BadgeDrawable.TOP_END
import com.google.android.material.badge.BadgeDrawable.TOP_START
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
@AndroidEntryPoint
class GameTableFragment : Fragment() {

    private var eastIcon: Drawable? = null
    private var southIcon: Drawable? = null
    private var westIcon: Drawable? = null
    private var northIcon: Drawable? = null
    private var _binding: GameTableFragmentBinding? = null
    private val binding get() = _binding!!
    private val gameViewModel: GameViewModel by hiltNavGraphViewModels(R.id.nav_graph_game)

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
        setListeners()
        startObservingGame()
    }

    private fun initResources() {
        eastIcon = context?.let { getDrawable(it, ic_east) }
        southIcon = context?.let { getDrawable(it, ic_south) }
        westIcon = context?.let { getDrawable(it, ic_west) }
        northIcon = context?.let { getDrawable(it, ic_north) }
    }

    private fun startObservingGame() {
        viewLifecycleOwner.lifecycleScope.launch { repeatOnLifecycle(STARTED) { gameViewModel.gameUiStateFlow.collect(::uiStateObserver) } }
    }

    private fun uiStateObserver(uiState: GameUiState) {
        when (uiState) {
            is GameUiState.Loading -> {}
            is GameUiState.Error -> {}
            is GameUiState.Loaded -> {
                setGameData(uiState.game)
                binding.gtsGameTableSeats.toggleDiffsButton(uiState.isDiffsCalcsFeatureEnabled)
                binding.gtsGameTableSeats.updateSeatState(uiState.selectedSeat, uiState.game)
                binding.gtsGameTableSeats.updateSeatsOrientation(uiState.seatsArrangement)
                binding.gtsGameTableSeats.toggleDiffsViews(uiState.shouldShowDiffs)
            }
        }
    }

    private fun setGameData(game: UiGame) {
        setGameName(game.gameName)
        setRoundStuff(game)
        binding.gtsGameTableSeats.setSeats(game, isUserFontTooBig())
    }

    private fun isUserFontTooBig(): Boolean =
        Settings.System.getFloat(requireActivity().contentResolver, Settings.System.FONT_SCALE, 1f) > 1.5f

    private fun setListeners() {
        with(binding) {
            fabGameTable.setOnSecureClickListener {
                findNavController().navigate(GameFragmentDirections.actionGameFragmentToDiceDialogFragment())
            }

            gtsGameTableSeats.setTableSeatsListener(object : GameTableSeats.GameTableSeatsListener {
                override fun onSeatClick(wind: TableWinds) {
                    gameViewModel.onSeatClicked(wind)
                    findNavController().navigate(GameFragmentDirections.actionGameFragmentToHandActionsDialogFragment())
                }

                override fun toggleDiffsView(shouldShowDiffs: Boolean) {
                    gameViewModel.toggleDiffsView(shouldShowDiffs)
                }
            })
        }
    }

    private fun setGameName(gameName: String) {
        if (gameName.isEmpty()) {
            binding.tvGameTableGameName.visibility = GONE
        } else {
            binding.tvGameTableGameName.text = gameName
            binding.tvGameTableGameName.visibility = VISIBLE
        }
    }

    private fun setRoundStuff(game: UiGame) {
        setFab(game)
        setRoundsNumAndWind(game)
    }

    private fun setFab(game: UiGame) {
        with(binding) {
            if (game.isEnded) {
                if (fabGameTable.tag != "ic_trophy_white_18dp") {
                    fabGameTable.tag = "ic_trophy_white_18dp"
                    fabGameTable.setImageResource(ic_trophy_white)
                    fabGameTable.setOnSecureClickListener { findNavController().navigateOnceToRanking() }
                    setFabPosition(BOTTOM_END)
                }
            } else {
                if (fabGameTable.tag != "ic_dice_multiple_white_24dp") {
                    fabGameTable.tag = "ic_dice_multiple_white_24dp"
                    fabGameTable.setImageResource(ic_dice)
                    fabGameTable.setOnSecureClickListener { findNavController().navigate(GameFragmentDirections.actionGameFragmentToDiceDialogFragment()) }
                }
                moveDice(game.uiRounds.size)
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
            val layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            val standardMarginInPx =
                applyDimension(COMPLEX_UNIT_DIP, 16f, resources.displayMetrics).toInt()
            layoutParams.setMargins(
                standardMarginInPx,
                standardMarginInPx,
                standardMarginInPx,
                standardMarginInPx
            )
            when (position) {
                BOTTOM_END -> {
                    tvGameTableRoundNumberBottomEnd.visibility = GONE
                    tvGameTableRoundNumberTopStart.visibility = VISIBLE
                    tvGameTableRoundNumberTopEnd.visibility = VISIBLE
                    tvGameTableRoundNumberBottomStart.visibility = VISIBLE
                    layoutParams.removeRule(RelativeLayout.BELOW)
                    layoutParams.addRule(ALIGN_PARENT_BOTTOM)
                    layoutParams.addRule(ALIGN_PARENT_END)
                }

                TOP_START -> {
                    tvGameTableRoundNumberBottomEnd.visibility = VISIBLE
                    tvGameTableRoundNumberTopStart.visibility = GONE
                    tvGameTableRoundNumberTopEnd.visibility = VISIBLE
                    tvGameTableRoundNumberBottomStart.visibility = VISIBLE
                    layoutParams.addRule(RelativeLayout.BELOW, R.id.tvGameTableGameName)
                    layoutParams.addRule(ALIGN_PARENT_START)
                }

                TOP_END -> {
                    tvGameTableRoundNumberBottomEnd.visibility = VISIBLE
                    tvGameTableRoundNumberTopStart.visibility = VISIBLE
                    tvGameTableRoundNumberTopEnd.visibility = GONE
                    tvGameTableRoundNumberBottomStart.visibility = VISIBLE
                    layoutParams.addRule(RelativeLayout.BELOW, R.id.tvGameTableGameName)
                    layoutParams.addRule(ALIGN_PARENT_END)
                }

                BOTTOM_START -> {
                    tvGameTableRoundNumberBottomEnd.visibility = VISIBLE
                    tvGameTableRoundNumberTopStart.visibility = VISIBLE
                    tvGameTableRoundNumberTopEnd.visibility = VISIBLE
                    tvGameTableRoundNumberBottomStart.visibility = GONE
                    layoutParams.removeRule(RelativeLayout.BELOW)
                    layoutParams.addRule(ALIGN_PARENT_BOTTOM)
                    layoutParams.addRule(ALIGN_PARENT_START)
                }
            }
            fabGameTable.layoutParams = layoutParams
        }
    }

    private fun setRoundsNumAndWind(game: UiGame) {
        with(binding) {
            if (game.isEnded) {
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
                val numRounds = game.uiRounds.size

                game.uiRounds.size.toString().let {
                    tvGameTableRoundNumberTopStart.text = it
                    tvGameTableRoundNumberTopEnd.text = it
                    tvGameTableRoundNumberBottomStart.text = it
                    tvGameTableRoundNumberBottomEnd.text = it
                }

                when {
                    numRounds < 5 -> {
                        tvGameTableRoundNumberTopStart
                            .setCompoundDrawablesWithIntrinsicBounds(null, null, null, eastIcon)
                        tvGameTableRoundNumberTopEnd
                            .setCompoundDrawablesWithIntrinsicBounds(null, null, null, eastIcon)
                        tvGameTableRoundNumberBottomStart
                            .setCompoundDrawablesWithIntrinsicBounds(null, null, null, eastIcon)
                        tvGameTableRoundNumberBottomEnd
                            .setCompoundDrawablesWithIntrinsicBounds(null, null, null, eastIcon)
                    }

                    numRounds < 9 -> {
                        tvGameTableRoundNumberTopStart
                            .setCompoundDrawablesWithIntrinsicBounds(null, null, null, southIcon)
                        tvGameTableRoundNumberTopEnd
                            .setCompoundDrawablesWithIntrinsicBounds(null, null, null, southIcon)
                        tvGameTableRoundNumberBottomStart
                            .setCompoundDrawablesWithIntrinsicBounds(null, null, null, southIcon)
                        tvGameTableRoundNumberBottomEnd
                            .setCompoundDrawablesWithIntrinsicBounds(null, null, null, southIcon)
                    }

                    numRounds < 13 -> {
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
