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
package com.etologic.mahjongscoring2.app.model

import androidx.recyclerview.widget.DiffUtil
import com.etologic.mahjongscoring2.app.utils.DateTimeUtils
import com.etologic.mahjongscoring2.business.model.dtos.BestHand
import com.etologic.mahjongscoring2.business.model.entities.Round
import com.etologic.mahjongscoring2.business.model.entities.Table
import com.etologic.mahjongscoring2.business.model.entities.Table.Companion.NUM_MCR_PLAYERS
import java.util.Locale

class GameItemDiffUtilCallback(private val newList: List<Table>, private val oldList: List<Table>) : DiffUtil.Callback() {

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
                    oldGame.getPlayersTotalPointsByCurrentSeat().map {
                        String.format(Locale.getDefault(), "%d", it)
                    },
                    newGame.getPlayersTotalPointsByCurrentSeat().map {
                        String.format(Locale.getDefault(), "%d", it)
                    }
                ) &&
                areBestHandsEqual(oldGame.getBestHand(), newGame.getBestHand())
                && Round.areEqual(oldGame.rounds, newGame.rounds))
    }

    private fun arePlayersTotalsPointsEquals(
        oldPlayersTotalPoints: List<String>,
        newPlayersTotalPoints: List<String>
    ): Boolean {
        for (i in 0 until NUM_MCR_PLAYERS) {
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
