package com.etologic.mahjongscoring2.app.game.game_table

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.*
import androidx.lifecycle.Observer
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.R.drawable
import com.etologic.mahjongscoring2.R.layout
import com.etologic.mahjongscoring2.app.game.base.BaseGameFragment
import com.etologic.mahjongscoring2.app.game.game_table.GameTableSeatsFragment.Companion.TAG
import com.etologic.mahjongscoring2.app.game.game_table.GameTableSeatsFragment.TableSeatsListener
import com.etologic.mahjongscoring2.app.model.DialogType.DICE
import com.etologic.mahjongscoring2.business.model.entities.Round
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.*
import kotlinx.android.synthetic.main.game_table_fragment.*

class GameTableFragment : BaseGameFragment() {
    
    private var tableSeats: GameTableSeatsFragment = GameTableSeatsFragment()
    private var eastIcon: Drawable? = context?.let { getDrawable(it, drawable.ic_east) }
    private var southIcon: Drawable? = context?.let { getDrawable(it, drawable.ic_south) }
    private var westIcon: Drawable? = context?.let { getDrawable(it, drawable.ic_west) }
    private var northIcon: Drawable? = context?.let { getDrawable(it, drawable.ic_north) }
    
    //LIFECYCLE
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layout.game_table_fragment, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTableSeats()
        setOnClickListeners()
        initViewModel()
        activityViewModel?.loadGame()
    }
    
    private fun initTableSeats() {
        tableSeats.let { childFragmentManager.beginTransaction().add(R.id.fcvGameTableSeats, it, TAG).commit() }
    }
    
    private fun setOnClickListeners() {
        tableSeats.setTableSeatsListener(object : TableSeatsListener {
            override fun onEastSeatClick() {
                activityViewModel?.onSeatClicked(EAST)
            }
            
            override fun onSouthSeatClick() {
                activityViewModel?.onSeatClicked(SOUTH)
            }
            
            override fun onWestSeatClick() {
                activityViewModel?.onSeatClicked(WEST)
            }
            
            override fun onNorthSeatClick() {
                activityViewModel?.onSeatClicked(NORTH)
            }
        })
        fabGameDiceUp?.setOnClickListener { activityViewModel?.showDialog(DICE) }
        fabGameDiceDown?.setOnClickListener { activityViewModel?.showDialog(DICE) }
    }
    
    private fun initViewModel() {
        activityViewModel?.getCurrentRound()?.observe(viewLifecycleOwner, Observer(this::currentRoundObserver))
        activityViewModel?.getEastSeat()?.observe(viewLifecycleOwner, Observer(tableSeats::setEastSeat))
        activityViewModel?.getSouthSeat()?.observe(viewLifecycleOwner, Observer(tableSeats::setSouthSeat))
        activityViewModel?.getWestSeat()?.observe(viewLifecycleOwner, Observer(tableSeats::setWestSeat))
        activityViewModel?.getNorthSeat()?.observe(viewLifecycleOwner, Observer(tableSeats::setNorthSeat))
    }
    
    private fun currentRoundObserver(currentRound: Round) {
        val roundId = currentRound.roundId
        if (currentRound.isEnded || roundId >= 16) {
            val endString = getString(R.string.end)
            tvGameTableRoundNumberUp?.text = endString
            tvGameTableRoundNumberDown?.text = endString
            tvGameTableRoundNumberUp?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            tvGameTableRoundNumberDown?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        } else {
            tvGameTableRoundNumberUp?.text = roundId.toString()
            tvGameTableRoundNumberDown?.text = roundId.toString()
            when {
                roundId < 5 -> {
                    tvGameTableRoundNumberUp?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, eastIcon)
                    tvGameTableRoundNumberDown?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, eastIcon)
                }
                roundId < 9 -> {
                    tvGameTableRoundNumberUp?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, southIcon)
                    tvGameTableRoundNumberDown?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, southIcon)
                }
                roundId < 13 -> {
                    tvGameTableRoundNumberUp?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, westIcon)
                    tvGameTableRoundNumberDown?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, westIcon)
                }
                else -> {
                    tvGameTableRoundNumberUp?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, northIcon)
                    tvGameTableRoundNumberDown?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, northIcon)
                }
            }
        }
    }
}
