package com.etologic.mahjongscoring2.app.main.combinations

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.base.BaseActivity
import com.etologic.mahjongscoring2.app.model.ShowState.SHOW
import kotlinx.android.synthetic.main.combinations_activity.*
import javax.inject.Inject

class CombinationsActivity : BaseActivity() {
    
    companion object {
        internal const val TAG = "CombinationsActivity"
        internal const val CODE = 2
    }
    
    @Inject
    internal lateinit var combinationsViewModelFactory: CombinationsViewModelFactory
    internal var viewModel: CombinationsViewModel? = null
    
    @Inject
    internal lateinit var rvAdapter: CombinationsRvAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.combinations_activity)
        setupToolbar()
        setupRecyclerView()
        setupViewModel()
        viewModel?.getAll()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(toolbarCombinations)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    
    private fun setupRecyclerView() {
        recyclerViewCombinations?.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        recyclerViewCombinations?.layoutManager = layoutManager
        val handler = Handler()
        handler.post { recyclerViewCombinations?.adapter = rvAdapter }
    }
    
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, combinationsViewModelFactory).get(CombinationsViewModel::class.java)
        viewModel?.getFilteredCombinations()?.observe(this, Observer { rvAdapter.setCombinations(it) })
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
                onBackPressed()
                return true
            }
            R.id.action_toggle_combination_explanation -> {
                val menuItem = toolbarCombinations?.menu?.getItem(1)
                menuItem?.setIcon(
                    if (rvAdapter.toggleImageOrDescription() === SHOW)
                        R.drawable.ic_library_books_white_24dp
                    else
                        R.drawable.ic_photo_library_white_24dp
                )
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
