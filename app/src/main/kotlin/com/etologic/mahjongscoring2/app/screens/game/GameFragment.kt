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
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.base.BaseMainFragment
import com.etologic.mahjongscoring2.app.base.ViewPagerAdapter
import com.etologic.mahjongscoring2.app.screens.MainActivity
import com.etologic.mahjongscoring2.app.screens.game.game_table.GameTableFragment
import com.etologic.mahjongscoring2.app.utils.shareFiles
import com.etologic.mahjongscoring2.app.utils.shareText
import com.etologic.mahjongscoring2.app.utils.showShareGameDialog
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.model.entities.UiGame.Companion.NOT_SET_GAME_ID
import com.etologic.mahjongscoring2.business.model.enums.SeatOrientation
import com.etologic.mahjongscoring2.databinding.GameFragmentBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GameFragment : BaseMainFragment() {

    companion object {
        const val TAG = "GameFragment"
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

    private val gameViewModel by viewModels<GameViewModel>()

    override val fragmentToolbar: Toolbar get() = binding.toolbarGame

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = GameFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override val menuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.game_menu, menu)

            resumeGameItem = menu.findItem(R.id.action_resume_game)
            endGameItem = menu.findItem(R.id.action_end_game)
            enableCalcsItem = menu.findItem(R.id.action_enable_diffs_calcs)
            disableCalcsItem = menu.findItem(R.id.action_disable_diffs_calcs)
            seatsOrientationMenuItem = menu.findItem(R.id.action_rotate_seats)

            resumeGameItem?.isVisible = shouldBeShownResumeButton
            endGameItem?.isVisible = shouldBeShownEndButton

            lifecycleScope.launch {
                toggleDiffsEnabling(gameViewModel.isDiffsCalcsFeatureEnabledFlow.first())
            }
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            with(activity as? MainActivity) {
                when (menuItem.itemId) {
                    android.R.id.home -> this?.openDrawer()
                    R.id.action_rotate_seats -> {
                        if (binding.viewPagerGame.currentItem == GameTableFragment.GameTablePages.LIST.code) {
                            gameViewModel.showPage(GameTableFragment.GameTablePages.TABLE)
                        }
                        gameViewModel.toggleSeatsRotation()
                    }

                    R.id.action_end_game -> gameViewModel.endGame()
                    R.id.action_enable_diffs_calcs -> gameViewModel.toggleDiffsFeature(true)
                    R.id.action_disable_diffs_calcs -> gameViewModel.toggleDiffsFeature(false)
                    R.id.action_resume_game -> gameViewModel.resumeGame()
                    R.id.action_edit_names -> openEditNamesDialog()
                    R.id.action_share_game -> this@with?.showShareGameDialog { shareGameOption ->
                        gameViewModel.shareGame(
                            option = shareGameOption,
                            getExternalFilesDir = { requireActivity().getExternalFilesDir(null) },
                        )
                    }

                    else -> return false
                }
                return true
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        orientationDownDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_rotation_down)
        orientationOutDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_rotation_out)
        setupViewPager()
        startObservingViewModel()
    }

    override fun onResume() {
        super.onResume()
        gameViewModel.setGameId(activityViewModel.activeGameId)
    }

    private fun startObservingViewModel() {
        Log.d("GameFragment", "GameViewModel: ${gameViewModel.hashCode()} - parentFragment: ${parentFragment.hashCode()}")
        with(viewLifecycleOwner.lifecycleScope) {
            launch { repeatOnLifecycle(STARTED) { gameViewModel.gameFlow.collect(::gameObserver) } }
            launch { repeatOnLifecycle(STARTED) { gameViewModel.isDiffsCalcsFeatureEnabledFlow.collect(::toggleDiffsEnabling) } }
            launch { repeatOnLifecycle(STARTED) { gameViewModel.seatsOrientationFlow.collect { updateSeatsOrientationIcon(it) } } }
            launch { repeatOnLifecycle(STARTED) { gameViewModel.pageToShowFlow.collect { pageToShowObserver(it) } } }
            launch { repeatOnLifecycle(STARTED) { gameViewModel.exportedTextFlow.collect { it?.let { requireActivity().shareText(it) } } } }
            launch { repeatOnLifecycle(STARTED) { gameViewModel.exportedFilesFlow.collect { it?.let { requireActivity().shareFiles(it) } } } }
        }
    }

    private fun pageToShowObserver(it: Pair<GameTableFragment.GameTablePages, ShouldHighlightLastRound>?) {
        it?.first?.code?.let { pageIndex -> binding.viewPagerGame.currentItem = pageIndex }
    }

    private fun gameObserver(game: UiGame) {
        if (game.gameId != NOT_SET_GAME_ID) {
            val isGameEnded = game.isEnded

            shouldBeShownResumeButton = isGameEnded && game.uiRounds.size < UiGame.MAX_MCR_ROUNDS
            shouldBeShownEndButton = isGameEnded.not() && game.uiRounds.size > 1

            resumeGameItem?.isVisible = shouldBeShownResumeButton
            endGameItem?.isVisible = shouldBeShownEndButton

            if (isGameEnded) {
                openRankingDialog()
            }
            
            activityViewModel.setCurrentGameName(game.gameName)
        }
    }

    private fun updateSeatsOrientationIcon(seatOrientation: SeatOrientation) {
        seatsOrientationMenuItem?.icon = when (seatOrientation) {
            SeatOrientation.OUT -> orientationOutDrawable
            SeatOrientation.DOWN -> orientationDownDrawable
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

    private fun setupViewPager() {
        with(binding) {
            // ViewPagerAdapter
            viewPagerGame.offscreenPageLimit = 1
            viewPagerGame.adapter = ViewPagerAdapter(this@GameFragment)

            // TabLayout
            TabLayoutMediator(tabLayoutGame, viewPagerGame) { tab, position ->
                tab.text = getString(if (position == 0) R.string.table else R.string.list)
            }.attach()
        }
    }
}