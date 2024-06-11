/*
 *     Copyright © 2024  Ernesto Vega de la Iglesia Soria
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
package com.etologic.mahjongscoring2.app.screens.old_games

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.model.GameItemDiffUtilCallback
import com.etologic.mahjongscoring2.app.screens.game.dialogs.RankingTableHelper
import com.etologic.mahjongscoring2.app.utils.DateTimeUtils
import com.etologic.mahjongscoring2.app.utils.setOnSecureClickListener
import com.etologic.mahjongscoring2.app.utils.toStringSigned
import com.etologic.mahjongscoring2.business.model.dtos.BestHand
import com.etologic.mahjongscoring2.business.model.entities.GameId
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.databinding.OldGamesItemBinding
import javax.inject.Inject

class OldGamesRvAdapter
@Inject constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface GameItemListener {
        fun onOldGameItemDeleteClicked(gameId: GameId)
        fun onOldGameItemShareClicked(gameId: GameId)
        fun onOldGameItemResumeClicked(gameId: GameId)
    }

    private var itemClickListener: GameItemListener? = null
    private var games: List<UiGame> = ArrayList()
    private var trophyIcon: Drawable? = null

    init {
        games = ArrayList()
    }

    fun setOldGameItemListener(listener: GameItemListener) {
        this.itemClickListener = listener
    }

    fun setGames(newGames: List<UiGame>) {
        val result = DiffUtil.calculateDiff(GameItemDiffUtilCallback(newGames, games), false)
        saveNewGamesCopy(newGames)
        result.dispatchUpdatesTo(this)
    }

    private fun saveNewGamesCopy(newGames: List<UiGame>) {
        val newGamesCopy = ArrayList<UiGame>(newGames.size)
        newGames.map { newGamesCopy.add(it.copy()) }
        games = newGamesCopy
    }

    override fun getItemCount(): Int {
        return games.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        trophyIcon = AppCompatResources.getDrawable(parent.context, R.drawable.ic_trophy_golden)
        val itemBinding = OldGamesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
    }

    private fun setFields(
        itemViewHolder: OldGameItemViewHolder,
        uiGame: UiGame,
        bestHand: BestHand
    ) {
        itemViewHolder.gameId = uiGame.gameId
        val prettyStartDate = DateTimeUtils.getPrettyDate(uiGame.startDate)
        itemViewHolder.tvGameName.text = uiGame.gameName.ifEmpty { prettyStartDate.replace("\n", " ") }
        itemViewHolder.tvStartDate.text = prettyStartDate
        itemViewHolder.tvEastPlayerName.text = uiGame.nameP1
        itemViewHolder.tvSouthPlayerName.text = uiGame.nameP2
        itemViewHolder.tvWestPlayerName.text = uiGame.nameP3
        itemViewHolder.tvNorthPlayerName.text = uiGame.nameP4
        itemViewHolder.tvEastPlayerPoints.text = uiGame.ongoingRound.totalPointsP1.toStringSigned()
        itemViewHolder.tvSouthPlayerPoints.text = uiGame.ongoingRound.totalPointsP2.toStringSigned()
        itemViewHolder.tvWestPlayerPoints.text = uiGame.ongoingRound.totalPointsP3.toStringSigned()
        itemViewHolder.tvNorthPlayerPoints.text = uiGame.ongoingRound.totalPointsP4.toStringSigned()
        setWinnerIcon(uiGame, itemViewHolder)
        itemViewHolder.tvRoundNumber.text = uiGame.finishedRounds.size.toString()
        setBestHand(itemViewHolder, bestHand)
    }

    private fun setWinnerIcon(uiGame: UiGame, itemViewHolder: OldGameItemViewHolder) {
        itemViewHolder.tvEastPlayerPoints.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
        itemViewHolder.tvSouthPlayerPoints.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
        itemViewHolder.tvWestPlayerPoints.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
        itemViewHolder.tvNorthPlayerPoints.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
        RankingTableHelper.generateRankingTable(uiGame)?.sortedPlayersRankings?.first()?.let {
            when (it.name) {
                uiGame.nameP1 -> {
                    with(itemViewHolder.tvEastPlayerPoints) {
                        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, trophyIcon, null)
                        compoundDrawablePadding = 8
                    }
                }
                uiGame.nameP2 -> {
                    with(itemViewHolder.tvSouthPlayerPoints) {
                        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, trophyIcon, null)
                        compoundDrawablePadding = 8
                    }
                }
                uiGame.nameP3 -> {
                    with(itemViewHolder.tvWestPlayerPoints) {
                        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, trophyIcon, null)
                        compoundDrawablePadding = 8
                    }
                }
                uiGame.nameP4 -> {
                    with(itemViewHolder.tvNorthPlayerPoints) {
                        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, trophyIcon, null)
                        compoundDrawablePadding = 8
                    }
                }
            }
        }
    }

    private fun setBestHand(itemViewHolder: OldGameItemViewHolder, bestHand: BestHand) {
        itemViewHolder.tvBestHandPlayerName.text = bestHand.playerName.ifEmpty { "-" }
        itemViewHolder.tvBestHandValue.text = if (bestHand.handValue == 0) "-" else bestHand.handValue.toString()
    }

    inner class OldGameItemViewHolder(binding: OldGamesItemBinding) : RecyclerView.ViewHolder(binding.root) {

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
        var gameId: GameId? = null
    }
}