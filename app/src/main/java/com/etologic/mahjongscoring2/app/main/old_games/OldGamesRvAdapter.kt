package com.etologic.mahjongscoring2.app.main.old_games

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.extensions.setOnSecureClickListener
import com.etologic.mahjongscoring2.app.model.GameItemDiffUtilCallback
import com.etologic.mahjongscoring2.app.utils.DateTimeUtils
import com.etologic.mahjongscoring2.app.utils.StringUtils
import com.etologic.mahjongscoring2.business.model.dtos.BestHand
import com.etologic.mahjongscoring2.business.model.entities.Table
import kotlinx.android.synthetic.main.main_oldgame_item.view.*
import java.util.*
import javax.inject.Inject

internal class OldGamesRvAdapter
@Inject internal constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    
    internal interface GameItemListener {
        
        fun onOldGameItemDeleteClicked(gameId: Long)
        fun onOldGameItemResumeClicked(gameId: Long)
    }
    
    private var itemClickListener: GameItemListener? = null
    private var games: List<Table> = ArrayList()
    
    init {
        games = ArrayList()
    }
    
    fun setOldGameItemListener(listener: GameItemListener) {
        this.itemClickListener = listener
    }
    
    fun setGames(newGames: List<Table>) {
        val result = DiffUtil.calculateDiff(GameItemDiffUtilCallback(newGames, games), true)
        saveNewGamesCopy(newGames)
        result.dispatchUpdatesTo(this)
    }
    
    private fun saveNewGamesCopy(newGames: List<Table>) {
        val newGamesCopy = ArrayList<Table>(newGames.size)
        newGames.map { newGamesCopy.add(it.getCopy()) }
        games = newGamesCopy
    }
    
    //LIFECYCLE
    override fun getItemCount(): Int {
        return games.size
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.main_oldgame_item, parent, false)
        return OldGameItemViewHolder(itemView)
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemViewHolder = holder as OldGameItemViewHolder
        val gameWithRounds = games[position]
        val duration = gameWithRounds.getDuration()
        val bestHand = gameWithRounds.getBestHand()
        setFields(itemViewHolder, gameWithRounds, duration, bestHand)
        itemViewHolder.btItemResume?.setOnSecureClickListener {
            if (itemViewHolder.gameId != null)
                itemClickListener?.onOldGameItemResumeClicked(itemViewHolder.gameId!!)
        }
        itemViewHolder.btItemDelete?.setOnSecureClickListener {
            if (itemViewHolder.gameId != null)
                itemClickListener?.onOldGameItemDeleteClicked(itemViewHolder.gameId!!)
        }
    }
    
    private fun setFields(itemViewHolder: OldGameItemViewHolder, table: Table, duration: Long, bestHand: BestHand) {
        itemViewHolder.gameId = table.game.gameId
        itemViewHolder.tvStartDate?.text =
            DateTimeUtils.getPrettyDate(table.game.startDate)
        itemViewHolder.tvDuration?.text = String.format("#%s", itemViewHolder.gameId.toString())//DateTimeUtils.getPrettyDuration(duration));
        itemViewHolder.tvEastPlayerName?.text = table.game.nameP1
        itemViewHolder.tvSouthPlayerName?.text = table.game.nameP2
        itemViewHolder.tvWestPlayerName?.text = table.game.nameP3
        itemViewHolder.tvNorthPlayerName?.text = table.game.nameP4
        val playersTotalPoints = table.getPlayersTotalPointsStringByCurrentSeat()
        itemViewHolder.tvEastPlayerPoints?.text = playersTotalPoints[0]
        itemViewHolder.tvSouthPlayerPoints?.text = playersTotalPoints[1]
        itemViewHolder.tvWestPlayerPoints?.text = playersTotalPoints[2]
        itemViewHolder.tvNorthPlayerPoints?.text = playersTotalPoints[3]
        itemViewHolder.tvRoundNumber?.text = table.rounds.size.toString()
        setBestHand(itemViewHolder, bestHand)
    }
    
    private fun setBestHand(itemViewHolder: OldGameItemViewHolder, bestHand: BestHand?) {
        if (bestHand == null || StringUtils.isEmpty(bestHand.playerName) ||
            bestHand.handValue <= 0
        ) {
            itemViewHolder.llBestHandContainer?.visibility = GONE
        } else {
            itemViewHolder.llBestHandContainer?.visibility = VISIBLE
            itemViewHolder.tvBestHandPlayerName?.text = bestHand.playerName
            itemViewHolder.tvBestHandValue?.text = bestHand.handValue.toString()
        }
    }
    
    internal inner class OldGameItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        
        val tvStartDate: TextView? = itemView.tvOldGameItemStartDate
        val tvDuration: TextView? = itemView.tvOldGameItemDuration
        val tvEastPlayerName: TextView? = itemView.tvOlgGameItemPlayerEastName
        val tvSouthPlayerName: TextView? = itemView.tvOlgGameItemPlayerSouthName
        val tvWestPlayerName: TextView? = itemView.tvOlgGameItemPlayerWestName
        val tvNorthPlayerName: TextView? = itemView.tvOlgGameItemPlayerNorthName
        val tvEastPlayerPoints: TextView? = itemView.tvOldGameItemPlayerEastPoints
        val tvSouthPlayerPoints: TextView? = itemView.tvOldGameItemPlayerSouthPoints
        val tvWestPlayerPoints: TextView? = itemView.tvOldGameItemPlayerWestPoints
        val tvNorthPlayerPoints: TextView? = itemView.tvOldGameItemPlayerNorthPoints
        val tvRoundNumber: TextView? = itemView.tvOldGameItemRoundsNumber
        val llBestHandContainer: LinearLayout? = itemView.llOldGameItemItemBestHand
        val tvBestHandPlayerName: TextView? = itemView.tvOldGameItemBestHandPlayerName
        val tvBestHandValue: TextView? = itemView.tvOldGameItemBestHandValue
        val btItemDelete: TextView? = itemView.btOldGameItemDelete
        val btItemResume: TextView? = itemView.btOldGameItemResume
        var gameId: Long? = null
    }
}