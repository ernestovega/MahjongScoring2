package es.etologic.mahjongscoring2.app.game.activity

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import es.etologic.mahjongscoring2.R
import es.etologic.mahjongscoring2.app.base.BaseActivity
import es.etologic.mahjongscoring2.app.base.ViewPagerAdapter
import es.etologic.mahjongscoring2.app.game.dialogs.RollDiceDialogFragment
import es.etologic.mahjongscoring2.app.game.game_list.GameListFragment
import es.etologic.mahjongscoring2.app.game.game_table.GameTableFragment
import es.etologic.mahjongscoring2.app.main.combinations.CombinationsActivity
import es.etologic.mahjongscoring2.app.model.DialogType
import es.etologic.mahjongscoring2.app.model.EnablingState
import es.etologic.mahjongscoring2.app.model.GamePages
import es.etologic.mahjongscoring2.app.model.ToolbarState
import es.etologic.mahjongscoring2.app.utils.KeyboardUtils
import es.etologic.mahjongscoring2.domain.model.enums.TableWinds
import kotlinx.android.synthetic.main.game_activity.*
import javax.inject.Inject

class GameActivity : BaseActivity() {
    
    companion object {
        const val ARG_KEY_GAME_ID = "arg_key_game_id"
        private const val OFFSCREEN_PAGE_LIMIT = 1
    }
    
    @Inject internal lateinit var viewModelFactory: GameActivityViewModelFactory
    private lateinit var viewModel: GameActivityViewModel
    private var pointsDialogLayout: LinearLayout? = null
    private var penaltyPointsDialogLayout: LinearLayout? = null
    private var playersDialogLayout: LinearLayout? = null
    private var eastIconDrawable: Drawable? = null
    private var southIconDrawable: Drawable? = null
    private var westIconDrawable: Drawable? = null
    private var northIconDrawable: Drawable? = null
    
