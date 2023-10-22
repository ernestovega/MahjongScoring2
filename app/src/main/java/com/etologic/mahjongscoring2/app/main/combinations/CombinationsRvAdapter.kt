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
package com.etologic.mahjongscoring2.app.main.combinations

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.extensions.setOnSecureClickListener
import com.etologic.mahjongscoring2.app.model.ShowState
import com.etologic.mahjongscoring2.app.model.ShowState.HIDE
import com.etologic.mahjongscoring2.app.model.ShowState.SHOW
import com.etologic.mahjongscoring2.business.model.entities.Combination
import com.etologic.mahjongscoring2.business.model.entities.Combination.CombinationDescriptionType.IMAGE
import com.etologic.mahjongscoring2.databinding.CombinationItemBinding
import java.util.*
import javax.inject.Inject

internal class CombinationsRvAdapter
@Inject constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    
    private var combinations: List<Combination> = ArrayList()
    private var imageOrDescriptionShowState = HIDE
    private var cardViewMinHeight: Int = 0
    
    @SuppressLint("NotifyDataSetChanged")
    internal fun setCombinations(combinations: List<Combination>) {
        saveCombinationsCopy(combinations)
        notifyDataSetChanged()
    }
    
    private fun saveCombinationsCopy(combinations: List<Combination>) {
        val newCombinationsCopy = ArrayList<Combination>(combinations.size)
        for (combination in combinations) {
            newCombinationsCopy.add(combination.getCopy())
        }
        this.combinations = newCombinationsCopy
    }
    
    fun toggleImageOrDescription(): ShowState {
        imageOrDescriptionShowState = if (imageOrDescriptionShowState === SHOW) HIDE else SHOW
        notifyItemRangeChanged(0, combinations.size - 1)
        return imageOrDescriptionShowState
    }
    
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        cardViewMinHeight = recyclerView.context.resources.getDimension(
            R.dimen.combination_item_cardview_min_height
        ).toInt()
    }
    
    override fun getItemCount(): Int {
        return combinations.size
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemBinding = CombinationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CombinationItemViewHolder(itemBinding)
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val combination = combinations[position]
        val myHolder = holder as CombinationItemViewHolder
        myHolder.tvPoints.text = combination.combinationPoints.toString()
        myHolder.tvName.text = combination.combinationName
        myHolder.tvPosition.text = String.format(Locale.getDefault(), "#%d", position + 1)
        
        if (combination.combinationDescriptionType === IMAGE) {
            myHolder.ivImage.setImageResource(combination.combinationImage)
            myHolder.tvDescription.visibility = GONE
            myHolder.ivImage.visibility = VISIBLE
        } else {
            myHolder.tvDescription.text = combination.combinationDescription
            myHolder.ivImage.visibility = GONE
            myHolder.tvDescription.visibility = VISIBLE
        }
        
        myHolder.flImageOrDescriptionContainer.visibility = if (imageOrDescriptionShowState === SHOW) VISIBLE else GONE
        
        myHolder.llContainer.setOnSecureClickListener {
            myHolder.flImageOrDescriptionContainer.visibility = if (myHolder.cardView.height == cardViewMinHeight) VISIBLE else GONE
        }
    }
    
    internal inner class CombinationItemViewHolder(binding: CombinationItemBinding) : RecyclerView.ViewHolder(binding.root) {
        
        val llContainer: LinearLayout = binding.llCombinationItemContainer
        val cardView: CardView = binding.cvCombinationItem
        val tvPoints: TextView = binding.tvCombinationItemPoints
        val tvName: TextView = binding.tvCombinationItemName
        val tvPosition: TextView = binding.tvCombinationItemPosition
        val flImageOrDescriptionContainer: FrameLayout = binding.flCombinationItemImageOrDescriptionContainer
        val ivImage: ImageView = binding.ivCombinationItemImage
        val tvDescription: TextView = binding.tvCombinationItemDescription
    }
}