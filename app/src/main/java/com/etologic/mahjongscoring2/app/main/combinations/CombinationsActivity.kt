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

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.base.BaseActivity
import com.etologic.mahjongscoring2.app.model.ShowState.SHOW
import com.etologic.mahjongscoring2.databinding.CombinationsActivityBinding
import javax.inject.Inject

class CombinationsActivity : BaseActivity() {

    private lateinit var binding: CombinationsActivityBinding

    @Inject
    internal lateinit var combinationsViewModelFactory: CombinationsViewModelFactory
    internal var viewModel: CombinationsViewModel? = null

    @Inject
    internal lateinit var rvAdapter: CombinationsRvAdapter

    override val onBackBehaviour = { finish() }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        binding = CombinationsActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setupToolbar()
        setupRecyclerView()
        setupViewModel()
        viewModel?.getAll()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarCombinations)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupRecyclerView() {
        binding.recyclerViewCombinations.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerViewCombinations.layoutManager = layoutManager
        val handler = Handler(Looper.getMainLooper())
        handler.post { binding.recyclerViewCombinations.adapter = rvAdapter }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, combinationsViewModelFactory)[CombinationsViewModel::class.java]
        viewModel?.getFilteredCombinations()?.observe(this) { rvAdapter.setCombinations(it) }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.combinations_menu, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.action_search_combination).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel?.searchCombination(newText)
                return true
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

            R.id.action_toggle_combination_explanation -> {
                val menuItem = binding.toolbarCombinations.menu?.getItem(1)
                menuItem?.setIcon(
                    if (rvAdapter.toggleImageOrDescription() === SHOW)
                        R.drawable.ic_photo_library_white_24dp
                    else
                        R.drawable.ic_library_books_white_24dp
                )
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }
}
