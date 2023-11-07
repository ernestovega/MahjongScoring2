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
package com.etologic.mahjongscoring2.app.main.diffs_calculator

import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.base.BaseActivity
import com.etologic.mahjongscoring2.databinding.DiffsCalculatorActivityBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import javax.inject.Inject

class DiffsCalculatorActivity : BaseActivity() {

    companion object {
        const val NUM_CALCS_INTERVAL: Int = 200
        const val MIN_POINTS_NEEDED: Int = 32
        const val MAX_INTERVALS: Int = 10
    }

    private lateinit var binding: DiffsCalculatorActivityBinding

    @Inject
    internal lateinit var rvAdapter: DiffsCalculatorRvAdapter

    override val onBackBehaviour = { finish() }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        binding = DiffsCalculatorActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setupToolbar()
        setupRecyclerView()
        loadFirstInterval()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarDiffsCalculator)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
        lifecycleScope.launch {
            rvAdapter.setFirstDiffs(getFirstInterval())
        }
    }

    private fun getFirstInterval(): List<Diff> {
        val list = mutableListOf<Diff>()
        for (i in MIN_POINTS_NEEDED..<MIN_POINTS_NEEDED + NUM_CALCS_INTERVAL) { list.add(Diff(i)) }
        return list
    }

    private fun loadNextInterval() {
        lifecycleScope.launch {
            rvAdapter.setNextDiffs(getNextInterval())
        }
    }

    private fun getNextInterval(): List<Diff> {
        val numCurrentIntervals = rvAdapter.itemCount / NUM_CALCS_INTERVAL
        return if (numCurrentIntervals < MAX_INTERVALS) {
            val list = mutableListOf<Diff>()
            val nextFirstPointsNeeded = NUM_CALCS_INTERVAL * numCurrentIntervals + MIN_POINTS_NEEDED
            for (i in nextFirstPointsNeeded..<nextFirstPointsNeeded + NUM_CALCS_INTERVAL) {
                list.add(Diff(i))
            }
            list
        } else {
            Snackbar.make(binding.root, R.string.i_think_is_enough_jajaja, LENGTH_LONG).show()
            emptyList()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackBehaviour.invoke()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}