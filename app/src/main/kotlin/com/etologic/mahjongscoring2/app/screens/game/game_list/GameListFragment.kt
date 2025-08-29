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

package com.etologic.mahjongscoring2.app.screens.game.game_list

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.screens.game.GameFragment
import com.etologic.mahjongscoring2.app.screens.game.GameFragment.GamePages.LIST
import com.etologic.mahjongscoring2.app.screens.game.GameFragment.GamePages.STAY
import com.etologic.mahjongscoring2.app.screens.game.GamePageActions
import com.etologic.mahjongscoring2.app.screens.game.GameUiState
import com.etologic.mahjongscoring2.app.screens.game.GameViewModel
import com.etologic.mahjongscoring2.app.screens.game.game_list.GameListRvAdapter.GameListItemListener
import com.etologic.mahjongscoring2.app.utils.toStringSigned
import com.etologic.mahjongscoring2.business.model.entities.RoundId
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.model.entities.UiRound
import com.etologic.mahjongscoring2.databinding.GameListFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@AndroidEntryPoint
class GameListFragment : Fragment() {

    private var _binding: GameListFragmentBinding? = null
    private val binding get() = _binding!!
    private val gameViewModel: GameViewModel by hiltNavGraphViewModels(R.id.nav_graph_game)

    @Inject
    lateinit var rvAdapter: GameListRvAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = GameListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        startObservingTable()
    }

    private fun setupRecyclerView() {
        binding.rvGameList.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        binding.rvGameList.layoutManager = layoutManager
        binding.rvGameList.adapter = rvAdapter
        rvAdapter.setItemListener(object : GameListItemListener {
            override fun onClick(view: View, roundId: RoundId) {
                val popup = PopupMenu(context!!, view)
                popup.menuInflater.inflate(R.menu.game_list_item_menu, popup.menu)
                popup.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.game_list_item_menu_remove -> {
                            AlertDialog.Builder(activity, R.style.AlertDialogStyleMM)
                                .setTitle(R.string.delete_round)
                                .setMessage(R.string.are_you_sure)
                                .setPositiveButton(R.string.ok) { _, _ -> gameViewModel.removeRound(roundId) }
                                .setNegativeButton(R.string.close, null)
                                .create()
                                .show()
                        }
                    }
                    true
                }
                popup.show()
            }
        })
    }

    private fun startObservingTable() {
        viewLifecycleOwner.lifecycleScope.launch { repeatOnLifecycle(STARTED) { gameViewModel.gameUiStateFlow.collect(::uiStateObserver) } }
    }

    private fun uiStateObserver(uiState: GameUiState) {
        when (uiState) {
            is GameUiState.Loading -> {}
            is GameUiState.Error -> {}
            is GameUiState.Loaded -> {
                setGameData(uiState.game)
                pageToShowObserver(uiState.pageToShow)
            }
        }
    }

    private fun setGameData(game: UiGame) {
        setRoundsList(game.finishedRounds)
        setNames(game.playersNames)
        setFooter(game)
    }

    private fun setRoundsList(roundsList: List<UiRound>) {
        with(binding) {
            rvAdapter.updateCollection(roundsList)
            if (roundsList.isEmpty()) {
                rlGameListEmptyState.visibility = VISIBLE
            } else {
                if (rlGameListEmptyState.isVisible) {
                    rlGameListEmptyState.visibility = GONE
                }
                rvGameList.smoothScrollToPosition(roundsList.size - 1)
            }
        }
    }

    private fun setNames(names: Array<String>) {
        with(binding) {
            tvGameListHeaderNameP1.text = names[0]
            tvGameListHeaderNameP2.text = names[1]
            tvGameListHeaderNameP3.text = names[2]
            tvGameListHeaderNameP4.text = names[3]
        }
    }

    private fun setFooter(uiGame: UiGame) {
        with(binding) {
            if (uiGame.ongoingRound.areTherePenalties()) {
                tvGameListFooterTotalPenaltiesP1.text = uiGame.ongoingRound.penaltyP1.toStringSigned()
                tvGameListFooterTotalPenaltiesP2.text = uiGame.ongoingRound.penaltyP2.toStringSigned()
                tvGameListFooterTotalPenaltiesP3.text = uiGame.ongoingRound.penaltyP3.toStringSigned()
                tvGameListFooterTotalPenaltiesP4.text = uiGame.ongoingRound.penaltyP4.toStringSigned()
                llGameListFooterTotalPenalties.visibility = VISIBLE
            } else {
                llGameListFooterTotalPenalties.visibility = GONE
            }
            tvGameListFooterTotalPointsP1.text = uiGame.ongoingRound.totalPointsP1.toStringSigned()
            tvGameListFooterTotalPointsP2.text = uiGame.ongoingRound.totalPointsP2.toStringSigned()
            tvGameListFooterTotalPointsP3.text = uiGame.ongoingRound.totalPointsP3.toStringSigned()
            tvGameListFooterTotalPointsP4.text = uiGame.ongoingRound.totalPointsP4.toStringSigned()
        }
    }

    private fun pageToShowObserver(pageToShow: Pair<GameFragment.GamePages, GamePageActions>) {
        val (page, action) = pageToShow
        if (page == LIST && action == GamePageActions.HIGHLIGHT_LAST_ROUND) {
            lifecycleScope.launch {
                delay(300.milliseconds)
                val lastItemPosition = rvAdapter.itemCount.minus(1)
                if (lastItemPosition >= 0) {
                    val lastItem = binding.rvGameList.findViewHolderForAdapterPosition(lastItemPosition) as GameListRvAdapter.ItemViewHolder
                    lastItem.highlight()
                    gameViewModel.showPage(STAY)
                }
            }
        }
    }
}
