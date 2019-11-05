package es.etologic.mahjongscoring2.app.game.game_table

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import es.etologic.mahjongscoring2.R
import es.etologic.mahjongscoring2.app.base.BaseFragment
import es.etologic.mahjongscoring2.app.game.activity.GameActivityViewModel
import es.etologic.mahjongscoring2.app.game.activity.GameActivityViewModelFactory
import es.etologic.mahjongscoring2.app.game.dialogs.GameRankingFragmentDialog
import es.etologic.mahjongscoring2.app.model.DialogType
import es.etologic.mahjongscoring2.app.model.EnablingState
import es.etologic.mahjongscoring2.app.model.EnablingState.DISABLED
import es.etologic.mahjongscoring2.app.model.EnablingState.ENABLED
import es.etologic.mahjongscoring2.app.model.Seat
import es.etologic.mahjongscoring2.app.model.ShowState
import es.etologic.mahjongscoring2.app.model.ShowState.SHOW
import es.etologic.mahjongscoring2.domain.model.enums.FabMenuStates
import es.etologic.mahjongscoring2.domain.model.enums.TableWinds.*
import kotlinx.android.synthetic.main.game_table_fragment.*
import javax.inject.Inject

class GameTableFragment : BaseFragment(), GameTableSeatsFragment.TableSeatsListener {
    
    @Inject internal lateinit var activityViewModelFactory: GameActivityViewModelFactory
    private lateinit var activityViewModel: GameActivityViewModel
    private var tableSeats: GameTableSeatsFragment? = null
    private var eastIcon: Drawable? = null
    private var southIcon: Drawable? = null
    private var westIcon: Drawable? = null
    private var northIcon: Drawable? = null
    
    //EVENTS
    override fun onEastSeatClick() {
        activityViewModel.onSeatClicked(EAST)
    }
    
    override fun onSouthSeatClick() {
        activityViewModel.onSeatClicked(SOUTH)
    }
    
    override fun onWestSeatClick() {
        activityViewModel.onSeatClicked(WEST)
    }
    
    override fun onNorthSeatClick() {
        activityViewModel.onSeatClicked(NORTH)
    }
    