    //LIFECYCLE
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
        initPlayersDialogLayout()
        initPointsDialogLayout()
        initPenaltyPointsDialogLayout()
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
        viewPagerGame.isEnabled = enablingState === EnablingState.ENABLED
    }
    
    private fun showDialogObserver(dialogType: DialogType) {
        when (dialogType) {
            DialogType.PLAYERS_NAMES -> showPlayersDialog()
            DialogType.ROLL_DICE -> showRollDiceDialog()
            DialogType.REQUEST_HAND_POINTS -> showRequestHandPointsDialog()
            DialogType.REQUEST_PENALTY_POINTS -> showRequestPenaltyPointsDialog()
            DialogType.EXIT -> showDialogExitGame()
            DialogType.NONE -> return
            DialogType.SHOW_RANKING -> return
        }
    }
    
    private fun showPlayersDialog() {
        (playersDialogLayout?.parent as ViewGroup).removeView(playersDialogLayout)
        val builder = AlertDialog.Builder(this, R.style.AlertDialogStyleMM)
        val tiet1 = ((playersDialogLayout?.getChildAt(0) as TextInputLayout).getChildAt(0) as FrameLayout).getChildAt(0) as TextInputEditText
        val tiet2 = ((playersDialogLayout?.getChildAt(1) as TextInputLayout).getChildAt(0) as FrameLayout).getChildAt(0) as TextInputEditText
        val tiet3 = ((playersDialogLayout?.getChildAt(2) as TextInputLayout).getChildAt(0) as FrameLayout).getChildAt(0) as TextInputEditText
        val tiet4 = ((playersDialogLayout?.getChildAt(3) as TextInputLayout).getChildAt(0) as FrameLayout).getChildAt(0) as TextInputEditText
        val playersNames = viewModel.getListNames().value
        tiet1.setText(playersNames?.get(TableWinds.EAST.code) ?: "")
        tiet2.setText(playersNames?.get(TableWinds.SOUTH.code) ?: "")
        tiet3.setText(playersNames?.get(TableWinds.WEST.code) ?: "")
        tiet4.setText(playersNames?.get(TableWinds.NORTH.code) ?: "")
        builder.setTitle(R.string.players_names)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                viewModel.onRequestPlayersResponse(tiet1.text, tiet2.text, tiet3.text, tiet4.text)
                KeyboardUtils.hideKeyboard(tiet4)
            }
            .setNegativeButton(android.R.string.cancel) { _, _ -> KeyboardUtils.hideKeyboard(tiet4) }
            .setView(playersDialogLayout)
            .create()
            .show()
        tiet1.requestFocus()
        tiet1.selectAll()
        KeyboardUtils.showKeyboard(tiet1)
    }
    
    private fun showRollDiceDialog() {
        RollDiceDialogFragment().show(supportFragmentManager, RollDiceDialogFragment.TAG)
    }
    
    private fun showRequestHandPointsDialog() {
        (pointsDialogLayout?.parent as ViewGroup).removeView(pointsDialogLayout)
        val builder = AlertDialog.Builder(this, R.style.AlertDialogStyleMM)
        val tiet = ((pointsDialogLayout?.getChildAt(0) as TextInputLayout).getChildAt(0) as FrameLayout).getChildAt(0) as TextInputEditText
        builder.setTitle(R.string.hand_points)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                viewModel.onRequestHandPointsResponse(tiet.text)
                KeyboardUtils.hideKeyboard(tiet)
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                viewModel.onRequestHandPointsCancel()
                KeyboardUtils.hideKeyboard(tiet)
            }
            .setOnDismissListener { tiet.setText("") }
            .setView(pointsDialogLayout)
            .create()
            .show()
        tiet.requestFocus()
        KeyboardUtils.showKeyboard(tiet)
    }
    
    private fun showRequestPenaltyPointsDialog() {
        (penaltyPointsDialogLayout?.parent as ViewGroup).removeView(penaltyPointsDialogLayout)
        val builder = AlertDialog.Builder(this, R.style.AlertDialogStyleMM)
        val tiet = ((penaltyPointsDialogLayout?.getChildAt(0) as TextInputLayout).getChildAt(0) as FrameLayout).getChildAt(0) as TextInputEditText
        val checkbox = penaltyPointsDialogLayout?.getChildAt(1) as CheckBox
        builder.setTitle(R.string.penalty_points)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                viewModel.onRequestPenaltyPointsResponse(tiet.text, checkbox.isChecked)
                KeyboardUtils.hideKeyboard(tiet)
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                viewModel.onRequestPenaltyPointsCancel()
                KeyboardUtils.hideKeyboard(tiet)
            }
            .setOnDismissListener {
                tiet.setText("")
                checkbox.isChecked = true
            }
            .setView(penaltyPointsDialogLayout)
            .create()
            .show()
        tiet.requestFocus()
        KeyboardUtils.showKeyboard(tiet)
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
    
    //    private void gameFinished(Boolean gameFinished) {
    //        if (gameFinished != null) {
    //            if (gameFinished) finish();
    //            else {
    //                Snackbar.make(viewPager, R.string.error_updating_end_date, Snackbar.LENGTH_LONG)
    //                        .setActionTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
    //                        .setAction(R.string.exit, v -> finish())
    //                        .show();
    //            }
    //        }
    //    }
    private fun startGame() {
        val gameId = intent.getLongExtra(ARG_KEY_GAME_ID, -1)
        if (gameId < 0) {
            viewModel.createGame()
        } else {
            viewModel.loadGame(gameId)
        }
    }
    
    @Suppress("LocalVariableName")
    private fun initPlayersDialogLayout() {
        val MAX_TIET_LINES = 1
        val MAX_TIET_CHARS = 16
        val tiet1 = TextInputEditText(this)
        tiet1.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
        tiet1.setLines(MAX_TIET_LINES)
        tiet1.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(MAX_TIET_CHARS))
        tiet1.setCompoundDrawablesWithIntrinsicBounds(eastIconDrawable, null, null, null)
        tiet1.compoundDrawablePadding = MAX_TIET_CHARS
        tiet1.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                KeyboardUtils.showKeyboard(v)
            else
                KeyboardUtils.hideKeyboard(v)
        }
        val til1 = TextInputLayout(this)
        til1.hint = getString(R.string.player_east_name)
        til1.addView(tiet1)
        val tiet2 = TextInputEditText(this)
        tiet2.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
        tiet2.setLines(MAX_TIET_LINES)
        tiet2.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(MAX_TIET_CHARS))
        tiet2.setCompoundDrawablesWithIntrinsicBounds(southIconDrawable, null, null, null)
        tiet2.compoundDrawablePadding = MAX_TIET_CHARS
        tiet2.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                KeyboardUtils.showKeyboard(v)
            else
                KeyboardUtils.hideKeyboard(v)
        }
        val til2 = TextInputLayout(this)
        til2.hint = getString(R.string.player_south_name)
        til2.addView(tiet2)
        val tiet3 = TextInputEditText(this)
        tiet3.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
        tiet3.setLines(MAX_TIET_LINES)
        tiet3.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(MAX_TIET_CHARS))
        tiet3.setCompoundDrawablesWithIntrinsicBounds(westIconDrawable, null, null, null)
        tiet3.compoundDrawablePadding = MAX_TIET_CHARS
        tiet3.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                KeyboardUtils.showKeyboard(v)
            else
                KeyboardUtils.hideKeyboard(v)
        }
        val til3 = TextInputLayout(this)
        til3.hint = getString(R.string.player_west_name)
        til3.addView(tiet3)
        val tiet4 = TextInputEditText(this)
        tiet4.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
        tiet4.setLines(MAX_TIET_LINES)
        tiet4.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(MAX_TIET_CHARS))
        tiet4.setCompoundDrawablesWithIntrinsicBounds(northIconDrawable, null, null, null)
        tiet4.compoundDrawablePadding = MAX_TIET_CHARS
        tiet4.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                KeyboardUtils.showKeyboard(v)
            else
                KeyboardUtils.hideKeyboard(v)
        }
        val til4 = TextInputLayout(this)
        til4.hint = getString(R.string.player_name)
        til4.addView(tiet4)
        val layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        val margin = resources.getDimensionPixelSize(R.dimen.standard_margin)
        val half_margin = resources.getDimensionPixelSize(R.dimen.half_standard_margin)
        layoutParams.setMargins(margin, margin, margin, half_margin)
        layoutParams.marginStart = resources.getDimensionPixelSize(R.dimen.standard_margin)
        layoutParams.marginEnd = margin
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.layoutParams = layoutParams
        layout.addView(til1, layoutParams)
        layout.addView(til2, layoutParams)
        layout.addView(til3, layoutParams)
        layout.addView(til4, layoutParams)
        playersDialogLayout = layout
    }
    
    private fun initPointsDialogLayout() {
        val tiet = TextInputEditText(this)
        tiet.inputType = InputType.TYPE_CLASS_NUMBER
        val digitsFilter = InputFilter { source, start, end, _, _, _ ->
            ForLoop@ for (i in start until end) {
                if (!Character.isDigit(source[i])) {
                    return@InputFilter ""
                }
            }
            null
        }
        val lengthFilter = InputFilter.LengthFilter(4)
        tiet.filters = arrayOf(digitsFilter, lengthFilter)
        tiet.setLines(1)
        tiet.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                KeyboardUtils.showKeyboard(v)
            } else {
                KeyboardUtils.hideKeyboard(v)
            }
        }
        val til = TextInputLayout(this)
        til.hint = getString(R.string.enter_points)
        til.addView(tiet)
        val layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        val margin = resources.getDimensionPixelSize(R.dimen.standard_margin)
        val halfMargin = resources.getDimensionPixelSize(R.dimen.half_standard_margin)
        layoutParams.setMargins(margin, margin, margin, halfMargin)
        layoutParams.marginStart = resources.getDimensionPixelSize(R.dimen.standard_margin)
        layoutParams.marginEnd = margin
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.layoutParams = layoutParams
        layout.addView(til, layoutParams)
        pointsDialogLayout = layout
    }
    
    private fun initPenaltyPointsDialogLayout() {
        val tiet = TextInputEditText(this)
        tiet.inputType = InputType.TYPE_CLASS_NUMBER
        val filter = InputFilter { source, start, end, _, _, _ ->
            for (i in start until end) {
                if (!Character.isDigit(source[i])) {
                    return@InputFilter ""
                }
            }
            null
        }
        tiet.filters = arrayOf(filter)
        val filterArray = arrayOfNulls<InputFilter>(1)
        filterArray[0] = InputFilter.LengthFilter(4)
        tiet.filters = filterArray
        tiet.setLines(1)
        tiet.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                KeyboardUtils.showKeyboard(v)
            } else {
                KeyboardUtils.hideKeyboard(v)
            }
        }
        val til = TextInputLayout(this)
        til.hint = getString(R.string.enter_points)
        til.addView(tiet)
        val layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        val margin = resources.getDimensionPixelSize(R.dimen.standard_margin)
        val halfMargin = resources.getDimensionPixelSize(R.dimen.half_standard_margin)
        layoutParams.setMargins(margin, margin, margin, halfMargin)
        layoutParams.marginStart = resources.getDimensionPixelSize(R.dimen.standard_margin)
        layoutParams.marginEnd = margin
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.layoutParams = layoutParams
        layout.addView(til, layoutParams)
        val checkBox = CheckBox(this)
        checkBox.text = getString(R.string.divided_equally)
        checkBox.isChecked = true
        layout.addView(checkBox, layoutParams)
        penaltyPointsDialogLayout = layout
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
                showPlayersDialog()
                return true
            }
            //            case R.id.action_play:
            //            case R.id.action_pause:
            else -> return super.onOptionsItemSelected(item)
        }
    }
    
    override fun onBackPressed() {
        viewModel.onBackPressed()
    }
}
