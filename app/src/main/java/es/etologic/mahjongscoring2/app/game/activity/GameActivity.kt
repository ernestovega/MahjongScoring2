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
import androidx.lifecycle.ViewModelProvider
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
import es.etologic.mahjongscoring2.app.model.DialogType.*
import es.etologic.mahjongscoring2.app.model.GamePages
import kotlinx.android.synthetic.main.game_activity.*
import javax.inject.Inject

class GameActivity : BaseActivity() {
    
    companion object {
        internal const val TAG = "GameActivity"
        internal const val CODE = 3
        private const val OFFSCREEN_PAGE_LIMIT = 1
    }
    
    private var eastIconDrawable: Drawable? = null
    private var southIconDrawable: Drawable? = null
    private var westIconDrawable: Drawable? = null
    private var northIconDrawable: Drawable? = null
    
    @Inject
    internal lateinit var viewModelFactory: GameActivityViewModelFactory
    private lateinit var viewModel: GameActivityViewModel
    
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
                goToActivity(Intent(this, CombinationsActivity::class.java), CombinationsActivity.CODE)
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
        viewModel.getCurrentViewPagerPage().observe(this, Observer { it?.let { viewPagerGame.currentItem = it.code } })
        viewModel.getEndGameState().observe(this, Observer { finish() })
        viewModel.getShowDialog().observe(this, Observer(this::showDialogObserver))
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
    
    private fun showDialogObserver(dialogType: DialogType) {
        when (dialogType) {
            NAMES -> openPlayersDialogFragment()
            DICE -> openDialog(PlayersDialogFragment(), RollDiceDialogFragment.TAG)
            HU -> openDialog(PointsDialogFragment(), PointsDialogFragment.TAG)
            PENALTY -> openDialog(PenaltyDialogFragment(), PenaltyDialogFragment.TAG)
            EXIT -> showDialogExitGame()
            RANKING -> return
            else -> return
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
}
