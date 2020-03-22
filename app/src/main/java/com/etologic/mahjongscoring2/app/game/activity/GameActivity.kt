package com.etologic.mahjongscoring2.app.game.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.base.BaseActivity
import com.etologic.mahjongscoring2.app.base.ViewPagerAdapter
import com.etologic.mahjongscoring2.app.game.game_list.GameListFragment
import com.etologic.mahjongscoring2.app.game.game_table.GameTableFragment
import com.etologic.mahjongscoring2.app.main.combinations.CombinationsActivity
import com.etologic.mahjongscoring2.app.model.DialogType.PLAYERS
import com.etologic.mahjongscoring2.app.model.GamePages
import kotlinx.android.synthetic.main.game_activity.*
import javax.inject.Inject

class GameActivity : BaseActivity() {
    
    companion object {
        internal const val TAG = "GameActivity"
        internal const val CODE = 3
        private const val OFFSCREEN_PAGE_LIMIT = 1
    }
    
    @Inject
    internal lateinit var viewModelFactory: GameActivityViewModelFactory
    internal lateinit var viewModel: GameActivityViewModel
    
    override fun onBackPressed() {
        viewModel.onBackPressed()
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.game_menu, menu)
        super.onCreateOptionsMenu(menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_combinations -> {
                goToActivity(Intent(this, CombinationsActivity::class.java), CombinationsActivity.CODE)
                true
            }
            R.id.action_players -> {
                viewModel.showDialog(PLAYERS)
                true
            }
            else -> super.onOptionsItemSelected(item)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_activity)
        setupToolbar()
        setupViewModel()
        setupViewPager()
    }
    
    private fun setupToolbar() {
        if (toolbarGame != null) {
            setSupportActionBar(toolbarGame)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp)
            toolbarGame.title = getString(R.string.game)
        }
    }
    
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, viewModelFactory).get(GameActivityViewModel::class.java)
        viewModel.getError().observe(this, Observer(this::showError))
        viewModel.getProgressState().observe(this, Observer(this::toggleProgress))
        viewModel.getSnackbarMessage().observe(this, Observer { this.showSnackbar(llProgress, it) })
        viewModel.getDialogToShow().observe(this, Observer { GameNavigator.showDialog(it, this) })
        viewModel.getCurrentPage().observe(this, Observer { it?.let { viewPagerGame.currentItem = it.code } })
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
                viewModel.showPage(GamePages.getFromCode(position))
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
