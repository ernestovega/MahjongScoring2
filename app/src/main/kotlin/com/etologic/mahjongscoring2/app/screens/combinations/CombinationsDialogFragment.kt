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

package com.etologic.mahjongscoring2.app.screens.combinations

import android.app.SearchManager
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.MenuProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.model.ShowState.SHOW
import com.etologic.mahjongscoring2.app.utils.KeyboardUtils.hideKeyboard
import com.etologic.mahjongscoring2.databinding.CombinationsDialogFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class CombinationsDialogFragment : DialogFragment() {

    companion object {
        const val TAG = "CombinationsFragment"
    }

    @Inject
    lateinit var rvAdapter: CombinationsRvAdapter

    private var _binding: CombinationsDialogFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CombinationsViewModel by viewModels()

    private val whiteColor: Int by lazy { ContextCompat.getColor(requireContext(), R.color.white) }

    private val toolbarMenuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.combinations_menu, menu)

            (menu.findItem(R.id.action_search_combination)?.actionView as? SearchView)?.apply {
                findViewById<ImageView>(androidx.appcompat.R.id.search_button)?.setColorFilter(whiteColor)
                findViewById<TextView>(androidx.appcompat.R.id.search_src_text)?.setTextColor(whiteColor)

                val searchManager = getSystemService(requireContext(), SearchManager::class.java)
                setSearchableInfo(searchManager?.getSearchableInfo(requireActivity().componentName))

                setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String): Boolean {
                        viewModel.searchCombination(newText)
                        return true
                    }
                })
            }
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            when (menuItem.itemId) {
                R.id.action_toggle_combination_explanation -> binding.toolbarCombinations.menu.findItem(menuItem.itemId)
                    ?.setIcon(if (rvAdapter.toggleImageOrDescription() === SHOW) R.drawable.ic_books else R.drawable.ic_photos)

                else -> return false
            }
            return true
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = CombinationsDialogFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.FullScreenDialogMM
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar()
        setupRecyclerView()
        startObservingViewModel()
    }

    private fun setToolbar() {
        with(binding.toolbarCombinations) {
            setNavigationOnClickListener { dismiss() }
            addMenuProvider(toolbarMenuProvider, viewLifecycleOwner, Lifecycle.State.RESUMED)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewCombinations.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        binding.recyclerViewCombinations.layoutManager = layoutManager
        val handler = Handler(Looper.getMainLooper())
        handler.post { binding.recyclerViewCombinations.adapter = rvAdapter }
    }

    private fun startObservingViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(STARTED) {
                viewModel.combinationsUiState.collect { uiState ->
                    when (uiState) {
                        is CombinationsUiState.Loading -> {}
                        is CombinationsUiState.Loaded -> {
                            rvAdapter.setCombinations(uiState.combinationsList)
                            binding.emptyLayoutCombinations.visibility = GONE
                        }
                        is CombinationsUiState.Empty -> {
                            rvAdapter.setCombinations(emptyList())
                            binding.emptyLayoutCombinations.visibility = VISIBLE
                        }
                    }
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        binding.root.hideKeyboard()
        super.onDismiss(dialog)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
