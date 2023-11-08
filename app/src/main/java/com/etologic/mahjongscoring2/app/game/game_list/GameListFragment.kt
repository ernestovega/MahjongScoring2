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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.game.activity.ShouldHighlightLastRound
import com.etologic.mahjongscoring2.app.game.base.BaseGameFragment
import com.etologic.mahjongscoring2.app.game.game_list.GameListRvAdapter.GameListItemListener
import com.etologic.mahjongscoring2.app.game.game_table.GameTableFragment
import com.etologic.mahjongscoring2.app.game.game_table.GameTableFragment.GameTablePages.LIST
import com.etologic.mahjongscoring2.business.model.entities.Round
import com.etologic.mahjongscoring2.business.model.entities.Table
import com.etologic.mahjongscoring2.databinding.GameListFragmentBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class GameListFragment : BaseGameFragment() {

    @Inject
    internal lateinit var rvAdapter: GameListRvAdapter

    private var _binding: GameListFragmentBinding? = null
    private val binding get() = _binding!!

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
            override fun onClick(view: View, roundId: Int) {
                val popup = PopupMenu(context!!, view)
                popup.menuInflater.inflate(R.menu.game_list_item_menu, popup.menu)
                popup.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.game_list_item_menu_remove -> {
                            AlertDialog.Builder(activity, R.style.AlertDialogStyleMM)
                                .setTitle(R.string.delete_round)
                                .setMessage(R.string.are_you_sure)
                                .setPositiveButton(R.string.ok) { _, _ ->
                                    activityViewModel?.removeRound(
                                        roundId
                                    )
                                }
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
        activityViewModel?.getCurrentTable()?.observe(viewLifecycleOwner) { tableObserver(it) }
        activityViewModel?.getPageToShow()?.observe(viewLifecycleOwner) { pageToShowObserver(it) }
    }

    private fun tableObserver(table: Table) {
        roundsListObserver(table.getEndedRounds())
        namesObserver(table.game.getPlayersNames())
        totalsObserver(table.getPlayersTotalPointsStringSigned())
    }

    private fun roundsListObserver(roundsList: List<Round>) {
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

    private fun namesObserver(names: Array<String>) {
        with(binding) {
            tvGameListHeaderNameP1.text = names[0]
            tvGameListHeaderNameP2.text = names[1]
            tvGameListHeaderNameP3.text = names[2]
            tvGameListHeaderNameP4.text = names[3]
        }
    }

    private fun totalsObserver(totals: Array<String>) {
        with(binding) {
            tvGameListFooterTotalPointsP1.text = totals[0]
            tvGameListFooterTotalPointsP2.text = totals[1]
            tvGameListFooterTotalPointsP3.text = totals[2]
            tvGameListFooterTotalPointsP4.text = totals[3]
        }
    }

    private fun pageToShowObserver(pageToShow: Pair<GameTableFragment.GameTablePages, ShouldHighlightLastRound>?) {
        if (pageToShow != null) {
            val (pageIndex, shouldHighlightLastRound) = pageToShow
            if (pageIndex == LIST && shouldHighlightLastRound) {
                lifecycleScope.launch {
                    activityViewModel?.showPage(null)
                    delay(300)
                    val lastItemPosition = rvAdapter.itemCount.minus(1)
                    if (lastItemPosition >= 0) {
                        val lastItem = binding.rvGameList.findViewHolderForAdapterPosition(lastItemPosition) as GameListRvAdapter.ItemViewHolder
                        if (activityViewModel?.lastHighlightedRound != lastItem.tvRoundNum.text) {
                            activityViewModel?.lastHighlightedRound = lastItem.tvRoundNum.text
                            lastItem.highlight()
                        }
                    }
                }
            }
        }
    }
}
