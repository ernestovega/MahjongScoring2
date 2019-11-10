package es.etologic.mahjongscoring2.app.game.game_table

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import es.etologic.mahjongscoring2.R
import es.etologic.mahjongscoring2.R.drawable
import es.etologic.mahjongscoring2.app.game.base.BaseGameActivityFragment
import es.etologic.mahjongscoring2.app.game.dialogs.RankingDialogFragment
import es.etologic.mahjongscoring2.app.game.game_table.GameTableSeatsFragment.TableSeatsListener
import es.etologic.mahjongscoring2.app.model.DialogType
import es.etologic.mahjongscoring2.app.model.Seat
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
    
    private fun setOnClickListeners(view: View) {
        tableSeats.setTableSeatsListener(object : TableSeatsListener {
            override fun onEastSeatClick() { activityViewModel.onSeatClicked(EAST) }
            override fun onSouthSeatClick() { activityViewModel.onSeatClicked(SOUTH) }
            override fun onWestSeatClick() { activityViewModel.onSeatClicked(WEST) }
            override fun onNorthSeatClick() { activityViewModel.onSeatClicked(NORTH) }})
        view.fabGameDice?.setOnClickListener { activityViewModel.diceClicked() }
        view.fabGameTableRanking?.setOnClickListener { activityViewModel.rankingClicked() }
    }
    
    override fun setupActivityViewModelObservers() {
        activityViewModel.getEastSeat().observe(this, Observer<Seat>(tableSeats::setEastSeat))
        activityViewModel.getSouthSeat().observe(this, Observer<Seat>(tableSeats::setSouthSeat))
        activityViewModel.getWestSeat().observe(this, Observer<Seat>(tableSeats::setWestSeat))
        activityViewModel.getNorthSeat().observe(this, Observer<Seat>(tableSeats::setNorthSeat))
        activityViewModel.getRoundNumber().observe(this, Observer<Int>(this::roundNumberObserver))
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
    
    private fun showDialogObserver(dialogType: DialogType) {
        if (dialogType == DialogType.RANKING)
            RankingDialogFragment().show(childFragmentManager, RankingDialogFragment.TAG)
    }
}
