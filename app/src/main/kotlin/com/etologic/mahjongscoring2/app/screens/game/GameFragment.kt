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

package com.etologic.mahjongscoring2.app.screens.game

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.base.BaseMainFragment
import com.etologic.mahjongscoring2.app.screens.MainActivity
import com.etologic.mahjongscoring2.app.screens.game.GameFragment.GamePages.STAY
import com.etologic.mahjongscoring2.app.utils.shareFiles
import com.etologic.mahjongscoring2.app.utils.shareText
import com.etologic.mahjongscoring2.app.utils.showShareGameDialog
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.model.entities.UiGame.Companion.NOT_SET_GAME_ID
import com.etologic.mahjongscoring2.business.model.enums.SeatsOrientation
import com.etologic.mahjongscoring2.databinding.GameFragmentBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GameFragment : BaseMainFragment() {

    companion object {
        const val TAG = "GameFragment"
    }

    enum class GamePages(val index: Int) {
        STAY(-1),
        TABLE(0),
        LIST(1);
    }

    private var orientationDownDrawable: Drawable? = null
    private var orientationOutDrawable: Drawable? = null
    private var seatsOrientationMenuItem: MenuItem? = null
    private var shouldBeShownResumeButton: Boolean = false
    private var shouldBeShownEndButton: Boolean = false
    private var endGameItem: MenuItem? = null
    private var resumeGameItem: MenuItem? = null
    private var enableCalcsItem: MenuItem? = null
    private var disableCalcsItem: MenuItem? = null

    private var _binding: GameFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<GameViewModel>()

    override val onBackOrUpClick: () -> Unit = {
        if (binding.viewPagerGame.currentItem == GamePages.LIST.index) {
            binding.viewPagerGame.setCurrentItem(GamePages.TABLE.index, true)
        } else {
            findNavController().navigateUp()
        }
    }

    override val toolbarMenuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.game_menu, menu)

            resumeGameItem = menu.findItem(R.id.action_resume_game)
            endGameItem = menu.findItem(R.id.action_end_game)
            enableCalcsItem = menu.findItem(R.id.action_enable_diffs_calcs)
            disableCalcsItem = menu.findItem(R.id.action_disable_diffs_calcs)
            seatsOrientationMenuItem = menu.findItem(R.id.action_seats_orientation)

            resumeGameItem?.isVisible = shouldBeShownResumeButton
            endGameItem?.isVisible = shouldBeShownEndButton

            lifecycleScope.launch {
                toggleDiffsEnabling((viewModel.gameUiStateFlow.value as? GameUiState.Loaded)?.isDiffsCalcsFeatureEnabled == true)
            }
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            with(activity as? MainActivity) {
                when (menuItem.itemId) {
                    android.R.id.home -> onBackOrUpClick.invoke()
                    R.id.action_seats_orientation -> {
                        if (binding.viewPagerGame.currentItem == GamePages.LIST.index) {
                            viewModel.showPage(GamePages.TABLE)
                        }
                        viewModel.toggleSeatsOrientation()
                    }

                    R.id.action_combinations -> findNavController().navigate(R.id.action_gameFragment_to_combinationsFragment)
                    R.id.action_end_game -> viewModel.endGame()
                    R.id.action_enable_diffs_calcs -> viewModel.showDiffCalcsFeature()
                    R.id.action_disable_diffs_calcs -> viewModel.hideDiffCalcsFeature()
                    R.id.action_resume_game -> viewModel.resumeGame()
                    R.id.action_edit_names -> findNavController().navigate(R.id.action_gameFragment_to_editNamesDialogFragment)
                    R.id.action_share_game -> this@with?.showShareGameDialog { shareGameOption ->
                        with(requireActivity()) {
                            viewModel.shareGame(
                                option = shareGameOption,
                                directory = getExternalFilesDir(null),
                                showShareText = { text -> shareText(text) },
                                showShareFiles = { files -> shareFiles(files) },
                            )
                        }
                    }

                    else -> return false
                }
                return true
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = GameFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        orientationDownDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_orientation_down)
        orientationOutDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_orientation_out)
        setupViewPager()
        startObservingViewModel()
    }

    private fun startObservingViewModel() {
        viewLifecycleOwner.lifecycleScope.launch { repeatOnLifecycle(STARTED) { viewModel.gameUiStateFlow.collect(::uiStateObserver) } }
    }

    private fun uiStateObserver(uiState: GameUiState) {
        when (uiState) {
            is GameUiState.Loading -> {}
            is GameUiState.Error -> showErrorDialog(uiState.throwable)
            is GameUiState.Loaded -> {
                setGameData(uiState.game)
                toggleDiffsEnabling(uiState.isDiffsCalcsFeatureEnabled)
                updateSeatsOrientationIcon(uiState.seatsOrientation)
                pageToShowObserver(uiState.pageToShow)
            }
        }
    }

    private fun setGameData(game: UiGame) {
        if (game.gameId != NOT_SET_GAME_ID) {
            val isGameEnded = game.isEnded

            shouldBeShownResumeButton = isGameEnded && game.uiRounds.size < UiGame.MAX_MCR_ROUNDS
            shouldBeShownEndButton = isGameEnded.not() && game.uiRounds.size > 1

            resumeGameItem?.isVisible = shouldBeShownResumeButton
            endGameItem?.isVisible = shouldBeShownEndButton

            if (isGameEnded) {
                findNavController().navigate(R.id.action_gameFragment_to_rankingDialogFragment)
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

    private fun updateSeatsOrientationIcon(seatsOrientation: SeatsOrientation) {
        seatsOrientationMenuItem?.icon = when (seatsOrientation) {
            SeatsOrientation.OUT -> orientationOutDrawable
            SeatsOrientation.DOWN -> orientationDownDrawable
        }
    }

    private fun pageToShowObserver(pageToShow: Pair<GamePages, ShouldHighlightLastRound>?) {
        pageToShow?.first?.let { gamePage ->
            with(binding.viewPagerGame) {
                if (gamePage != STAY && gamePage.index != currentItem) {
                    currentItem = gamePage.index
                }
            }
        }
    }

    private fun setupViewPager() {
        with(binding) {
            // ViewPagerAdapter
            viewPagerGame.offscreenPageLimit = 1
            viewPagerGame.adapter = GameViewPagerAdapter(this@GameFragment)

            // TabLayout
            TabLayoutMediator(tabLayoutGame, viewPagerGame) { tab, position ->
                tab.text = getString(if (position == 0) R.string.table else R.string.list)
            }.attach()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.setGameId(activityViewModel.activeGameId)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}