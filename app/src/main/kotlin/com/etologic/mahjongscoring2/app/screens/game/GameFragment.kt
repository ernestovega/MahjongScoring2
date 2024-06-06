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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager.widget.ViewPager
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.base.BaseFragment
import com.etologic.mahjongscoring2.app.base.ViewPagerAdapter
import com.etologic.mahjongscoring2.app.screens.MainActivity
import com.etologic.mahjongscoring2.app.screens.game.GameViewModel.Factory
import com.etologic.mahjongscoring2.app.screens.game.game_list.GameListFragment
import com.etologic.mahjongscoring2.app.screens.game.game_table.GameTableFragment
import com.etologic.mahjongscoring2.app.screens.openEditNamesDialog
import com.etologic.mahjongscoring2.app.screens.openRankingDialog
import com.etologic.mahjongscoring2.app.utils.shareFiles
import com.etologic.mahjongscoring2.app.utils.shareText
import com.etologic.mahjongscoring2.app.utils.showShareGameDialog
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.model.enums.SeatOrientation
import com.etologic.mahjongscoring2.databinding.GameFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GameFragment : BaseFragment() {

    companion object {
        const val TAG = "GameFragment"
        const val KEY_GAME_ID = "game_id"
        private const val OFFSCREEN_PAGE_LIMIT = 1
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

    @Inject
    lateinit var gameViewModelFactory: Factory
    private val viewModel by viewModels<GameViewModel> {
        GameViewModel.provideFactory(gameViewModelFactory, gameId = arguments?.getLong(KEY_GAME_ID) ?: -1)
    }

    private var _binding: GameFragmentBinding? = null
    private val binding get() = _binding!!

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

            toggleDiffsEnabling(viewModel.isDiffsCalcsFeatureEnabledFlow.value)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            with(activity as? MainActivity) {
                when (menuItem.itemId) {
                    android.R.id.home -> this?.openDrawer()
                    R.id.action_rotate_seats -> {
                        if (binding.viewPagerGame.currentItem == GameTableFragment.GameTablePages.LIST.code) {
                            viewModel.showPage(GameTableFragment.GameTablePages.TABLE)
                        }
                        viewModel.toggleSeatsRotation()
                    }

//            R.id.action_combinations -> viewModel.navigateTo(COMBINATIONS)
                    R.id.action_end_game -> viewModel.endGame()
                    R.id.action_enable_diffs_calcs -> viewModel.toggleDiffsFeature(true)
                    R.id.action_disable_diffs_calcs -> viewModel.toggleDiffsFeature(false)
                    R.id.action_resume_game -> viewModel.resumeGame()
                    R.id.action_edit_names -> this?.openEditNamesDialog()
                    R.id.action_share_game -> this?.showShareGameDialog { shareGameOption ->
                        viewModel.shareGame(
                            option = shareGameOption,
                            getExternalFilesDir = { activity?.getExternalFilesDir(null) },
                        )
                    }

                    else -> return false
                }
                return true
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        orientationDownDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_rotation_down)
        orientationOutDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_rotation_out)
        setupToolbar()
        setupViewPager()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbarGame.title = getString(R.string.game)
        activityViewModel.setToolbar(binding.toolbarGame)
    }

    private fun observeViewModel() {
        viewModel.getError().observe(viewLifecycleOwner) { showError(it) }
        viewModel.getPageToShow().observe(viewLifecycleOwner) { it?.first?.code?.let { pageIndex -> binding.viewPagerGame.currentItem = pageIndex } }
        viewModel.getSeatsOrientation().observe(viewLifecycleOwner) { updateSeatsOrientationIcon(it) }
        viewModel.getExportedText().observe(viewLifecycleOwner) { activity?.shareText(it) }
        viewModel.getExportedFiles().observe(viewLifecycleOwner) { activity?.shareFiles(it) }
        lifecycleScope.launch { repeatOnLifecycle(Lifecycle.State.STARTED) { viewModel.gameFlow.collect(::gameObserver) } }
        lifecycleScope.launch { repeatOnLifecycle(Lifecycle.State.STARTED) { viewModel.isDiffsCalcsFeatureEnabledFlow.collect(::toggleDiffsEnabling) } }
    }

    private fun gameObserver(uiGame: UiGame) {
        val isGameEnded = uiGame.isEnded

        shouldBeShownResumeButton = isGameEnded && uiGame.uiRounds.size < UiGame.MAX_MCR_ROUNDS
        shouldBeShownEndButton = isGameEnded.not() && uiGame.uiRounds.size > 1

        resumeGameItem?.isVisible = shouldBeShownResumeButton
        endGameItem?.isVisible = shouldBeShownEndButton

        if (isGameEnded) {
            activity?.openRankingDialog()
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
            val vpAdapter = initAdapter()
            tabLayoutGame.setupWithViewPager(viewPagerGame)
            viewPagerGame.offscreenPageLimit = OFFSCREEN_PAGE_LIMIT
            viewPagerGame.adapter = vpAdapter
            viewPagerGame.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
                override fun onPageScrollStateChanged(state: Int) {}
                override fun onPageSelected(position: Int) {
                    viewModel.showPage(GameTableFragment.GameTablePages.getFromCode(position))
                }
            })
        }
    }

    private fun initAdapter(): ViewPagerAdapter? {
        val gameTableFragment = GameTableFragment()
        val gameListFragment = GameListFragment()
        val adapter = activity?.supportFragmentManager?.let { ViewPagerAdapter(it) }
        adapter?.addFragment(gameTableFragment, getString(R.string.table))
        adapter?.addFragment(gameListFragment, getString(R.string.list))
        return adapter
    }
}