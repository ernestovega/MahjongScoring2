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
package com.etologic.mahjongscoring2.app.main.old_games

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.R.string.player_one
import com.etologic.mahjongscoring2.app.base.BaseFragment
import com.etologic.mahjongscoring2.app.extensions.setOnSecureClickListener
import com.etologic.mahjongscoring2.app.main.activity.MainViewModel
import com.etologic.mahjongscoring2.app.main.activity.MainViewModel.MainScreens.GAME
import com.etologic.mahjongscoring2.data_source.model.GameId
import com.etologic.mahjongscoring2.business.model.entities.UIGame
import com.etologic.mahjongscoring2.databinding.MainOldgamesFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OldGamesFragment : BaseFragment() {

    companion object {
        const val TAG = "OldGamesFragment"
    }

    private val activityViewModel: MainViewModel by activityViewModels()
    private val viewModel: OldGamesViewModel by viewModels()
    @Inject
    lateinit var rvAdapter: OldGamesRvAdapter
    private lateinit var defaultNames: Array<String>

    override fun onResume() {
        super.onResume()
        viewModel.getAllGames()
    }

    private var _binding: MainOldgamesFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = MainOldgamesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        defaultNames =
            arrayOf(
                getString(player_one),
                getString(R.string.player_two),
                getString(R.string.player_three),
                getString(R.string.player_four)
            )
        setupRecyclerView()
        setupSwipeRefreshLayout()
        setToolbar()
        setOnClickListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.rvOldGames.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        binding.rvOldGames.layoutManager = layoutManager
        rvAdapter.setOldGameItemListener(object : OldGamesRvAdapter.GameItemListener {

            override fun onOldGameItemDeleteClicked(gameId: GameId) {
                AlertDialog.Builder(activity, R.style.AlertDialogStyleMM)
                    .setTitle(R.string.delete_game)
                    .setMessage(R.string.are_you_sure)
                    .setPositiveButton(R.string.delete) { _, _ -> viewModel.deleteGame(gameId) }
                    .setNegativeButton(R.string.cancel, null)
                    .create()
                    .show()
            }

            override fun onOldGameItemResumeClicked(gameId: GameId) {
                startGame(gameId)
            }
        })
        binding.rvOldGames.adapter = rvAdapter
    }

    private fun observeViewModel() {
        viewModel.getError().observe(viewLifecycleOwner) { showError(it) }
        viewModel.getGames().observe(viewLifecycleOwner) { gamesObserver(it) }
        viewModel.getCreatedGameId().observe(viewLifecycleOwner) { startGame(it) }
    }

    private fun gamesObserver(games: List<UIGame>) {
        if (games.isEmpty())
            binding.emptyLayoutOldGames.visibility = VISIBLE
        else {
            binding.emptyLayoutOldGames.visibility = GONE
            rvAdapter.setGames(games)
            scrollToLastActiveGame(games)
        }
    }

    private fun scrollToLastActiveGame(games: List<UIGame>) {
        if (activityViewModel.activeGameId != null) {
            val gameIndex = games.indexOfFirst { it.dbGame.gameId == viewModel.getCreatedGameId().value }
            if (gameIndex >= 0) {
                binding.rvOldGames.smoothScrollToPosition(
                    binding.rvOldGames.getChildLayoutPosition(
                        binding.rvOldGames.getChildAt(gameIndex)
                    )
                )
            }
        }
    }

    private fun startGame(gameId: GameId) {
        activityViewModel.activeGameId = gameId
        activityViewModel.navigateTo(GAME)
    }

    private fun setToolbar() {
        activityViewModel.setToolbar(binding.toolbarOldGames)
    }

    private fun setupSwipeRefreshLayout() {
        binding.swipeRefreshLayoutOldGames.setOnRefreshListener {
            viewModel.getAllGames()
            binding.swipeRefreshLayoutOldGames.isRefreshing = false
        }
    }

    private fun setOnClickListeners() {
        binding.fabOldGames.setOnSecureClickListener {
            viewModel.createGame(defaultNames)
        }
    }
}
