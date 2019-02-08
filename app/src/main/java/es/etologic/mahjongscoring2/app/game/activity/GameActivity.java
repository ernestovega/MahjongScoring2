package es.etologic.mahjongscoring2.app.game.activity;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import es.etologic.mahjongscoring2.R;
import es.etologic.mahjongscoring2.app.base.BaseActivity;
import es.etologic.mahjongscoring2.app.base.ViewPagerAdapter;
import es.etologic.mahjongscoring2.app.game.game_list.GameListFragment;
import es.etologic.mahjongscoring2.app.game.game_table.GameTableFragment;
import es.etologic.mahjongscoring2.app.model.DialogType;
import es.etologic.mahjongscoring2.app.model.EnablingState;
import es.etologic.mahjongscoring2.app.model.ShowState;
import es.etologic.mahjongscoring2.app.model.ToolbarState;
import es.etologic.mahjongscoring2.domain.model.enums.FabMenuStates;

public class GameActivity extends BaseActivity {

    //CONSTANTS
    public static final String ARG_KEY_GAME_ID = "arg_key_game_id";
    private static final int OFFSCREEN_PAGE_LIMIT = 1;
    private static final int INDEX_TABLE = 0;
    private static final int INDEX_LIST = 1;
    //VIEWS
    @BindView(R.id.toolbarGame) Toolbar toolbar;
    @BindView(R.id.tabLayoutGame) TabLayout tabLayout;
    @BindView(R.id.viewPagerGame) ViewPager viewPager;
    //FIELDS
    @Inject GameActivityViewModelFactory viewModelFactory;
    private Unbinder unbinder;
    private GameActivityViewModel activityViewModel;

    //LIFECYCLE
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);
        unbinder = ButterKnife.bind(this);
        setupToolbar();
        setupViewModel();
        getExtras();
        setupViewPager();
        activityViewModel.loadGame();
    }
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
        }
    }
    private void setupViewModel() {
        activityViewModel = ViewModelProviders.of(this, viewModelFactory).get(GameActivityViewModel.class);
        activityViewModel.getError().observe(this, this::showError);
        activityViewModel.getProgressState().observe(this, this::toggleProgress);
        activityViewModel.getViewPagerPagingState().observe(this, this::viewPagerPagingStateObserver);
        activityViewModel.getShowDialog().observe(this, this::showDialogObserver);
        activityViewModel.getToolbarState().observe(this, this::toolbarStateObserver);
    }
    private void viewPagerPagingStateObserver(EnablingState enablingState) {
        viewPager.setEnabled(enablingState == EnablingState.ENABLED);
    }
    private void showDialogObserver(DialogType dialogType) {
        switch (dialogType) {
            case REQUEST_POINTS_HAND:
                break;
            case REQUEST_POINTS_PENALTI:
                break;
            case SHOW_RANKING:
                break;
            default:
                break;
        }
    }
    private void toolbarStateObserver(ToolbarState toolbarState) {
        switch (toolbarState) {
            default:
            case NORMAL:
                toolbar.setTitle(getString(R.string.game));
                break;
            case REQUEST_LOOSER:
                toolbar.setTitle(getString(R.string.select_looser_player));
                break;
        }
    }
    private void gameFinished(Boolean gameFinished) {
        if (gameFinished != null) {
            if (gameFinished) finish();
            else {
                Snackbar.make(viewPager, R.string.error_updating_end_date, Snackbar.LENGTH_LONG)
                        .setActionTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .setAction(R.string.exit, v -> finish())
                        .show();
            }
        }
    }
    private void getExtras() {
        long gameId = getIntent().getLongExtra(ARG_KEY_GAME_ID, -1);
        if (gameId == -1) {
            finish();
        } else {
            activityViewModel.setGameId(gameId);
        }
    }
    private void setupViewPager() {
        ViewPagerAdapter vpAdapter = initAdapter();
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(OFFSCREEN_PAGE_LIMIT);
        viewPager.setAdapter(vpAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
        @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
        @Override public void onPageScrollStateChanged(int state) {}
        @Override public void onPageSelected(int position) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeAsUpIndicator(position == 0 ?
                        R.drawable.ic_clear_white_24dp : R.drawable.ic_arrow_back_white_24dp);
            }
        }

    });
}
    private ViewPagerAdapter initAdapter() {
        GameTableFragment gameTableFragment = new GameTableFragment();
        GameListFragment gameListFragment = new GameListFragment();

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(gameTableFragment, getString(R.string.table));
        adapter.addFragment(gameListFragment, getString(R.string.list));

        return adapter;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.game_menu, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_play:
                return true;
            case R.id.action_pause:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == INDEX_LIST) {
            viewPager.setCurrentItem(INDEX_TABLE, true);
        } else {
            showDialogEndGame();
        }
    }
    private void showDialogEndGame() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.exit_game)
                .setMessage(R.string.are_you_sure)
                .setPositiveButton(R.string.exit, (dialog, which) -> finish()/*activityViewModel.endGame()*/)
                .setNegativeButton(android.R.string.cancel, null)
                .create()
                .show();
    }
    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
