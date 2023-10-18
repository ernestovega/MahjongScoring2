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
package com.etologic.mahjongscoring2.app.game.activity

import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
import com.etologic.mahjongscoring2.business.model.entities.Table
import com.etologic.mahjongscoring2.business.model.entities.Table.Companion.MAX_MCR_ROUNDS
import com.etologic.mahjongscoring2.databinding.GameActivityBinding
import java.lang.System.currentTimeMillis
import javax.inject.Inject

class GameActivity : BaseActivity() {

    companion object {
        internal const val TAG = "GameActivity"
        internal const val CODE = 3
        private const val OFFSCREEN_PAGE_LIMIT = 1
        internal const val LAST_BACKPRESSED_MIN_TIME: Long = 2000
    }

    private var shouldBeShownResumeButton: Boolean = false
    private var shouldBeShownEndButton: Boolean = false
    private var endGameItem: MenuItem? = null
    private var resumeGameItem: MenuItem? = null

    @Inject
    internal lateinit var viewModelFactory: GameViewModelFactory
    internal lateinit var viewModel: GameViewModel
    private var lastBackPress: Long = 0

    private lateinit var binding: GameActivityBinding

    override fun onBackPressed() {
        if (binding.viewPagerGame.currentItem == LIST.code)
            viewModel.showPage(TABLE)
        else {
            val currentTimeMillis = currentTimeMillis()
            if (currentTimeMillis - lastBackPress > LAST_BACKPRESSED_MIN_TIME) {
                showSnackbar(binding.viewPagerGame, getString(R.string.press_again_to_exit))
                lastBackPress = currentTimeMillis
            } else
                viewModel.navigateTo(EXIT)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.game_menu, menu)
        super.onCreateOptionsMenu(menu)

        resumeGameItem = menu.findItem(R.id.action_resume_game)
        endGameItem = menu.findItem(R.id.action_end_game)

        resumeGameItem?.isVisible = shouldBeShownResumeButton
        endGameItem?.isVisible = shouldBeShownEndButton

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (binding.viewPagerGame.currentItem == TABLE.code)
                    viewModel.navigateTo(EXIT)
                else
                    onBackPressed()
                true
            }

            R.id.action_ratate_seats -> {
                if (binding.viewPagerGame.currentItem == LIST.code) viewModel.showPage(TABLE)
                viewModel.toggleSeatsRotation()
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

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        setTheme(R.style.AppTheme)
        binding = GameActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initViewModel()
        setupToolbar()
        setupViewPager()
        observeViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this, viewModelFactory).get(GameViewModel::class.java)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarGame)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp)
        binding.toolbarGame.title = getString(R.string.game)
    }

    private fun observeViewModel() {
        viewModel.getError().observe(this, Observer(this::showError))
        viewModel.getSnackbarMessage().observe(this) { showSnackbar(binding.viewPagerGame, it) }
        viewModel.getCurrentScreen().observe(this) { GameNavigator.showDialog(it, this) }
        viewModel.getCurrentPage().observe(this) { binding.viewPagerGame.currentItem = it.code }
        viewModel.getCurrentTable().observe(this) { currentRoundObserver(it) }
    }

    private fun currentRoundObserver(currentTable: Table) {
        val currentRound = currentTable.rounds.last()

        shouldBeShownResumeButton =
            if (currentRound.isEnded) currentTable.rounds.size < MAX_MCR_ROUNDS else false
        shouldBeShownEndButton = !currentRound.isEnded && currentTable.rounds.size > 1

        resumeGameItem?.isVisible = shouldBeShownResumeButton
        endGameItem?.isVisible = shouldBeShownEndButton

        if (currentRound.isEnded) viewModel.navigateTo(RANKING)
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
