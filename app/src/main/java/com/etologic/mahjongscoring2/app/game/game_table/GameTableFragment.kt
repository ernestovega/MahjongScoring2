package com.etologic.mahjongscoring2.app.game.game_table

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.TypedValue
import android.util.TypedValue.*
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.RelativeLayout.*
import androidx.core.content.ContextCompat.getDrawable
import androidx.lifecycle.Observer
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.R.*
import com.etologic.mahjongscoring2.app.game.base.BaseGameFragment
import com.etologic.mahjongscoring2.app.game.game_table.GameTableSeatsFragment.Companion.TAG
import com.etologic.mahjongscoring2.app.game.game_table.GameTableSeatsFragment.TableSeatsListener
import com.etologic.mahjongscoring2.app.model.DialogType.DICE
import com.etologic.mahjongscoring2.business.model.entities.Round
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.*
import com.google.android.material.badge.BadgeDrawable.*
import kotlinx.android.synthetic.main.game_table_fragment.*

class GameTableFragment : BaseGameFragment(), TableSeatsListener {
    
    private var tableSeats: GameTableSeatsFragment = GameTableSeatsFragment()
    
    private var eastIcon: Drawable? = null
    private var southIcon: Drawable? = null
    private var westIcon: Drawable? = null
    private var northIcon: Drawable? = null
    
    //EVENTS
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
    
    //LIFECYCLE
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layout.game_table_fragment, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initResources()
        initTableSeats()
        setOnClickListeners()
        initViewModel()
        activityViewModel?.loadGame()
    }
    
    private fun initResources() {
        eastIcon = context?.let { getDrawable(it, drawable.ic_east) }
        southIcon = context?.let { getDrawable(it, drawable.ic_south) }
        westIcon = context?.let { getDrawable(it, drawable.ic_west) }
        northIcon = context?.let { getDrawable(it, drawable.ic_north) }
    }
    
    private fun initTableSeats() {
        childFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .add(R.id.fcvGameTableSeats, tableSeats, TAG)
            .commit()
    }
    
    private fun setOnClickListeners() {
        tableSeats.setTableSeatsListener(this)
        fabGameTableDice?.setOnClickListener { activityViewModel?.showDialog(DICE) }
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
        moveDice(roundId)
        setRoundNumsAndWinds(currentRound, roundId)
    }
    
    private fun moveDice(roundId: Int) {
        when(roundId) {
            1 -> setFabPosition(BOTTOM_END)
            2 -> setFabPosition(TOP_END)
            3 -> setFabPosition(TOP_START)
            4 -> setFabPosition(BOTTOM_START)
            5 -> setFabPosition(BOTTOM_END)
            6 -> setFabPosition(TOP_END)
            7 -> setFabPosition(TOP_START)
            8 -> setFabPosition(BOTTOM_START)
            9 -> setFabPosition(BOTTOM_END)
            10 -> setFabPosition(TOP_END)
            11 -> setFabPosition(TOP_START)
            12 -> setFabPosition(BOTTOM_START)
            13 -> setFabPosition(BOTTOM_END)
            14 -> setFabPosition(TOP_END)
            15 -> setFabPosition(TOP_START)
            16 -> setFabPosition(BOTTOM_START)
        }
    }
    
    private fun setFabPosition(position: Int) {
        val params = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        val standarMarginInPx = applyDimension(COMPLEX_UNIT_DIP, 16f, resources.displayMetrics).toInt()
        params.setMargins(standarMarginInPx, standarMarginInPx, standarMarginInPx, standarMarginInPx)
        when(position) {
            BOTTOM_END -> {
                tvGameTableRoundNumberBottomEnd.visibility = GONE
                tvGameTableRoundNumberTopStart.visibility = VISIBLE
                tvGameTableRoundNumberTopEnd.visibility = VISIBLE
                tvGameTableRoundNumberBottomStart.visibility = VISIBLE
                params.addRule(ALIGN_PARENT_BOTTOM)
                params.addRule(ALIGN_PARENT_END)
            }
            TOP_START -> {
                tvGameTableRoundNumberBottomEnd.visibility = VISIBLE
                tvGameTableRoundNumberTopStart.visibility = GONE
                tvGameTableRoundNumberTopEnd.visibility = VISIBLE
                tvGameTableRoundNumberBottomStart.visibility = VISIBLE
                params.addRule(ALIGN_PARENT_TOP)
                params.addRule(ALIGN_PARENT_START)
            }
            TOP_END -> {
                tvGameTableRoundNumberBottomEnd.visibility = VISIBLE
                tvGameTableRoundNumberTopStart.visibility = VISIBLE
                tvGameTableRoundNumberTopEnd.visibility = GONE
                tvGameTableRoundNumberBottomStart.visibility = VISIBLE
                params.addRule(ALIGN_PARENT_TOP)
                params.addRule(ALIGN_PARENT_END)
            }
            BOTTOM_START -> {
                tvGameTableRoundNumberBottomEnd.visibility = VISIBLE
                tvGameTableRoundNumberTopStart.visibility = VISIBLE
                tvGameTableRoundNumberTopEnd.visibility = VISIBLE
                tvGameTableRoundNumberBottomStart.visibility = GONE
                params.addRule(ALIGN_PARENT_BOTTOM)
                params.addRule(ALIGN_PARENT_START)
            }
        }
        fabGameTableDice?.layoutParams = params
    }
    
    private fun setRoundNumsAndWinds(currentRound: Round, roundId: Int) {
        if (currentRound.isEnded || roundId >= 16) {
            val endString = getString(string.end)
            tvGameTableRoundNumberTopStart?.text = endString
            tvGameTableRoundNumberTopEnd?.text = endString
            tvGameTableRoundNumberBottomStart?.text = endString
            tvGameTableRoundNumberBottomEnd?.text = endString
            tvGameTableRoundNumberTopStart?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            tvGameTableRoundNumberTopEnd?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            tvGameTableRoundNumberBottomStart?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            tvGameTableRoundNumberBottomEnd?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        } else {
            tvGameTableRoundNumberTopStart?.text = roundId.toString()
            tvGameTableRoundNumberTopEnd?.text = roundId.toString()
            tvGameTableRoundNumberBottomStart?.text = roundId.toString()
            tvGameTableRoundNumberBottomEnd?.text = roundId.toString()
            when {
                roundId < 5 -> {
                    tvGameTableRoundNumberTopStart?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, eastIcon)
                    tvGameTableRoundNumberTopEnd?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, eastIcon)
                    tvGameTableRoundNumberBottomStart?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, eastIcon)
                    tvGameTableRoundNumberBottomEnd?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, eastIcon)
                }
                roundId < 9 -> {
                    tvGameTableRoundNumberTopStart?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, southIcon)
                    tvGameTableRoundNumberTopEnd?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, southIcon)
                    tvGameTableRoundNumberBottomStart?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, southIcon)
                    tvGameTableRoundNumberBottomEnd?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, southIcon)
                }
                roundId < 13 -> {
                    tvGameTableRoundNumberTopStart?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, westIcon)
                    tvGameTableRoundNumberTopEnd?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, westIcon)
                    tvGameTableRoundNumberBottomStart?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, westIcon)
                    tvGameTableRoundNumberBottomEnd?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, westIcon)
                }
                else -> {
                    tvGameTableRoundNumberTopStart?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, northIcon)
                    tvGameTableRoundNumberTopEnd?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, northIcon)
                    tvGameTableRoundNumberBottomStart?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, northIcon)
                    tvGameTableRoundNumberBottomEnd?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, northIcon)
                }
            }
        }
    }
}
