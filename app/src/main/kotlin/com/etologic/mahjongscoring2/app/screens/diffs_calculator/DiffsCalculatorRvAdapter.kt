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
package com.etologic.mahjongscoring2.app.screens.diffs_calculator

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.model.Diff
import com.etologic.mahjongscoring2.app.screens.diffs_calculator.DiffsCalculatorFragment.Companion.NUM_CALCS_INTERVAL
import com.etologic.mahjongscoring2.databinding.DiffsCalculatorItemBinding
import javax.inject.Inject

class DiffsCalculatorRvAdapter
@Inject constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var diffs = mutableListOf<Diff>()
    private var gray: Int? = null
    private var white: Int? = null
    @SuppressLint("NotifyDataSetChanged")
    fun setFirstDiffs(firstDiffs: List<Diff>) {
        diffs.addAll(firstDiffs)
        notifyDataSetChanged()
    }

    fun setNextDiffs(newDiffs: List<Diff>) {
        val currentSize = diffs.size
        diffs.addAll(newDiffs)
        notifyItemRangeInserted(currentSize, NUM_CALCS_INTERVAL)
    }

    override fun getItemCount(): Int {
        return diffs.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        initResources(parent.context)
        val itemBinding = DiffsCalculatorItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiffsCalculatorItemViewHolder(itemBinding)
    }

    private fun initResources(context: Context) {
        gray = ContextCompat.getColor(context, R.color.grayLight)
        white = ContextCompat.getColor(context, R.color.white)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val diff = diffs[position]
        val myHolder = holder as DiffsCalculatorItemViewHolder
        myHolder.tvPointsNeeded.text = diff.pointsNeeded.toString()
        myHolder.tvSelfPick.text = diff.selfPick.toString()
        myHolder.tvDirectHu.text = diff.directHu.toString()
        myHolder.tvIndirectHu.text = diff.indirectHu.toString()
        (if (position % 2 == 0) white else gray)?.let { myHolder.llContainer.setBackgroundColor(it) }
    }

    inner class DiffsCalculatorItemViewHolder(binding: DiffsCalculatorItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val llContainer: LinearLayout = binding.llDiffsCalculator
        val tvPointsNeeded: TextView = binding.tvDiffsCalculatorItemPointsNeeded
        val tvSelfPick: TextView = binding.tvDiffsCalculatorItemSelfPick
        val tvDirectHu: TextView = binding.tvDiffsCalculatorItemDirectHu
        val tvIndirectHu: TextView = binding.tvDiffsCalculatorItemIndirectHu
    }
}