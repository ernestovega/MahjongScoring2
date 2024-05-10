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
package com.etologic.mahjongscoring2.app.game.game_list

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.base.BaseFragment
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel
import com.etologic.mahjongscoring2.app.game.activity.ShouldHighlightLastRound
import com.etologic.mahjongscoring2.app.game.game_list.GameListRvAdapter.GameListItemListener
import com.etologic.mahjongscoring2.app.game.game_table.GameTableFragment
import com.etologic.mahjongscoring2.app.game.game_table.GameTableFragment.GameTablePages.LIST
import com.etologic.mahjongscoring2.app.utils.toStringSigned
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.model.entities.UiRound
import com.etologic.mahjongscoring2.business.model.entities.RoundId
import com.etologic.mahjongscoring2.databinding.GameListFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GameListFragment : BaseFragment() {

    private var _binding: GameListFragmentBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var rvAdapter: GameListRvAdapter

    private val activityViewModel: GameViewModel by activityViewModels()

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
        initViewModel()
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
                                .setPositiveButton(R.string.ok) { _, _ -> activityViewModel.removeRound(roundId) }
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

    private fun initViewModel() {
        activityViewModel.getPageToShow().observe(viewLifecycleOwner) { pageToShowObserver(it) }

        viewLifecycleOwner.lifecycleScope.launch { repeatOnLifecycle(Lifecycle.State.STARTED) { activityViewModel.gameFlow.collect(::gameObserver) } }
    }

    private fun gameObserver(uiGame: UiGame) {
        setRoundsList(uiGame.getEndedRounds())
        setNames(uiGame.playersNames)
        setFooter(uiGame)
    }

    private fun setRoundsList(roundsList: List<UiRound>) {
        with(binding) {
            rvAdapter.updateCollection(roundsList)
            if (roundsList.isEmpty()) {
                rlGameListEmptyState.visibility = VISIBLE
            } else {
                if (rlGameListEmptyState.visibility == VISIBLE) {
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
            if (uiGame.currentRound.areTherePenalties()) {
                tvGameListFooterTotalPenaltiesP1.text = uiGame.currentRound.penaltyP1.toStringSigned()
                tvGameListFooterTotalPenaltiesP2.text = uiGame.currentRound.penaltyP2.toStringSigned()
                tvGameListFooterTotalPenaltiesP3.text = uiGame.currentRound.penaltyP3.toStringSigned()
                tvGameListFooterTotalPenaltiesP4.text = uiGame.currentRound.penaltyP4.toStringSigned()
                llGameListFooterTotalPenalties.visibility = VISIBLE
            } else {
                llGameListFooterTotalPenalties.visibility = GONE
            }
            tvGameListFooterTotalPointsP1.text = uiGame.currentRound.totalPointsP1.toStringSigned()
            tvGameListFooterTotalPointsP2.text = uiGame.currentRound.totalPointsP2.toStringSigned()
            tvGameListFooterTotalPointsP3.text = uiGame.currentRound.totalPointsP3.toStringSigned()
            tvGameListFooterTotalPointsP4.text = uiGame.currentRound.totalPointsP4.toStringSigned()
        }
    }

    private fun pageToShowObserver(pageToShow: Pair<GameTableFragment.GameTablePages, ShouldHighlightLastRound>?) {
        if (pageToShow != null) {
            val (pageIndex, shouldHighlightLastRound) = pageToShow
            if (pageIndex == LIST && shouldHighlightLastRound) {
                lifecycleScope.launch {
                    activityViewModel.showPage(null)
                    delay(300)
                    val lastItemPosition = rvAdapter.itemCount.minus(1)
                    if (lastItemPosition >= 0) {
                        val lastItem = binding.rvGameList.findViewHolderForAdapterPosition(lastItemPosition)
                                as GameListRvAdapter.ItemViewHolder
                        if (activityViewModel.lastHighlightedRound != lastItem.tvRoundNum.text) {
                            activityViewModel.lastHighlightedRound = lastItem.tvRoundNum.text
                            lastItem.highlight()
                        }
                    }
                }
            }
        }
    }
}
