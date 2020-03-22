package com.etologic.mahjongscoring2.app.main.combinations

import android.view.LayoutInflater
import android.view.View
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
import com.etologic.mahjongscoring2.app.model.ShowState
import com.etologic.mahjongscoring2.app.model.ShowState.HIDE
import com.etologic.mahjongscoring2.app.model.ShowState.SHOW
import com.etologic.mahjongscoring2.business.model.entities.Combination
import com.etologic.mahjongscoring2.business.model.entities.Combination.CombinationDescriptionType.IMAGE
import kotlinx.android.synthetic.main.combination_item.view.*
import java.util.*

internal class CombinationsRvAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    
    private var combinations: List<Combination> = ArrayList()
    private var imageOrDescriptionShowState = HIDE
    private var cardViewMinHeight: Int = 0
    
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
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.combination_item, parent, false)
        return CombinationItemViewHolder(itemView)
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val combination = combinations[position]
        val myHolder = holder as CombinationItemViewHolder
        myHolder.tvPoints?.text = combination.combinationPoints.toString()
        myHolder.tvName?.text = combination.combinationName
        myHolder.tvPosition?.text = String.format(Locale.getDefault(), "#%d", position + 1)
        
        if (combination.combinationDescriptionType === IMAGE) {
            myHolder.ivImage?.setImageResource(combination.combinationImage)
            myHolder.tvDescription?.visibility = GONE
            myHolder.ivImage?.visibility = VISIBLE
        } else {
            myHolder.tvDescription?.text = combination.combinationDescription
            myHolder.ivImage?.visibility = GONE
            myHolder.tvDescription?.visibility = VISIBLE
        }
        
        myHolder.flImageOrDescriptionContainer?.visibility = if (imageOrDescriptionShowState === SHOW) VISIBLE else GONE
        
        myHolder.llContainer?.setOnClickListener {
            myHolder.flImageOrDescriptionContainer?.visibility = if (myHolder.cardView?.height == cardViewMinHeight) VISIBLE else GONE
        }
    }
    
    internal inner class CombinationItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        
        val llContainer: LinearLayout? = itemView.llCombinationItemContainer
        val cardView: CardView? = itemView.cvCombinationItem
        val tvPoints: TextView? = itemView.tvCombinationItemPoints
        val tvName: TextView? = itemView.tvCombinationItemName
        val tvPosition: TextView? = itemView.tvCombinationItemPosition
        val flImageOrDescriptionContainer: FrameLayout? = itemView.flCombinationItemImageOrDescriptionContainer
        val ivImage: ImageView? = itemView.ivCombinationItemImage
        val tvDescription: TextView? = itemView.tvCombinationItemDescription
    }
}