/*
 *     Copyright © 2023  Ernesto Vega de la Iglesia Soria
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.etologic.mahjongscoring2.app.main.old_games

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.etologic.mahjongscoring2.app.extensions.setOnSecureClickListener
import com.etologic.mahjongscoring2.app.model.GameItemDiffUtilCallback
import com.etologic.mahjongscoring2.app.utils.DateTimeUtils
import com.etologic.mahjongscoring2.business.model.dtos.BestHand
import com.etologic.mahjongscoring2.business.model.entities.UIGame
import com.etologic.mahjongscoring2.data_source.model.GameId
import com.etologic.mahjongscoring2.databinding.MainOldgameItemBinding
import java.util.Locale
import javax.inject.Inject

class OldGamesRvAdapter
@Inject constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface GameItemListener {
        fun onOldGameItemDeleteClicked(gameId: GameId)
        fun onOldGameItemShareClicked(gameId: GameId)
        fun onOldGameItemResumeClicked(gameId: GameId)
    }

    private var itemClickListener: GameItemListener? = null
    private var games: List<UIGame> = ArrayList()

    init {
        games = ArrayList()
    }

    fun setOldGameItemListener(listener: GameItemListener) {
        this.itemClickListener = listener
    }

    fun setGames(newGames: List<UIGame>) {
        val result = DiffUtil.calculateDiff(GameItemDiffUtilCallback(newGames, games), false)
        saveNewGamesCopy(newGames)
        result.dispatchUpdatesTo(this)
    }

    private fun saveNewGamesCopy(newGames: List<UIGame>) {
        val newGamesCopy = ArrayList<UIGame>(newGames.size)
        newGames.map { newGamesCopy.add(it.copy()) }
        games = newGamesCopy
    }

    override fun getItemCount(): Int {
        return games.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemBinding = MainOldgameItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OldGameItemViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemViewHolder = holder as OldGameItemViewHolder
        val game = games[position]
        val bestHand = game.getBestHand()
        setFields(itemViewHolder, game, bestHand)
        itemViewHolder.cvContainer.setOnSecureClickListener {
            itemViewHolder.gameId?.let { itemClickListener?.onOldGameItemResumeClicked(it) }
        }
        itemViewHolder.btItemDelete.setOnSecureClickListener {
            itemViewHolder.gameId?.let { itemClickListener?.onOldGameItemDeleteClicked(it) }
        }
        itemViewHolder.btItemShare.setOnSecureClickListener {
            itemViewHolder.gameId?.let { itemClickListener?.onOldGameItemShareClicked(it) }
        }
        itemViewHolder.btItemResume.setOnSecureClickListener {
            itemViewHolder.gameId?.let { itemClickListener?.onOldGameItemResumeClicked(it) }
        }
    }

    private fun setFields(
        itemViewHolder: OldGameItemViewHolder,
        uiGame: UIGame,
        bestHand: BestHand
    ) {
        itemViewHolder.gameId = uiGame.dbGame.gameId
        itemViewHolder.tvGameName.text = uiGame.dbGame.gameName
        itemViewHolder.tvStartDate.text = DateTimeUtils.getPrettyDate(uiGame.dbGame.startDate)
        itemViewHolder.tvEastPlayerName.text = uiGame.dbGame.nameP1
        itemViewHolder.tvSouthPlayerName.text = uiGame.dbGame.nameP2
        itemViewHolder.tvWestPlayerName.text = uiGame.dbGame.nameP3
        itemViewHolder.tvNorthPlayerName.text = uiGame.dbGame.nameP4
        val playersTotalPoints = uiGame.getPlayersTotalPointsWithPenalties().map { String.format(Locale.getDefault(), "%d", it) }
        itemViewHolder.tvEastPlayerPoints.text = playersTotalPoints[0]
        itemViewHolder.tvSouthPlayerPoints.text = playersTotalPoints[1]
        itemViewHolder.tvWestPlayerPoints.text = playersTotalPoints[2]
        itemViewHolder.tvNorthPlayerPoints.text = playersTotalPoints[3]
        itemViewHolder.tvRoundNumber.text = uiGame.rounds.size.toString()
        setBestHand(itemViewHolder, bestHand)
    }

    private fun setBestHand(itemViewHolder: OldGameItemViewHolder, bestHand: BestHand) {
        itemViewHolder.tvBestHandPlayerName.text = bestHand.playerName.ifEmpty { "-" }
        itemViewHolder.tvBestHandValue.text = if (bestHand.handValue == 0) "-" else bestHand.handValue.toString()
    }

    inner class OldGameItemViewHolder(binding: MainOldgameItemBinding) : RecyclerView.ViewHolder(binding.root) {

        val cvContainer: CardView = binding.cvOldGameItem
        val tvGameName: TextView = binding.tvOldGameItemGameName
        val tvStartDate: TextView = binding.tvOldGameItemStartDate
        val tvEastPlayerName: TextView = binding.tvOlgGameItemPlayerEastName
        val tvSouthPlayerName: TextView = binding.tvOlgGameItemPlayerSouthName
        val tvWestPlayerName: TextView = binding.tvOlgGameItemPlayerWestName
        val tvNorthPlayerName: TextView = binding.tvOlgGameItemPlayerNorthName
        val tvEastPlayerPoints: TextView = binding.tvOldGameItemPlayerEastPoints
        val tvSouthPlayerPoints: TextView = binding.tvOldGameItemPlayerSouthPoints
        val tvWestPlayerPoints: TextView = binding.tvOldGameItemPlayerWestPoints
        val tvNorthPlayerPoints: TextView = binding.tvOldGameItemPlayerNorthPoints
        val tvRoundNumber: TextView = binding.tvOldGameItemRoundsNumber
        val tvBestHandPlayerName: TextView = binding.tvOldGameItemBestHandPlayerName
        val tvBestHandValue: TextView = binding.tvOldGameItemBestHandValue
        val btItemDelete: ImageButton = binding.btOldGameItemDelete
        val btItemShare: ImageButton = binding.btOldGameItemShare
        val btItemResume: ImageButton = binding.btOldGameItemResume
        var gameId: GameId? = null
    }
}