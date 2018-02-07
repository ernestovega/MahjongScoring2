package es.etologic.mahjongscoring2.app.combinations;

import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import es.etologic.mahjongscoring2.Injector;
import es.etologic.mahjongscoring2.R;
import es.etologic.mahjongscoring2.domain.entities.Combination;

import static es.etologic.mahjongscoring2.app.model.ShowState.SHOW;

public class CombinationsActivity extends AppCompatActivity {

    //region Fields

    @BindView(R.id.toolbarCombinations) Toolbar toolbar;
    @BindView (R.id.recyclerViewCombinations) RecyclerView recyclerView;
    private Unbinder unbinder;
    private CombinationsRvAdapter rvAdapter;
    private CombinationsViewModel viewModel;

    //endregion

    //region Lifecycle

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.combinations_activity);
        unbinder = ButterKnife.bind(this);
        setupToolbar();
        setupRecyclerView();
        setupViewModel();
        observeViewModel();
        viewModel.loadCombinations();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.combinations_menu, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search_combination)
                .getActionView();
        if(searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    viewModel.searchCombination(newText);
                    return true;
                }
            });
        }
        return true;
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    //endregion

    //region Events

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_search_combination:
                //TODO
                return true;
            case R.id.action_toggle_combination_explanation:
                MenuItem menuItem = toolbar.getMenu().getItem(1);
                menuItem.setIcon(rvAdapter.toggleImageOrDescription() == SHOW ?
                        R.drawable.ic_library_books_white_24dp :
                        R.drawable.ic_photo_library_white_24dp);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //endregion

    //region Private

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupViewModel() {
        viewModel = ViewModelProviders
                .of(this, Injector.provideCombinationsViewModelFactory(this))
                .get(CombinationsViewModel.class);
    }

    public void setupRecyclerView() {
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        rvAdapter = new CombinationsRvAdapter();
        recyclerView.setAdapter(rvAdapter);
    }

    private void observeViewModel() {
        viewModel.getCombinations().observe(this, this ::setCombinations);
    }

    private void setCombinations(List<Combination> combinations) {
        rvAdapter.setCombinations(combinations);
    }

    //endregion
}
