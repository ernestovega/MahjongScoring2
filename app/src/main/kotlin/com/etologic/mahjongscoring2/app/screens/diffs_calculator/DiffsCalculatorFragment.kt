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
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.base.BaseMainFragment
import com.etologic.mahjongscoring2.app.screens.diffs_calculator.DiffsCalculatorViewModel.Companion.MAX_ITEMS
import com.etologic.mahjongscoring2.databinding.DiffsCalculatorFragmentBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DiffsCalculatorFragment : BaseMainFragment() {

    companion object Companion {
        const val TAG = "DiffsCalculatorFragment"
        private const val ITEM_NUMBER_THRESHOLD_TO_LOAD_NEXT_INTERVAL: Int = 10
    }

    @Inject
    lateinit var rvAdapter: DiffsCalculatorRvAdapter

    private var _binding: DiffsCalculatorFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DiffsCalculatorViewModel by viewModels()

    private val maxReachedSnackBar by lazy { Snackbar.make(binding.root, R.string.i_think_is_enough_jajaja, LENGTH_LONG) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DiffsCalculatorFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        startObservingViewModel()
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
                    val lastCompletelyVisibleItemPosition = myLayoutManager.findLastCompletelyVisibleItemPosition()
                    val totalItemCount = myLayoutManager.itemCount
                    if (lastCompletelyVisibleItemPosition == MAX_ITEMS - 1) {
                        val isScrollingDown = dy > 0
                        if (isScrollingDown && maxReachedSnackBar.isShownOrQueued.not()) {
                            maxReachedSnackBar.show()
                        }
                    } else if (lastCompletelyVisibleItemPosition >= totalItemCount - ITEM_NUMBER_THRESHOLD_TO_LOAD_NEXT_INTERVAL) {
                        viewModel.loadNextInterval()
                    }
                }
            })
        }
    }

    private fun startObservingViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.diffsCalculatorUiState.collect { uiState ->
                    when (uiState) {
                        is DiffsCalculatorUiState.Loading -> {}
                        is DiffsCalculatorUiState.Loaded -> rvAdapter.setDiffs(uiState.diffsList)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}