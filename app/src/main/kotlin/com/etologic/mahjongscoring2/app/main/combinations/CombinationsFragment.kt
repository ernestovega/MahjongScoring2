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
package com.etologic.mahjongscoring2.app.main.combinations

import android.app.SearchManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.base.BaseFragment
import com.etologic.mahjongscoring2.app.main.activity.MainActivity
import com.etologic.mahjongscoring2.app.model.ShowState.SHOW
import com.etologic.mahjongscoring2.databinding.CombinationsFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CombinationsFragment : BaseFragment() {

    companion object {
        const val TAG = "CombinationsFragment"
    }

    private var _binding: CombinationsFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CombinationsViewModel by viewModels()

    @Inject
    lateinit var rvAdapter: CombinationsRvAdapter

    override val menuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.combinations_menu, menu)
            val searchManager = getSystemService(requireContext(), SearchManager::class.java)
            val searchView = menu.findItem(R.id.action_search_combination)?.actionView as? SearchView
            searchView?.setSearchableInfo(searchManager?.getSearchableInfo(activity?.componentName))
            searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    viewModel.searchCombination(newText)
                    return true
                }
            })
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            with(activity as? MainActivity) {
                when (menuItem.itemId) {
                    android.R.id.home -> this?.openDrawer()
                    R.id.action_toggle_combination_explanation -> binding.toolbarCombinations.menu?.findItem(menuItem.itemId)
                        ?.setIcon(if (rvAdapter.toggleImageOrDescription() === SHOW) R.drawable.ic_books else R.drawable.ic_photos)
                    else -> return false
                }
                return true
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = CombinationsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupToolbar() {
        activityViewModel.setToolbar(binding.toolbarCombinations)
    }

    private fun setupRecyclerView() {
        binding.recyclerViewCombinations.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        binding.recyclerViewCombinations.layoutManager = layoutManager
        val handler = Handler(Looper.getMainLooper())
        handler.post { binding.recyclerViewCombinations.adapter = rvAdapter }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.combinationsState.collect(rvAdapter::setCombinations)
            }
        }
    }
}
