package com.etologic.mahjongscoring2.app.game.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.base.BaseActivity
import com.etologic.mahjongscoring2.app.base.ViewPagerAdapter
import com.etologic.mahjongscoring2.app.game.activity.GameActivityViewModel.GameScreens.*
import com.etologic.mahjongscoring2.app.game.game_list.GameListFragment
import com.etologic.mahjongscoring2.app.game.game_table.GameTableFragment
import com.etologic.mahjongscoring2.app.game.game_table.GameTableFragment.GameTablePages.Companion.getFromCode
import com.etologic.mahjongscoring2.app.game.game_table.GameTableFragment.GameTablePages.LIST
import com.etologic.mahjongscoring2.app.game.game_table.GameTableFragment.GameTablePages.TABLE
import com.etologic.mahjongscoring2.business.model.entities.Table
import com.etologic.mahjongscoring2.business.model.entities.Table.Companion.MAX_MCR_ROUNDS
import kotlinx.android.synthetic.main.game_activity.*
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
    internal lateinit var viewModelFactory: GameActivityViewModelFactory
    internal lateinit var viewModel: GameActivityViewModel
    private var lastBackPress: Long = 0
    
    override fun onBackPressed() {
        if (viewPagerGame.currentItem == LIST.code)
            viewModel.showPage(TABLE)
        else {
            val currentTimeMillis = currentTimeMillis()
            if (currentTimeMillis - lastBackPress > LAST_BACKPRESSED_MIN_TIME) {
                showSnackbar(viewPagerGame, getString(R.string.press_again_to_exit))
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
                if (viewPagerGame.currentItem == TABLE.code)
                    viewModel.navigateTo(EXIT)
                else
                    onBackPressed()
                true
            }
            R.id.action_ratate_seats -> {
                if (viewPagerGame.currentItem == LIST.code) viewModel.showPage(TABLE)
                viewModel.changeSeatsRotation()
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
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_activity)
        initViewModel()
        setupToolbar()
        setupViewPager()
        observeViewModel()
    }
    
    private fun initViewModel() {
        viewModel = ViewModelProvider(this, viewModelFactory).get(GameActivityViewModel::class.java)
    }
    
    private fun setupToolbar() {
        if (toolbarGame != null) {
            setSupportActionBar(toolbarGame)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp)
            toolbarGame.title = getString(R.string.game)
        }
    }
    
    private fun observeViewModel() {
        viewModel.getError().observe(this, Observer(this::showError))
        viewModel.getProgressState().observe(this, Observer(this::toggleProgress))
        viewModel.getSnackbarMessage().observe(this, Observer { message -> viewPagerGame?.let { this.showSnackbar(it, message) } })
        viewModel.getCurrentScreen().observe(this, Observer { GameNavigator.showDialog(it, this) })
        viewModel.getCurrentPage().observe(this, Observer { it?.let { viewPagerGame.currentItem = it.code } })
        viewModel.getCurrentTable().observe(this, Observer(this::currentRoundObserver))
    }
    
    private fun currentRoundObserver(currentTable: Table) {
        val currentRound = currentTable.rounds.last()
        shouldBeShownResumeButton = if (currentRound.isEnded) currentTable.rounds.size < MAX_MCR_ROUNDS else false
        shouldBeShownEndButton = !currentRound.isEnded
        resumeGameItem?.isVisible = shouldBeShownResumeButton
        endGameItem?.isVisible = shouldBeShownEndButton
        if (currentRound.isEnded) viewModel.navigateTo(RANKING)
    }
    
    private fun setupViewPager() {
        val vpAdapter = initAdapter()
        tabLayoutGame.setupWithViewPager(viewPagerGame)
        viewPagerGame.offscreenPageLimit = OFFSCREEN_PAGE_LIMIT
        viewPagerGame.adapter = vpAdapter
        viewPagerGame.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageSelected(position: Int) {
                viewModel.showPage(getFromCode(position))
                supportActionBar?.setHomeAsUpIndicator(if (position == 0) R.drawable.ic_clear_white_24dp else R.drawable.ic_arrow_back_white_24dp)
            }
        })
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
