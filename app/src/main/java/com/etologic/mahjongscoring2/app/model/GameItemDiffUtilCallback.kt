package com.etologic.mahjongscoring2.app.model

import androidx.recyclerview.widget.DiffUtil
import com.etologic.mahjongscoring2.app.utils.DateTimeUtils
import com.etologic.mahjongscoring2.business.model.dtos.BestHand
import com.etologic.mahjongscoring2.business.model.entities.Table
import com.etologic.mahjongscoring2.business.model.entities.Round

class GameItemDiffUtilCallback(private val newList: List<Table>, private val oldList: List<Table>) : DiffUtil.Callback() {
    
    companion object {
        private const val NUM_GAME_PLAYERS = 4
    }
    
    override fun getOldListSize(): Int {
        return oldList.size
    }
    
    override fun getNewListSize(): Int {
        return newList.size
    }
    
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].game.gameId == newList[newItemPosition].game.gameId
    }
    
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val newGame = newList[newItemPosition]
        val oldGame = oldList[oldItemPosition]
        return (oldGame.game.gameId == newGame.game.gameId &&
            oldGame.game.nameP1 == newGame.game.nameP1 &&
            oldGame.game.nameP2 == newGame.game.nameP2 &&
            oldGame.game.nameP3 == newGame.game.nameP3 &&
            oldGame.game.nameP4 == newGame.game.nameP4 &&
            DateTimeUtils.areEqual(oldGame.game.startDate, newGame.game.startDate) &&
            arePlayersTotalsPointsEquals(
                oldGame.getPlayersTotalPointsStringByCurrentSeat(),
                newGame.getPlayersTotalPointsStringByCurrentSeat()
            ) &&
            areBestHandsEqual(oldGame.getBestHand(), newGame.getBestHand())
            && Round.areEqual(oldGame.rounds, newGame.rounds))
    }
    
    private fun arePlayersTotalsPointsEquals(
        oldPlayersTotalPoints: List<String>,
        newPlayersTotalPoints: List<String>
    ): Boolean {
        for (i in 0 until NUM_GAME_PLAYERS) {
            if (oldPlayersTotalPoints[i] != newPlayersTotalPoints[i]) {
                return false
            }
        }
        return true
    }
    
    private fun areBestHandsEqual(oldBestHand: BestHand, newBestHand: BestHand): Boolean {
        return oldBestHand.playerName == newBestHand.playerName && oldBestHand.handValue == newBestHand.handValue
    }
}
