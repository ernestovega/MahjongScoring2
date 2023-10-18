/*
    Copyright (C) 2020  Ernesto Vega de la Iglesia Soria
 
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.etologic.mahjongscoring2.app.main.old_games

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.etologic.mahjongscoring2.app.extensions.setOnSecureClickListener
import com.etologic.mahjongscoring2.app.model.GameItemDiffUtilCallback
import com.etologic.mahjongscoring2.app.utils.DateTimeUtils
import com.etologic.mahjongscoring2.app.utils.StringUtils
import com.etologic.mahjongscoring2.business.model.dtos.BestHand
import com.etologic.mahjongscoring2.business.model.entities.Table
import com.etologic.mahjongscoring2.databinding.MainOldgameItemBinding
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
        val result = DiffUtil.calculateDiff(GameItemDiffUtilCallback(newGames, games), false)
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
        val itemBinding = MainOldgameItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OldGameItemViewHolder(itemBinding)
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemViewHolder = holder as OldGameItemViewHolder
        val gameWithRounds = games[position]
        val bestHand = gameWithRounds.getBestHand()
        setFields(itemViewHolder, gameWithRounds, bestHand)
        itemViewHolder.btItemResume.setOnSecureClickListener {
            if (itemViewHolder.gameId != null)
                itemClickListener?.onOldGameItemResumeClicked(itemViewHolder.gameId!!)
        }
        itemViewHolder.btItemDelete.setOnSecureClickListener {
            if (itemViewHolder.gameId != null)
                itemClickListener?.onOldGameItemDeleteClicked(itemViewHolder.gameId!!)
        }
    }
    
    private fun setFields(
        itemViewHolder: OldGameItemViewHolder,
        table: Table,
        bestHand: BestHand
    ) {
        itemViewHolder.gameId = table.game.gameId
        itemViewHolder.tvStartDate.text = DateTimeUtils.getPrettyDate(table.game.startDate)
        itemViewHolder.tvDuration.text = String.format("#%s", itemViewHolder.gameId.toString())
        itemViewHolder.tvEastPlayerName.text = table.game.nameP1
        itemViewHolder.tvSouthPlayerName.text = table.game.nameP2
        itemViewHolder.tvWestPlayerName.text = table.game.nameP3
        itemViewHolder.tvNorthPlayerName.text = table.game.nameP4
        val playersTotalPoints = table.getPlayersTotalPointsStringByCurrentSeat()
        itemViewHolder.tvEastPlayerPoints.text = playersTotalPoints[0]
        itemViewHolder.tvSouthPlayerPoints.text = playersTotalPoints[1]
        itemViewHolder.tvWestPlayerPoints.text = playersTotalPoints[2]
        itemViewHolder.tvNorthPlayerPoints.text = playersTotalPoints[3]
        itemViewHolder.tvRoundNumber.text = table.rounds.size.toString()
        setBestHand(itemViewHolder, bestHand)
    }
    
    private fun setBestHand(itemViewHolder: OldGameItemViewHolder, bestHand: BestHand?) {
        if (bestHand == null || StringUtils.isEmpty(bestHand.playerName) ||
            bestHand.handValue <= 0
        ) {
            itemViewHolder.llBestHandContainer.visibility = GONE
        } else {
            itemViewHolder.llBestHandContainer.visibility = VISIBLE
            itemViewHolder.tvBestHandPlayerName.text = bestHand.playerName
            itemViewHolder.tvBestHandValue.text = bestHand.handValue.toString()
        }
    }
    
    internal inner class OldGameItemViewHolder(binding: MainOldgameItemBinding) : RecyclerView.ViewHolder(binding.root) {
        
        val tvStartDate: TextView = binding.tvOldGameItemStartDate
        val tvDuration: TextView = binding.tvOldGameItemDuration
        val tvEastPlayerName: TextView = binding.tvOlgGameItemPlayerEastName
        val tvSouthPlayerName: TextView = binding.tvOlgGameItemPlayerSouthName
        val tvWestPlayerName: TextView = binding.tvOlgGameItemPlayerWestName
        val tvNorthPlayerName: TextView = binding.tvOlgGameItemPlayerNorthName
        val tvEastPlayerPoints: TextView = binding.tvOldGameItemPlayerEastPoints
        val tvSouthPlayerPoints: TextView = binding.tvOldGameItemPlayerSouthPoints
        val tvWestPlayerPoints: TextView = binding.tvOldGameItemPlayerWestPoints
        val tvNorthPlayerPoints: TextView = binding.tvOldGameItemPlayerNorthPoints
        val tvRoundNumber: TextView = binding.tvOldGameItemRoundsNumber
        val llBestHandContainer: LinearLayout = binding.llOldGameItemItemBestHand
        val tvBestHandPlayerName: TextView = binding.tvOldGameItemBestHandPlayerName
        val tvBestHandValue: TextView = binding.tvOldGameItemBestHandValue
        val btItemDelete: TextView = binding.btOldGameItemDelete
        val btItemResume: TextView = binding.btOldGameItemResume
        var gameId: Long? = null
    }
}