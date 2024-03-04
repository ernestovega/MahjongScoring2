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
package com.etologic.mahjongscoring2.app.game.activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.base.BaseActivity
import com.etologic.mahjongscoring2.app.base.ViewPagerAdapter
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel.GameScreens.COMBINATIONS
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel.GameScreens.EXIT
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel.GameScreens.PLAYERS
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel.GameScreens.RANKING
import com.etologic.mahjongscoring2.app.game.game_list.GameListFragment
import com.etologic.mahjongscoring2.app.game.game_table.GameTableFragment
import com.etologic.mahjongscoring2.app.game.game_table.GameTableFragment.GameTablePages.Companion.getFromCode
import com.etologic.mahjongscoring2.app.game.game_table.GameTableFragment.GameTablePages.LIST
import com.etologic.mahjongscoring2.app.game.game_table.GameTableFragment.GameTablePages.TABLE
import com.etologic.mahjongscoring2.business.model.entities.UIGame
import com.etologic.mahjongscoring2.business.model.entities.UIGame.Companion.MAX_MCR_ROUNDS
import com.etologic.mahjongscoring2.business.model.enums.SeatOrientation
import com.etologic.mahjongscoring2.business.model.enums.SeatOrientation.DOWN
import com.etologic.mahjongscoring2.business.model.enums.SeatOrientation.OUT
import com.etologic.mahjongscoring2.data_source.model.DBGame
import com.etologic.mahjongscoring2.data_source.model.GameId
import com.etologic.mahjongscoring2.databinding.GameActivityBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GameActivity : BaseActivity() {

    companion object {
        private const val OFFSCREEN_PAGE_LIMIT = 1
        const val KEY_GAME_ID = "game_id"
    }

    private var orientationDownDrawable: Drawable? = null
    private var orientationOutDrawable: Drawable? = null
    private var seatsOrientationMenuItem: MenuItem? = null
    private var shouldBeShownResumeButton: Boolean = false
    private var shouldBeShownEndButton: Boolean = false
    private var endGameItem: MenuItem? = null
    private var resumeGameItem: MenuItem? = null
    private var showCalcsItem: MenuItem? = null
    private var hideCalcsItem: MenuItem? = null

    private lateinit var binding: GameActivityBinding

    @Inject
    lateinit var gameViewModelFactory: GameViewModel.Factory
    private val viewModel by viewModels<GameViewModel> {
        GameViewModel.provideFactory(gameViewModelFactory, intent.extras?.getLong(KEY_GAME_ID, -1) as GameId)
    }

    override val onBackBehaviour = {
        if (binding.viewPagerGame.currentItem == LIST.code) {
            viewModel.showPage(TABLE)
        } else {
            viewModel.navigateTo(EXIT)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.game_menu, menu)
        super.onCreateOptionsMenu(menu)

        resumeGameItem = menu.findItem(R.id.action_resume_game)
        endGameItem = menu.findItem(R.id.action_end_game)
        showCalcsItem = menu.findItem(R.id.action_show_diffs_calcs)
        hideCalcsItem = menu.findItem(R.id.action_hide_diffs_calcs)

        resumeGameItem?.isVisible = shouldBeShownResumeButton
        endGameItem?.isVisible = shouldBeShownEndButton

        showCalcsItem?.isVisible = viewModel.shouldShowDiffs().value == false
        hideCalcsItem?.isVisible = viewModel.shouldShowDiffs().value != false

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackBehaviour.invoke()
                true
            }

            R.id.action_rotate_seats -> {
                if (binding.viewPagerGame.currentItem == LIST.code) viewModel.showPage(TABLE)
                viewModel.toggleSeatsRotation()
                seatsOrientationMenuItem = item
                true
            }

            R.id.action_combinations -> {
                viewModel.navigateTo(COMBINATIONS)
                true
            }

            R.id.action_end_game -> {
                viewModel.endGame()
                true
            }

            R.id.action_show_diffs_calcs -> {
                viewModel.showDiffs()
                true
            }

            R.id.action_hide_diffs_calcs -> {
                viewModel.hideDiffs()
                true
            }

            R.id.action_resume_game -> {
                viewModel.resumeGame()
                true
            }

            R.id.action_players -> {
                viewModel.navigateTo(PLAYERS)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        binding = GameActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        orientationDownDrawable = ContextCompat.getDrawable(applicationContext, R.drawable.ic_rotation_down)
        orientationOutDrawable = ContextCompat.getDrawable(applicationContext, R.drawable.ic_rotation_out)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setupToolbar()
        setupViewPager()
        observeViewModel()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarGame)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp)
        binding.toolbarGame.title = getString(R.string.game)
    }

    private fun observeViewModel() {
        viewModel.getError().observe(this) { showError(it) }
        viewModel.getCurrentScreen().observe(this) { GameNavigator.navigateTo(it, this, viewModel) }
        viewModel.getPageToShow()
            .observe(this) { it?.first?.code?.let { pageIndex -> binding.viewPagerGame.currentItem = pageIndex } }
        viewModel.getSeatsOrientation().observe(this) { updateSeatsOrientationIcon(it) }
        viewModel.shouldShowDiffs().observe(this) { toggleDiffs(it) }

        lifecycleScope.launch { viewModel.gameFlow.collect(::gameObserver) }
    }

    private fun gameObserver(uiGame: UIGame) {
        val isGameEnded = uiGame.dbGame.isEnded

        shouldBeShownResumeButton = isGameEnded && uiGame.rounds.size < MAX_MCR_ROUNDS
        shouldBeShownEndButton = isGameEnded.not() && uiGame.rounds.size > 1

        resumeGameItem?.isVisible = shouldBeShownResumeButton
        endGameItem?.isVisible = shouldBeShownEndButton

        if (isGameEnded) {
            viewModel.navigateTo(RANKING)
        }
    }

    private fun updateSeatsOrientationIcon(seatOrientation: SeatOrientation) {
        seatsOrientationMenuItem?.icon = when (seatOrientation) {
            OUT -> orientationOutDrawable
            DOWN -> orientationDownDrawable
        }
    }

    private fun toggleDiffs(shouldShowDiffs: Boolean) {
        if (shouldShowDiffs) {
            showCalcsItem?.isVisible = false
            hideCalcsItem?.isVisible = true
        } else {
            showCalcsItem?.isVisible = true
            hideCalcsItem?.isVisible = false
        }
    }

    private fun setupViewPager() {
        with(binding) {
            val vpAdapter = initAdapter()
            tabLayoutGame.setupWithViewPager(viewPagerGame)
            viewPagerGame.offscreenPageLimit = OFFSCREEN_PAGE_LIMIT
            viewPagerGame.adapter = vpAdapter
            viewPagerGame.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageScrollStateChanged(state: Int) {}
                override fun onPageSelected(position: Int) {
                    viewModel.showPage(getFromCode(position))
                    supportActionBar?.setHomeAsUpIndicator(if (position == 0) R.drawable.ic_clear_white_24dp else R.drawable.ic_arrow_back_white_24dp)
                }
            })
        }
    }

    private fun initAdapter(): ViewPagerAdapter {
        val gameTableFragment = GameTableFragment()
        val gameListFragment = GameListFragment()
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(gameTableFragment, getString(R.string.table))
        adapter.addFragment(gameListFragment, getString(R.string.list))
        return adapter
    }
}
