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
import es.etologic.mahjongscoring2.R
import es.etologic.mahjongscoring2.R.drawable
import es.etologic.mahjongscoring2.app.game.base.BaseGameActivityFragment
import es.etologic.mahjongscoring2.app.game.dialogs.RankingDialogFragment
import es.etologic.mahjongscoring2.app.game.game_table.GameTableSeatsFragment.TableSeatsListener
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
import kotlinx.android.synthetic.main.game_table_fragment.view.*

class GameTableFragment : BaseGameActivityFragment() {
    
    private lateinit var tableSeats: GameTableSeatsFragment
    private var eastIcon: Drawable? = null
    private var southIcon: Drawable? = null
    private var westIcon: Drawable? = null
    private var northIcon: Drawable? = null
    
    //LIFECYCLE
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.game_table_fragment, container, false)
        initResources(inflater)
        initTableSeats()
        setupFabMenu(view)
        setOnClickListeners(view)
        return view
    }
    
    private fun initResources(inflater: LayoutInflater) {
        eastIcon = ContextCompat.getDrawable(inflater.context, drawable.ic_east)
        southIcon = ContextCompat.getDrawable(inflater.context, drawable.ic_south)
        westIcon = ContextCompat.getDrawable(inflater.context, drawable.ic_west)
        northIcon = ContextCompat.getDrawable(inflater.context, drawable.ic_north)
    }
    
    private fun initTableSeats() {
        tableSeats = GameTableSeatsFragment()
        childFragmentManager
            .beginTransaction()
            .replace(R.id.flGameTableSeats, tableSeats)
            .commit()
    }
    
    private fun setupFabMenu(view :View) {
        view.famGameTable?.setClosedOnTouchOutside(true)
        view.famGameTable?.setOnMenuToggleListener { activityViewModel.onToggleFabMenu(it) }
    }
    
    private fun setOnClickListeners(view: View) {
        tableSeats.setTableSeatsListener(object : TableSeatsListener {
            override fun onEastSeatClick() { activityViewModel.onSeatClicked(EAST) }
            override fun onSouthSeatClick() { activityViewModel.onSeatClicked(SOUTH) }
            override fun onWestSeatClick() { activityViewModel.onSeatClicked(WEST) }
            override fun onNorthSeatClick() { activityViewModel.onSeatClicked(NORTH) }})
        view.fabGameDice?.setOnClickListener { activityViewModel.famMenuDiceClicked() }
        view.fabGameTablePenaltyCancel?.setOnClickListener { activityViewModel.onFabGamePenaltyCancelClicked() }
        view.fabGameTablePenalty?.setOnClickListener { activityViewModel.onFabGamePenaltyClicked() }
        view.fabGameTableWashout?.setOnClickListener { activityViewModel.onFabGameWashoutClicked() }
        view.fabGameTableTsumo?.setOnClickListener { activityViewModel.onFabGameTsumoClicked() }
        view.fabGameTableRon?.setOnClickListener { activityViewModel.onFabGameRonClicked() }
        view.fabGameTableCancel?.setOnClickListener { activityViewModel.onFabCancelRequestingLooserClicked() }
        view.fabGameTableRanking?.setOnClickListener { activityViewModel.onFabRankingClicked() }
    }
    
    override fun setupActivityViewModelObservers() {
        activityViewModel.getEastSeat().observe(this, Observer<Seat>(tableSeats::setEastSeat))
        activityViewModel.getSouthSeat().observe(this, Observer<Seat>(tableSeats::setSouthSeat))
        activityViewModel.getWestSeat().observe(this, Observer<Seat>(tableSeats::setWestSeat))
        activityViewModel.getNorthSeat().observe(this, Observer<Seat>(tableSeats::setNorthSeat))
        activityViewModel.getRoundNumber().observe(this, Observer<Int>(this::roundNumberObserver))
        activityViewModel.getFabMenuState().observe(this, Observer<FabMenuStates>(this::fabMenuStateObserver))
        activityViewModel.getFabMenuOpenState().observe(this, Observer<ShowState>(this::fabMenuOpenStateObserver))
        activityViewModel.getShowDialog().observe(this, Observer<DialogType>(this::showDialogObserver))
        activityViewModel.initGame()
    }
    
    private fun roundNumberObserver(roundNumber: Int) {
        if (roundNumber == 16) {
            tvGameTableRoundNumberUp?.text = String.format("%s - End", roundNumber)
            tvGameTableRoundNumberDown?.text = String.format("%s - End", roundNumber)
            tvGameTableRoundNumberUp?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            tvGameTableRoundNumberDown?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        } else {
            tvGameTableRoundNumberUp?.text = roundNumber.toString()
            tvGameTableRoundNumberDown?.text = roundNumber.toString()
            when {
                roundNumber < 5 -> {
                    tvGameTableRoundNumberUp?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, eastIcon)
                    tvGameTableRoundNumberDown?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, eastIcon) }
                roundNumber < 9 -> {
                    tvGameTableRoundNumberUp?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, southIcon)
                    tvGameTableRoundNumberDown?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, southIcon) }
                roundNumber < 13 -> {
                    tvGameTableRoundNumberUp?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, westIcon)
                    tvGameTableRoundNumberDown?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, westIcon) }
                else -> {
                    tvGameTableRoundNumberUp?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, northIcon)
                    tvGameTableRoundNumberDown?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, northIcon) }
            }
        }
    }
    
    private fun fabMenuStateObserver(fabMenuStates: FabMenuStates) {
        when (fabMenuStates) {
            FabMenuStates.NORMAL -> {
                applyFabMenuState(DISABLED, DISABLED, DISABLED, DISABLED)
                fabGameTableRanking?.visibility = GONE
                fabGameTableCancel?.visibility = GONE
            }
            FabMenuStates.PLAYER_SELECTED -> {
                applyFabMenuState(DISABLED, ENABLED, ENABLED, ENABLED)
                fabGameTableRanking?.visibility = GONE
                fabGameTableCancel?.visibility = GONE
            }
            FabMenuStates.PLAYER_PENALIZED -> {
                applyFabMenuState(ENABLED, ENABLED, DISABLED, DISABLED)
                fabGameTableRanking?.visibility = GONE
                fabGameTableCancel?.visibility = GONE
            }
            FabMenuStates.RANKING -> {
                fabGameTableRanking.visibility = VISIBLE
                famGameTable?.visibility = GONE
                fabGameDice?.visibility = GONE
                fabGameTableRanking?.visibility = GONE
                fabGameTableCancel?.visibility = VISIBLE
                showSnackbar(fabGameTableCancel, getString(R.string.select_discarder_player))
            }
            FabMenuStates.CANCEL_REQUEST_DISCARDER -> {
                famGameTable?.visibility = GONE
                fabGameDice?.visibility = GONE
                fabGameTableRanking?.visibility = GONE
                fabGameTableCancel?.visibility = VISIBLE
                showSnackbar(fabGameTableCancel, getString(R.string.select_discarder_player))
            }
        }
    }
    
    private fun applyFabMenuState(penaltyCancelState: EnablingState, penaltyState: EnablingState, tsumoState: EnablingState, ronState: EnablingState) {
        if (penaltyCancelState == ENABLED) {
            fabGameTablePenalty?.isEnabled = true
            fabGameTablePenalty?.visibility = VISIBLE
        } else {
            fabGameTablePenalty?.isEnabled = false
            fabGameTablePenalty?.visibility = GONE
        }
        fabGameTablePenalty?.isEnabled = penaltyState == ENABLED
        fabGameTableTsumo?.isEnabled = tsumoState == ENABLED
        fabGameTableRon?.isEnabled = ronState == ENABLED
    }
    
    private fun fabMenuOpenStateObserver(state: ShowState) {
        if (state == SHOW) {
            if (famGameTable?.visibility != VISIBLE) {
                famGameTable?.visibility = VISIBLE
                fabGameDice?.visibility = GONE
            }
            if (famGameTable?.isOpened == false)
                famGameTable?.open(true)
        } else {
            if (famGameTable?.isOpened == true)
                famGameTable?.close(true)
            if (famGameTable?.visibility == VISIBLE) {
                famGameTable?.visibility = GONE
                fabGameDice?.visibility = VISIBLE
            }
        }
    }
    
    private fun showDialogObserver(dialogType: DialogType) {
        if (dialogType == DialogType.SHOW_RANKING)
            RankingDialogFragment().show(childFragmentManager, RankingDialogFragment.TAG)
    }
}
