package es.etologic.mahjongscoring2.app.main.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat.START
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import es.etologic.mahjongscoring2.BuildConfig
import es.etologic.mahjongscoring2.R
import es.etologic.mahjongscoring2.app.base.BaseActivity
import es.etologic.mahjongscoring2.app.game.activity.GameActivity
import es.etologic.mahjongscoring2.app.main.activity.MainActivityViewModel.MainScreens
import es.etologic.mahjongscoring2.app.main.activity.MainActivityViewModel.MainScreens.*
import es.etologic.mahjongscoring2.app.main.combinations.CombinationsActivity
import es.etologic.mahjongscoring2.app.main.old_games.OldGamesFragment
import kotlinx.android.synthetic.main.main_activity.*
import javax.inject.Inject

class MainActivity : BaseActivity() {
    
    companion object {
        internal const val TAG = "MainActivity"
        internal const val CODE = 1
        private const val GREEN_BOOK_URL = "https://docs.google.com/gview?embedded=true&url=mahjong-europe.org/portal/images/docs/mcr_EN.pdf"
        private const val MARKET_URI_BASE = "market://details?id="
        private const val PLAY_STORE_URL_BASE = "http://play.google.com/store/apps/details?id="
        private const val EMAIL_SUBJECT = "Mahjong Scoring 2"
        private const val EMAIL_ADDRESS = "mahjongmadrid@gmail.com"
        private const val LAST_BACKPRESSED_MIN_TIME: Long = 2000
    }
    
    @Inject internal lateinit var viewModelFactory: MainActivityViewModelFactory
    private var viewModel: MainActivityViewModel? = null
    private var lastBackPress: Long = 0
    
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
    }
    
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        setupViewModel()
        setupDrawer()
        viewModel?.navigateTo(OLD_GAMES)
    }
    
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainActivityViewModel::class.java)
        viewModel?.getProgressState()?.observe(this, Observer(this::toggleProgress))
        viewModel?.getCurrentScreen()?.observe(this, Observer(this::goToScreen))
        viewModel?.getCurrentToolbar()?.observe(this, Observer(this::setToolbar))
    }
    
    private fun goToScreen(screen: MainScreens) {
        when (screen) {
            OLD_GAMES -> goToOldGames()
            GAME -> goToGame()
            COMBINATIONS -> goToCombinations()
            GREEN_BOOK -> goToGreenBook()
            RATE -> goToRate()
            CONTACT -> goToContact()
            FINISH -> onBackPressed()
        }
    }
    
    private fun goToOldGames() {
        val oldGamesFragment = OldGamesFragment()
        goToFragment(oldGamesFragment)
    }
    
    private fun goToGame() {
        val intent = Intent(this, GameActivity::class.java)
        goToActivity(intent, GameActivity.CODE)
    }
    
    private fun goToFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left)
        fragmentTransaction.add(R.id.frameLayoutMain, fragment).addToBackStack(null)
        fragmentTransaction.commit()
    }
    
    private fun goToCombinations() {
        val intent = Intent(this, CombinationsActivity::class.java)
        goToActivity(intent, CombinationsActivity.CODE)
    }
    
    private fun goToGreenBook() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(GREEN_BOOK_URL))
        startActivity(intent)
    }
    
    private fun goToRate() {
        val uriMarket = Uri.parse(MARKET_URI_BASE + BuildConfig.APPLICATION_ID)
        var intent = Intent(Intent.ACTION_VIEW, uriMarket)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        else {
            @Suppress("DEPRECATION")
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        }
        try {
            this.startActivity(intent)
        } catch (e: Exception) {
            val uriPlayStore = Uri.parse(PLAY_STORE_URL_BASE + BuildConfig.APPLICATION_ID)
            intent = Intent(Intent.ACTION_VIEW, uriPlayStore)
            startActivity(intent)
        }
        
    }
    
    private fun goToContact() {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(EMAIL_ADDRESS))
        intent.putExtra(Intent.EXTRA_SUBJECT, EMAIL_SUBJECT)
        if (intent.resolveActivity(this.packageManager) != null)
            try {
                this.startActivity(intent)
            } catch (e: Exception) {
                Snackbar.make(drawerLayoutMain, R.string.no_email_apps_founded, Snackbar.LENGTH_LONG).show()
            }
    }
    
    override fun onBackPressed() {
        if (drawerLayoutMain?.isDrawerOpen(START) == true)
            closeDrawer()
        else if (supportFragmentManager.backStackEntryCount > 1)
            super.onBackPressed()
        else {
            val currentTimeMillis = System.currentTimeMillis()
            if (currentTimeMillis - lastBackPress > LAST_BACKPRESSED_MIN_TIME) {
                Snackbar.make(navigationViewMain, R.string.press_again_to_exit, Snackbar.LENGTH_LONG).show()
                lastBackPress = currentTimeMillis
            } else
                finish()
        }
    }
    
    private fun closeDrawer() {
        drawerLayoutMain.closeDrawer(START, true)
    }
    
    private fun setToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        val actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayoutMain, R.string.open_drawer, R.string.close_drawer)
        drawerLayoutMain?.addDrawerListener(actionBarDrawerToggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        actionBarDrawerToggle.syncState()
    }
    
    private fun setupDrawer() {
        setAppVersion()
        setOldGamesActionNewGameListener()
        setMenuItemSelectedListener()
    }
    
    private fun setAppVersion() {
        val tvAppVersion = navigationViewMain?.getHeaderView(0)?.findViewById<TextView>(R.id.tvDrawerHeaderAppVersion)
        tvAppVersion?.text = BuildConfig.VERSION_NAME
    }
    
    private fun setOldGamesActionNewGameListener() {
        val oldGamesItem = navigationViewMain?.menu?.findItem(R.id.nav_oldgames)
        oldGamesItem?.actionView?.findViewById<View>(R.id.ibMainDrawerOldGamesActionLayoutNewGame)?.setOnClickListener {
            goToActivity(Intent(this, GameActivity::class.java), GameActivity.CODE)
            closeDrawer()
        }
    }
    
    private fun setMenuItemSelectedListener() {
        navigationViewMain?.setNavigationItemSelectedListener { menuItem ->
            this.closeDrawer()
            when (menuItem.itemId) {
                R.id.nav_oldgames -> viewModel?.navigateTo(OLD_GAMES)
                R.id.nav_combinations -> viewModel?.navigateTo(COMBINATIONS)
                R.id.nav_greenbook -> viewModel?.navigateTo(GREEN_BOOK)
                R.id.nav_rate -> viewModel?.navigateTo(RATE)
                R.id.nav_contact -> viewModel?.navigateTo(CONTACT)
                else -> return@setNavigationItemSelectedListener false
            }
            return@setNavigationItemSelectedListener true
        }
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            openDrawer()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    
    private fun openDrawer() {
        if (drawerLayoutMain != null) {
            drawerLayoutMain?.openDrawer(START, true)
        }
    }
}