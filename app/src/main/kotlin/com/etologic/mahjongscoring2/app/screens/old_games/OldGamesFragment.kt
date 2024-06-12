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
package com.etologic.mahjongscoring2.app.screens.old_games

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.base.BaseMainFragment
import com.etologic.mahjongscoring2.app.screens.MainActivity
import com.etologic.mahjongscoring2.app.screens.MainViewModel.MainScreens.GAME
import com.etologic.mahjongscoring2.app.screens.goToPickFile
import com.etologic.mahjongscoring2.app.screens.openSetupNewGameDialog
import com.etologic.mahjongscoring2.app.utils.goToChooseLanguage
import com.etologic.mahjongscoring2.app.utils.setOnSecureClickListener
import com.etologic.mahjongscoring2.app.utils.showShareGameDialog
import com.etologic.mahjongscoring2.business.model.entities.GameId
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.databinding.OldGamesFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OldGamesFragment : BaseMainFragment() {

    companion object {
        const val TAG = "OldGamesFragment"
    }

    private var changeLanguageItem: MenuItem? = null
    private var enableCalcsItem: MenuItem? = null
    private var disableCalcsItem: MenuItem? = null

    @Inject
    lateinit var rvAdapter: OldGamesRvAdapter

    private var _binding: OldGamesFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OldGamesViewModel by viewModels()

    override val fragmentToolbar: Toolbar get() = binding.toolbarOldGames


    override val menuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.old_games_menu, menu)

            changeLanguageItem = menu.findItem(R.id.action_change_language)
            enableCalcsItem = menu.findItem(R.id.action_enable_diffs_calcs)
            disableCalcsItem = menu.findItem(R.id.action_disable_diffs_calcs)

            toggleDiffsEnabling(activityViewModel.isDiffsCalcsFeatureEnabledFlow.value)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            with(activity as? MainActivity) {
                when (menuItem.itemId) {
                    android.R.id.home -> this?.openDrawer()
                    R.id.action_change_language -> this?.goToChooseLanguage(languageHelper.currentLanguage) {
                        languageHelper.changeLanguage(
                            it,
                            this
                        )
                    }

                    R.id.action_enable_diffs_calcs -> activityViewModel.toggleDiffsFeature(true)
                    R.id.action_disable_diffs_calcs -> activityViewModel.toggleDiffsFeature(false)
                    R.id.action_export_games -> lifecycleScope.launch { activityViewModel.exportGames { this@with?.getExternalFilesDir(null) } }
                    R.id.action_import_games -> this?.goToPickFile()
                    else -> return false
                }
                return true
            }
        }
    }

    private fun toggleDiffsEnabling(shouldShowDiffs: Boolean) {
        if (shouldShowDiffs) {
            enableCalcsItem?.isVisible = false
            disableCalcsItem?.isVisible = true
        } else {
            enableCalcsItem?.isVisible = true
            disableCalcsItem?.isVisible = false
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = OldGamesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setToolbar()
        setOnClickListeners()
        startObservingViewModel()
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

            override fun onOldGameItemShareClicked(gameId: GameId) {
                requireContext().showShareGameDialog { shareGameOption ->
                    activityViewModel.shareGame(
                        gameId = gameId,
                        option = shareGameOption,
                        getExternalFilesDir = { requireContext().getExternalFilesDir(null) },
                    )
                }
            }

            override fun onOldGameItemResumeClicked(gameId: GameId) {
                activityViewModel.activeGameId = gameId
                activityViewModel.navigateTo(GAME)
            }
        })
        binding.rvOldGames.adapter = rvAdapter
    }

    private fun startObservingViewModel() {
        with(viewLifecycleOwner.lifecycleScope) {
            launch { repeatOnLifecycle(STARTED) { viewModel.gamesState.collect(::gamesObserver) } }
            launch { repeatOnLifecycle(STARTED) { activityViewModel.isDiffsCalcsFeatureEnabledFlow.collect(::toggleDiffsEnabling) } }
        }
    }

    private fun gamesObserver(games: List<UiGame>) {
        binding.emptyLayoutOldGames.visibility = if (games.isEmpty()) VISIBLE else GONE
        rvAdapter.setGames(games)
    }

    private fun setToolbar() {
        activityViewModel.setToolbar(binding.toolbarOldGames)
    }

    private fun setOnClickListeners() {
        binding.fabOldGames.setOnSecureClickListener {
            requireActivity().openSetupNewGameDialog()
        }
    }
}
