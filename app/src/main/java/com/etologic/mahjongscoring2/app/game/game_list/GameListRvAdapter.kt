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
import com.etologic.mahjongscoring2.R.color
import com.etologic.mahjongscoring2.app.base.BaseRvAdapter
import com.etologic.mahjongscoring2.app.extensions.setOnSecureClickListener
import com.etologic.mahjongscoring2.business.model.entities.Round
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.EAST
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NORTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.SOUTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.WEST
import com.etologic.mahjongscoring2.databinding.GameListRoundItemBinding
import javax.inject.Inject

internal class GameListRvAdapter
@Inject constructor() : BaseRvAdapter<Round>() {

    internal interface GameListItemListener {
        fun onClick(view: View, roundId: Int)
    }

    //Fields
    private var greenMM: Int? = null
    private var grayMM: Int? = null
    private var red: Int? = null
    private var accent: Int? = null
    private var gray: Int? = null
    private var white: Int? = null
    private var itemListener: GameListItemListener? = null

    internal fun setItemListener(listener: GameListItemListener) {
        itemListener = listener
    }

    //Lifecycle
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        initResources(parent.context)
        val itemBinding = GameListRoundItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(itemBinding)
    }

    private fun initResources(context: Context) {
        accent = ContextCompat.getColor(context, color.colorAccent)
        greenMM = ContextCompat.getColor(context, color.colorPrimary)
        grayMM = ContextCompat.getColor(context, color.grayMM)
        red = ContextCompat.getColor(context, color.red)
        gray = ContextCompat.getColor(context, color.grayLight)
        white = ContextCompat.getColor(context, color.white)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val round = collection[position]
        val myHolder = holder as ItemViewHolder
        fillTexts(round, myHolder)
        setWinnerColor(round, myHolder)
        setLooserColor(round, myHolder)
        setPenaltiesIcons(round, myHolder)
        setBackgroundColor(round, myHolder)
        holder.llContainer.setOnSecureClickListener { itemListener?.onClick(holder.tvRoundNum, round.roundId) }
        holder.llContainer.setOnLongClickListener { itemListener?.onClick(holder.tvRoundNum, round.roundId); true }
    }

    private fun fillTexts(item: Round, mHolder: ItemViewHolder) {
        mHolder.tvRoundNum.text = item.roundNumber.toString()
        mHolder.tvHandPoints.text = item.handPoints.toString()
        (if (item.isBestHand) accent else grayMM)?.let { mHolder.tvHandPoints.setTextColor(it) }
        mHolder.tvPointsP1.text = String.format("%+d", item.pointsP1)
        mHolder.tvPointsP2.text = String.format("%+d", item.pointsP2)
        mHolder.tvPointsP3.text = String.format("%+d", item.pointsP3)
        mHolder.tvPointsP4.text = String.format("%+d", item.pointsP4)
        mHolder.tvTotalPointsP1.text = String.format("%+d", item.totalPointsP1)
        mHolder.tvTotalPointsP2.text = String.format("%+d", item.totalPointsP2)
        mHolder.tvTotalPointsP3.text = String.format("%+d", item.totalPointsP3)
        mHolder.tvTotalPointsP4.text = String.format("%+d", item.totalPointsP4)
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
                else -> {
                }
            }
        }
    }

    private fun setPenaltiesIcons(item: Round, mHolder: ItemViewHolder) {
        mHolder.ivPenaltyP1.visibility = if (item.penaltyP1 < 0) VISIBLE else GONE
        mHolder.ivPenaltyP2.visibility = if (item.penaltyP2 < 0) VISIBLE else GONE
        mHolder.ivPenaltyP3.visibility = if (item.penaltyP3 < 0) VISIBLE else GONE
        mHolder.ivPenaltyP4.visibility = if (item.penaltyP4 < 0) VISIBLE else GONE
    }

    private fun setBackgroundColor(item: Round, mHolder: ItemViewHolder) {
        (if (item.roundNumber % 2 == 0) gray else white)?.let {
            mHolder.llContainer.setBackgroundColor(it)
        }
    }

    //VIEW HOLDER
    internal inner class ItemViewHolder(binding: GameListRoundItemBinding) : RecyclerView.ViewHolder(binding.root) {

        var llContainer: LinearLayout = binding.llGameListItemContainer
        var tvRoundNum: TextView = binding.tvGameListItemRoundNumber
        var tvHandPoints: TextView = binding.tvGameListItemHandPoints
        var tvPointsP1: TextView = binding.tvGameListItemRoundPointsP1
        var tvPointsP2: TextView = binding.tvGameListItemRoundPointsP2
        var tvPointsP3: TextView = binding.tvGameListItemRoundPointsP3
        var tvPointsP4: TextView = binding.tvGameListItemRoundPointsP4
        var ivPenaltyP1: ImageView = binding.ivGameListItemPenaltyIconP1
        var ivPenaltyP2: ImageView = binding.ivGameListItemPenaltyIconP2
        var ivPenaltyP3: ImageView = binding.ivGameListItemPenaltyIconP3
        var ivPenaltyP4: ImageView = binding.ivGameListItemPenaltyIconP4
        var tvTotalPointsP1: TextView = binding.tvGameListItemRoundTotalPointsP1
        var tvTotalPointsP2: TextView = binding.tvGameListItemRoundTotalPointsP2
        var tvTotalPointsP3: TextView = binding.tvGameListItemRoundTotalPointsP3
        var tvTotalPointsP4: TextView = binding.tvGameListItemRoundTotalPointsP4
    }

}
