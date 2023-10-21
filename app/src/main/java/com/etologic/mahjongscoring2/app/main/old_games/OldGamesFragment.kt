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
package com.etologic.mahjongscoring2.app.main.old_games

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.R.string.player_one
import com.etologic.mahjongscoring2.app.base.BaseFragment
import com.etologic.mahjongscoring2.app.extensions.setOnSecureClickListener
import com.etologic.mahjongscoring2.app.main.activity.MainActivityViewModelFactory
import com.etologic.mahjongscoring2.app.main.activity.MainViewModel
import com.etologic.mahjongscoring2.app.main.activity.MainViewModel.MainScreens.GAME
import com.etologic.mahjongscoring2.business.model.entities.Table
import com.etologic.mahjongscoring2.business.model.enums.GameStartType
import com.etologic.mahjongscoring2.business.model.enums.GameStartType.NEW
import com.etologic.mahjongscoring2.databinding.MainOldgamesFragmentBinding
import javax.inject.Inject

class OldGamesFragment : BaseFragment(), OldGamesRvAdapter.GameItemListener {

    companion object {
        const val TAG = "OldGamesFragment"
    }

    @Inject
    internal lateinit var mainActivityViewModelFactory: MainActivityViewModelFactory

    @Inject
    internal lateinit var oldGamesViewModelFactory: OldGamesViewModelFactory
    private lateinit var activityViewModel: MainViewModel
    private lateinit var viewModel: OldGamesViewModel

    @Inject
    internal lateinit var rvAdapter: OldGamesRvAdapter
    private lateinit var defaultNames: Array<String>

    //EVENTS
    override fun onOldGameItemDeleteClicked(gameId: Long) {
        AlertDialog.Builder(activity, R.style.AlertDialogStyleMM)
            .setTitle(R.string.delete_game)
            .setMessage(R.string.are_you_sure)
            .setPositiveButton(R.string.delete) { _, _ -> viewModel.deleteGame(gameId) }
            .setNegativeButton(R.string.cancel, null)
            .create()
            .show()
    }

    override fun onOldGameItemResumeClicked(gameId: Long) {
        viewModel.startGame(gameId)
    }

    //LIFECYCLE
    override fun onResume() {
        super.onResume()
        viewModel.getAllGames()
    }

    private var _binding: MainOldgamesFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainOldgamesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        defaultNames =
            arrayOf(getString(player_one), getString(R.string.player_two), getString(R.string.player_three), getString(R.string.player_four))
        setupRecyclerView()
        setupViewModel()
        setupSwipeRefreshLayout()
        setToolbar()
        setOnClickListeners()
    }

    private fun setupRecyclerView() {
        binding.rvOldGames.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        binding.rvOldGames.layoutManager = layoutManager
        rvAdapter.setOldGameItemListener(this)
        binding.rvOldGames.adapter = rvAdapter
    }

    private fun setupViewModel() {
        if (activity != null) {
            activityViewModel = ViewModelProvider(requireActivity(), mainActivityViewModelFactory)[MainViewModel::class.java]
            viewModel = ViewModelProvider(this, oldGamesViewModelFactory)[OldGamesViewModel::class.java]
            viewModel.getError().observe(viewLifecycleOwner) { showError(it) }
            viewModel.getSnackbarMessage().observe(viewLifecycleOwner) { showSnackbar(binding.root, it) }
            viewModel.getGames().observe(viewLifecycleOwner) { gamesObserver(it) }
            viewModel.getStartGame().observe(viewLifecycleOwner) { startGameObserver(it) }
        }
    }

    private fun gamesObserver(games: List<Table>) {
        if (games.isEmpty())
            binding.emptyLayoutOldGames.visibility = VISIBLE
        else {
            binding.emptyLayoutOldGames.visibility = GONE
            rvAdapter.setGames(games)
            if (activityViewModel.lastGameStartType == NEW) {
                activityViewModel.lastGameStartType = null
                binding.rvOldGames.smoothScrollToPosition(0)
            }
        }
    }

    private fun startGameObserver(gameStartType: GameStartType) {
        activityViewModel.lastGameStartType = gameStartType
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
            viewModel.startNewGame(defaultNames)
        }
    }
}