    //LIFECYCLE
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.game_table_fragment, container, false)
        eastIcon = ContextCompat.getDrawable(inflater.context, R.drawable.ic_east)
        southIcon = ContextCompat.getDrawable(inflater.context, R.drawable.ic_south)
        westIcon = ContextCompat.getDrawable(inflater.context, R.drawable.ic_west)
        northIcon = ContextCompat.getDrawable(inflater.context, R.drawable.ic_north)
        return view
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tableSeats = childFragmentManager.findFragmentById(R.id.fGameTableSeats) as GameTableSeatsFragment?
        setupActivityViewModel()
        setupFabMenu()
        setOnClickListeners()
    }
    
    private fun setOnClickListeners() {
        tableSeats?.setTableSeatsListener(this)
        
        fabGameDice?.setOnClickListener {
            activityViewModel.famMenuDiceClicked()
        }
        
        fabGameTablePenaltyCancel?.setOnClickListener {
            activityViewModel.onFabGamePenaltyCancelClicked()
        }
        
        fabGameTablePenalty?.setOnClickListener {
            activityViewModel.onFabGamePenaltyClicked()
        }
        
        fabGameTableWashout?.setOnClickListener {
            activityViewModel.onFabGameWashoutClicked()
        }
        
        fabGameTableTsumo?.setOnClickListener {
            activityViewModel.onFabGameTsumoClicked()
        }
        
        fabGameTableRon?.setOnClickListener {
            activityViewModel.onFabGameRonClicked()
        }
        
        fabGameTableCancel?.setOnClickListener {
            activityViewModel.onFabCancelRequestingLooserClicked()
        }
        
        fabGameTableRanking?.setOnClickListener {
            activityViewModel.onFabRankingClicked()
        }
    }
    
    private fun setupFabMenu() {
        famGameTable.setClosedOnTouchOutside(true)
        famGameTable.setOnMenuToggleListener { activityViewModel.onToggleFabMenu(it) }
    }
    
    private fun setupActivityViewModel() {
        activityViewModel = ViewModelProviders.of(this, activityViewModelFactory).get(GameActivityViewModel::class.java)
        activityViewModel.roundNumber.observe(this, Observer { it?.let { it1 -> this.roundNumberObserver(it1) } })
        activityViewModel.eastSeat.observe(this, Observer<Seat> { it?.let { it1 -> tableSeats?.setEastSeat(it1) } })
        activityViewModel.southSeat.observe(this, Observer<Seat> { it?.let { it1 -> tableSeats?.setSouthSeat(it1) } })
        activityViewModel.westSeat.observe(this, Observer<Seat> { it?.let { it1 -> tableSeats?.setWestSeat(it1) } })
        activityViewModel.northSeat.observe(this, Observer<Seat> { it?.let { it1 -> tableSeats?.setNorthSeat(it1) } })
        activityViewModel.fabMenuState.observe(this, Observer<FabMenuStates> { it?.let { it1 -> this.fabMenuStateObserver(it1) } })
        activityViewModel.fabMenuOpenState.observe(this, Observer<ShowState> { it?.let { it1 -> this.fabMenuOpenStateObserver(it1) } })
        activityViewModel.showDialog.observe(this, Observer<DialogType> { it?.let { it1 -> this.showDialogObserver(it1) } })
    }
    
    private fun roundNumberObserver(roundNumber: Int) {
        if (roundNumber == 16) {
            tvGameTableRoundNumberUp.text = String.format("%s - End", roundNumber)
            tvGameTableRoundNumberDown.text = String.format("%s - End", roundNumber)
        } else {
            tvGameTableRoundNumberUp.text = roundNumber.toString()
            tvGameTableRoundNumberDown.text = roundNumber.toString()
        }
        when {
            roundNumber < 5 -> {
                ivGameTableRoundWindUp.setImageDrawable(eastIcon)
                ivGameTableRoundWindDown.setImageDrawable(eastIcon)
            }
            roundNumber < 9 -> {
                ivGameTableRoundWindUp.setImageDrawable(southIcon)
                ivGameTableRoundWindDown.setImageDrawable(southIcon)
            }
            roundNumber < 13 -> {
                ivGameTableRoundWindUp.setImageDrawable(westIcon)
                ivGameTableRoundWindDown.setImageDrawable(westIcon)
            }
            else -> {
                ivGameTableRoundWindUp.setImageDrawable(northIcon)
                ivGameTableRoundWindDown.setImageDrawable(northIcon)
            }
        }
    }
    
    private fun fabMenuStateObserver(fabMenuStates: FabMenuStates) {
        when (fabMenuStates) {
            FabMenuStates.NORMAL -> {
                applyFabMenuState(DISABLED, DISABLED, DISABLED, DISABLED)
                fabGameTableRanking.visibility = GONE
                fabGameTableCancel.visibility = GONE
            }
            FabMenuStates.PLAYER_SELECTED -> {
                applyFabMenuState(DISABLED, ENABLED, ENABLED, ENABLED)
                fabGameTableRanking.visibility = GONE
                fabGameTableCancel.visibility = GONE
            }
            FabMenuStates.PLAYER_PENALIZED -> {
                applyFabMenuState(ENABLED, ENABLED, DISABLED, DISABLED)
                fabGameTableRanking.visibility = GONE
                fabGameTableCancel.visibility = GONE
            }
            FabMenuStates.RANKING -> {
                fabGameTableRanking.visibility = VISIBLE
                famGameTable.visibility = GONE
                fabGameDice.visibility = GONE
                fabGameTableRanking.visibility = GONE
                fabGameTableCancel.visibility = VISIBLE
                showSnackbar(fabGameTableCancel, getString(R.string.select_discarder_player))
            }
            FabMenuStates.CANCEL_REQUEST_DISCARDER -> {
                famGameTable.visibility = GONE
                fabGameDice.visibility = GONE
                fabGameTableRanking.visibility = GONE
                fabGameTableCancel.visibility = VISIBLE
                showSnackbar(fabGameTableCancel, getString(R.string.select_discarder_player))
            }
        }
    }
    
    private fun applyFabMenuState(
        penaltyCancelState: EnablingState,
        penaltyState: EnablingState,
        tsumoState: EnablingState,
        ronState: EnablingState
    ) {
        if (penaltyCancelState == ENABLED) {
            fabGameTablePenalty.isEnabled = true
            fabGameTablePenalty.visibility = VISIBLE
        } else {
            fabGameTablePenalty.isEnabled = false
            fabGameTablePenalty.visibility = GONE
        }
        fabGameTablePenalty.isEnabled = penaltyState == ENABLED
        fabGameTableTsumo.isEnabled = tsumoState == ENABLED
        fabGameTableRon.isEnabled = ronState == ENABLED
    }
    
    private fun fabMenuOpenStateObserver(state: ShowState) {
        if (state == SHOW) {
            if (famGameTable.visibility != VISIBLE) {
                famGameTable.visibility = VISIBLE
                fabGameDice.visibility = GONE
            }
            if (!famGameTable.isOpened) {
                famGameTable.open(true)
            }
        } else {
            if (famGameTable.isOpened) {
                famGameTable.close(true)
            }
            if (famGameTable.visibility == VISIBLE) {
                famGameTable.visibility = GONE
                fabGameDice.visibility = VISIBLE
            }
        }
    }
    
    private fun showDialogObserver(dialogType: DialogType) {
        if (dialogType == DialogType.SHOW_RANKING) {
            showRankingDialog()
        }
    }
    
    private fun showRankingDialog() {
        GameRankingFragmentDialog().show(childFragmentManager, GameRankingFragmentDialog.TAG)
    }
}
