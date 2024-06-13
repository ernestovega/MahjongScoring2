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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.base.BaseMainFragment
import com.etologic.mahjongscoring2.app.model.Diff
import com.etologic.mahjongscoring2.databinding.DiffsCalculatorFragmentBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DiffsCalculatorFragment : BaseMainFragment() {

    companion object {
        const val TAG = "DiffsCalculatorFragment"
        const val NUM_CALCS_INTERVAL: Int = 200
        const val MIN_POINTS_NEEDED: Int = 32
        const val MAX_INTERVALS: Int = 10
        const val MAX_ITEMS: Int = NUM_CALCS_INTERVAL * MAX_INTERVALS
    }

    @Inject
    lateinit var rvAdapter: DiffsCalculatorRvAdapter

    private var _binding: DiffsCalculatorFragmentBinding? = null
    private val binding get() = _binding!!

    override val menuProvider: MenuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {}
        override fun onMenuItemSelected(menuItem: MenuItem): Boolean = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DiffsCalculatorFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadFirstInterval()
    }

    private fun setupRecyclerView() {
        with(binding.recyclerViewDiffsCalculator) {
            setHasFixedSize(true)
            val myLayoutManager = LinearLayoutManager(this.context)
            layoutManager = myLayoutManager
            adapter = rvAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val lastVisibleItemPosition = myLayoutManager.findLastVisibleItemPosition()
                    val totalItemCount = myLayoutManager.itemCount
                    if (lastVisibleItemPosition == totalItemCount - 1) {
                        loadNextInterval()
                    }
                }
            })
        }
    }

    private fun loadFirstInterval() {

        fun getFirstInterval(): List<Diff> {
            val list = mutableListOf<Diff>()
            for (i in MIN_POINTS_NEEDED..<MIN_POINTS_NEEDED + NUM_CALCS_INTERVAL) {
                list.add(Diff(i))
            }
            return list
        }

        lifecycleScope.launch {
            val firstInterval = getFirstInterval()
            rvAdapter.setFirstDiffs(firstInterval)
        }
    }

    private fun loadNextInterval() {

        fun getNextInterval(): List<Diff> {
            val numCurrentIntervals = rvAdapter.itemCount / NUM_CALCS_INTERVAL
            return if (numCurrentIntervals < MAX_INTERVALS) {
                val list = mutableListOf<Diff>()
                val nextFirstPointsNeeded = NUM_CALCS_INTERVAL * numCurrentIntervals + MIN_POINTS_NEEDED
                for (i in nextFirstPointsNeeded..<nextFirstPointsNeeded + NUM_CALCS_INTERVAL) {
                    list.add(Diff(i))
                }
                list
            } else {
                val myLayoutManager = binding.recyclerViewDiffsCalculator.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = myLayoutManager.findLastVisibleItemPosition()
                if (lastVisibleItemPosition == MAX_ITEMS - 1) {
                    Snackbar.make(binding.root, R.string.i_think_is_enough_jajaja, LENGTH_LONG).show()
                }
                emptyList()
            }
        }

        lifecycleScope.launch {
            val nextInterval = getNextInterval()
            if (nextInterval.isNotEmpty()) {
                rvAdapter.setNextDiffs(nextInterval)
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}