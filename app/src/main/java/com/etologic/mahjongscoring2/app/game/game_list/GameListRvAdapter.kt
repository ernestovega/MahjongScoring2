package com.etologic.mahjongscoring2.app.game.game_list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.R.color
import com.etologic.mahjongscoring2.app.base.BaseRvAdapter
import com.etologic.mahjongscoring2.app.extensions.setOnSecureClickListener
import com.etologic.mahjongscoring2.business.model.entities.Round
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.*
import kotlinx.android.synthetic.main.game_list_round_item.view.*
import javax.inject.Inject

internal class GameListRvAdapter
@Inject constructor() : BaseRvAdapter<Round>() {
    
    //Fields
    private var greenMM: Int? = null
    private var grayMM: Int? = null
    private var red: Int? = null
    private var accent: Int? = null
    
    //Lifecycle
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        initResources(parent.context)
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.game_list_round_item, parent, false)
        return ItemViewHolder(itemView)
    }
    
    private fun initResources(context: Context) {
        accent = ContextCompat.getColor(context, color.colorAccent)
        greenMM = ContextCompat.getColor(context, color.colorPrimary)
        grayMM = ContextCompat.getColor(context, color.grayMM)
        red = ContextCompat.getColor(context, color.red)
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val round = collection[position]
        val myHolder = holder as ItemViewHolder
        holder.round = round
        fillTexts(round, myHolder)
        setWinnerColor(round, myHolder)
        setLooserColor(round, myHolder)
        setPenaltiesIcons(round, myHolder)
        holder.llTotalsContainer.visibility = if(round.isExpanded) VISIBLE else GONE
        
    }
    
    private fun fillTexts(item: Round, mHolder: ItemViewHolder) {
        mHolder.tvRoundNum.text = item.roundId.toString()
        mHolder.tvHandPoints.text = item.handPoints.toString()
        (if (item.isBestHand) accent else grayMM)?.let { mHolder.tvHandPoints.setTextColor(it) }
        mHolder.tvPointsP1.text = item.pointsP1.toString()
        mHolder.tvPointsP2.text = item.pointsP2.toString()
        mHolder.tvPointsP3.text = item.pointsP3.toString()
        mHolder.tvPointsP4.text = item.pointsP4.toString()
        mHolder.tvTotalPointsP1.text = item.totalPointsP1.toString()
        mHolder.tvTotalPointsP2.text = item.totalPointsP2.toString()
        mHolder.tvTotalPointsP3.text = item.totalPointsP3.toString()
        mHolder.tvTotalPointsP4.text = item.totalPointsP4.toString()
    }
    
    private fun setWinnerColor(item: Round, mHolder: ItemViewHolder) {
        (if (item.winnerInitialSeat == EAST) greenMM else grayMM)?.let { mHolder.tvPointsP1.setTextColor(it) }
        (if (item.winnerInitialSeat == SOUTH) greenMM else grayMM)?.let { mHolder.tvPointsP2.setTextColor(it) }
        (if (item.winnerInitialSeat == WEST) greenMM else grayMM)?.let { mHolder.tvPointsP3.setTextColor(it) }
        (if (item.winnerInitialSeat == NORTH) greenMM else grayMM)?.let { mHolder.tvPointsP4.setTextColor(it) }
    }
    
    private fun setLooserColor(item: Round, mHolder: ItemViewHolder) {
        red?.let {
            when (item.discarderInitialSeat) {
                EAST -> mHolder.tvPointsP1.setTextColor(it)
                SOUTH -> mHolder.tvPointsP2.setTextColor(it)
                WEST -> mHolder.tvPointsP3.setTextColor(it)
                NORTH -> mHolder.tvPointsP4.setTextColor(it)
                else -> {}
            }
        }
    }
    
    private fun setPenaltiesIcons(item: Round, mHolder: ItemViewHolder) {
        mHolder.ivPenaltyP1.visibility = if (item.penaltyP1 < 0) VISIBLE else GONE
        mHolder.ivPenaltyP2.visibility = if (item.penaltyP2 < 0) VISIBLE else GONE
        mHolder.ivPenaltyP3.visibility = if (item.penaltyP3 < 0) VISIBLE else GONE
        mHolder.ivPenaltyP4.visibility = if (item.penaltyP4 < 0) VISIBLE else GONE
    }
    
    //VIEWHOLDER
    internal inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        
        var llContainer: LinearLayout = view.llGameListItemContainer
        var tvRoundNum: TextView = view.tvGameListItemRoundNumber
        var tvHandPoints: TextView = view.tvGameListItemHandPoints
        var tvPointsP1: TextView = view.tvGameListItemRoundPointsP1
        var tvPointsP2: TextView = view.tvGameListItemRoundPointsP2
        var tvPointsP3: TextView = view.tvGameListItemRoundPointsP3
        var tvPointsP4: TextView = view.tvGameListItemRoundPointsP4
        var ivPenaltyP1: ImageView = view.ivGameListItemPenaltyIconP1
        var ivPenaltyP2: ImageView = view.ivGameListItemPenaltyIconP2
        var ivPenaltyP3: ImageView = view.ivGameListItemPenaltyIconP3
        var ivPenaltyP4: ImageView = view.ivGameListItemPenaltyIconP4
        var llTotalsContainer: LinearLayout = view.llGameListItemRoundTotalsContainer
        var tvTotalPointsP1: TextView = view.tvGameListItemRoundTotalPointsP1
        var tvTotalPointsP2: TextView = view.tvGameListItemRoundTotalPointsP2
        var tvTotalPointsP3: TextView = view.tvGameListItemRoundTotalPointsP3
        var tvTotalPointsP4: TextView = view.tvGameListItemRoundTotalPointsP4
        lateinit var round: Round
        
        init {
            llContainer.setOnSecureClickListener {
                round.isExpanded = !round.isExpanded
                notifyDataSetChanged()
            }
        }
    }
    
}
