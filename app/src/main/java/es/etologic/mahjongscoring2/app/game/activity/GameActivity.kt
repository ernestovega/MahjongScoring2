package es.etologic.mahjongscoring2.app.game.activity

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import es.etologic.mahjongscoring2.R
import es.etologic.mahjongscoring2.app.base.BaseActivity
import es.etologic.mahjongscoring2.app.base.ViewPagerAdapter
import es.etologic.mahjongscoring2.app.game.dialogs.PenaltyDialogFragment
import es.etologic.mahjongscoring2.app.game.dialogs.PlayersDialogFragment
import es.etologic.mahjongscoring2.app.game.dialogs.PointsDialogFragment
import es.etologic.mahjongscoring2.app.game.dialogs.RollDiceDialogFragment
import es.etologic.mahjongscoring2.app.game.game_list.GameListFragment
import es.etologic.mahjongscoring2.app.game.game_table.GameTableFragment
import es.etologic.mahjongscoring2.app.main.combinations.CombinationsActivity
import es.etologic.mahjongscoring2.app.model.DialogType
import es.etologic.mahjongscoring2.app.model.EnablingState
import es.etologic.mahjongscoring2.app.model.GamePages
import es.etologic.mahjongscoring2.app.model.ToolbarState
import kotlinx.android.synthetic.main.game_activity.*
import javax.inject.Inject

class GameActivity : BaseActivity() {
    
    companion object {
        const val ARG_KEY_GAME_ID = "arg_key_game_id"
        private const val OFFSCREEN_PAGE_LIMIT = 1
    }
    
    @Inject internal lateinit var viewModelFactory: GameActivityViewModelFactory
    private lateinit var viewModel: GameActivityViewModel
    private var eastIconDrawable: Drawable? = null
    private var southIconDrawable: Drawable? = null
    private var westIconDrawable: Drawable? = null
    private var northIconDrawable: Drawable? = null
    
    override fun onBackPressed() {
        viewModel.onBackPressed()
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.game_menu, menu)
        super.onCreateOptionsMenu(menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.action_combinations -> {
                startActivity(Intent(this, CombinationsActivity::class.java))
                return true
            }
            R.id.action_players -> {
                openPlayersDialogFragment()
                return true
            }
            //            case R.id.action_play:
            //            case R.id.action_pause:
            else -> return super.onOptionsItemSelected(item)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContentView(R.layout.game_activity)
        
        eastIconDrawable = ContextCompat.getDrawable(this, R.drawable.ic_east)
        southIconDrawable = ContextCompat.getDrawable(this, R.drawable.ic_south)
        westIconDrawable = ContextCompat.getDrawable(this, R.drawable.ic_west)
        northIconDrawable = ContextCompat.getDrawable(this, R.drawable.ic_north)
        
        setupToolbar()
        setupViewModel()
        setupViewPager()
        startGame()
    }
    
    private fun setupToolbar() {
        if (toolbarGame != null) {
            setSupportActionBar(toolbarGame)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp)
        }
    }
    
    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(GameActivityViewModel::class.java)
        viewModel.getError().observe(this, Observer<Throwable> { this.showError(it) })
        viewModel.getViewPagerPagingState().observe(this, Observer { it?.let { this.viewPagerPagingStateObserver(it) } })
        viewModel.getShowDialog().observe(this, Observer<DialogType> { it?.let { this.showDialogObserver(it) } })
        viewModel.getToolbarState().observe(this, Observer { it?.let { this.toolbarStateObserver(it) } })
        viewModel.getCurrentViewPagerPage().observe(this, Observer { it?.let { viewPagerGame.currentItem = it.code } })
        viewModel.getEndGameState().observe(this, Observer { finish() })
    }
    
    private fun viewPagerPagingStateObserver(enablingState: EnablingState) {
        viewPagerGame.isEnabled = enablingState == EnablingState.ENABLED
    }
    
    private fun showDialogObserver(dialogType: DialogType) {
        when (dialogType) {
            DialogType.PLAYERS_NAMES -> openPlayersDialogFragment()
            DialogType.ROLL_DICE -> openDialog(PlayersDialogFragment(), RollDiceDialogFragment.TAG)
            DialogType.REQUEST_HAND_POINTS -> openDialog(PointsDialogFragment(), PointsDialogFragment.TAG)
            DialogType.REQUEST_PENALTY_POINTS -> openDialog(PenaltyDialogFragment(), PenaltyDialogFragment.TAG)
            DialogType.EXIT -> showDialogExitGame()
            DialogType.NONE -> return
            DialogType.SHOW_RANKING -> return
        }
    }
    private fun openPlayersDialogFragment() {
        val playersDialogFragment = PlayersDialogFragment()
        playersDialogFragment.setNames(viewModel.listNames.value)
        openDialog(playersDialogFragment, PlayersDialogFragment.TAG)
    }
    private fun openDialog(dialogFragment: DialogFragment, tag: String) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val prev = supportFragmentManager.findFragmentByTag(tag)
        if (prev != null)
            fragmentTransaction.remove(prev)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
        dialogFragment.show(supportFragmentManager, tag)
    }
    private fun showDialogExitGame() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.exit_game)
            .setMessage(R.string.are_you_sure)
            .setPositiveButton(R.string.exit) { _, _ -> viewModel.endGame() }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
            .show()
    }
    
    private fun toolbarStateObserver(toolbarState: ToolbarState) {
        when (toolbarState) {
            ToolbarState.NORMAL -> toolbarGame.title = getString(R.string.game)
            ToolbarState.REQUEST_DISCARDER -> toolbarGame.title = getString(R.string.select_discarder)
        }
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
                viewModel.setCurrentViewPagerPage(GamePages.getFromCode(position))
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
    
    /*//    private void gameFinished(Boolean gameFinished) {
    //        if (gameFinished != null) {
    //            if (gameFinished) finish();
    //            else {
    //                Snackbar.make(viewPager, R.string.error_updating_end_date, Snackbar.LENGTH_LONG)
    //                        .setActionTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
    //                        .setAction(R.string.exit, v -> finish())
    //                        .show();
    //            }
    //        }
    //    }*/
    
    private fun startGame() {
        viewModel.setGameId(intent.getLongExtra(ARG_KEY_GAME_ID, -1))
        viewModel.initGame()
    }
}
