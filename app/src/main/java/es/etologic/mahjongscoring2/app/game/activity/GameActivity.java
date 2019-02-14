package es.etologic.mahjongscoring2.app.game.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

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
import es.etologic.mahjongscoring2.app.model.GamePages;
import es.etologic.mahjongscoring2.app.model.ToolbarState;
import es.etologic.mahjongscoring2.app.utils.KeyboardUtils;

import static es.etologic.mahjongscoring2.app.model.GamePages.LIST;
import static es.etologic.mahjongscoring2.app.model.GamePages.TABLE;

public class GameActivity extends BaseActivity {

    //CONSTANTS
    public static final String ARG_KEY_GAME_ID = "arg_key_game_id";
    private static final int OFFSCREEN_PAGE_LIMIT = 1;
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
        activityViewModel.getLocalProgressState().observe(this, this::toggleProgress);
        activityViewModel.getViewPagerPagingState().observe(this, this::viewPagerPagingStateObserver);
        activityViewModel.getShowDialog().observe(this, this::showDialogObserver);
        activityViewModel.getToolbarState().observe(this, this::toolbarStateObserver);
        activityViewModel.getPageToSee().observe(this, this::pageToSeeObserver);
    }
    private void pageToSeeObserver(GamePages gamePages) {
        if (gamePages != null) {
            viewPager.setCurrentItem(gamePages.getCode());
        }
    }
    private void viewPagerPagingStateObserver(EnablingState enablingState) {
        viewPager.setEnabled(enablingState == EnablingState.ENABLED);
    }
    private void showDialogObserver(DialogType dialogType) {
        switch (dialogType) {
            case REQUEST_HAND_POINTS:
                showRequestHandPointsDialog();
                break;
            case REQUEST_PENALTY_POINTS:
                showRequestPenaltyPointsDialog();
                break;
            case SHOW_RANKING:

                break;
            default:
                break;
        }
    }
    public void showRequestHandPointsDialog() {
        final LinearLayout linearLayout = getPointsDialogEditTextPoints();
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyleMM);
        EditText childAt = (EditText) linearLayout.getChildAt(0);
        builder.setTitle(R.string.hand_points)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    activityViewModel.onRequestHandPointsResponse(childAt.getText().toString());
                    KeyboardUtils.hideKeyboard(childAt);
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                    activityViewModel.onRequestHandPointsCancel();
                    KeyboardUtils.hideKeyboard(childAt);
                })
                .setView(linearLayout)
                .create()
                .show();
        childAt.requestFocus();
        KeyboardUtils.showKeyboard(childAt);
    }
    public void showRequestPenaltyPointsDialog() {
        final LinearLayout linearLayout = getPointsDialogEditTextPoints();
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyleMM);
        EditText childAt = (EditText) linearLayout.getChildAt(0);
        builder .setTitle(R.string.penalty_points)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    activityViewModel.onRequestPenaltyPointsResponse(childAt.getText().toString());
                    KeyboardUtils.hideKeyboard(childAt);
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                    activityViewModel.onRequestPenaltyPointsCancel();
                    KeyboardUtils.hideKeyboard(childAt);
                })
                .setView(linearLayout)
                .create()
                .show();
        childAt.requestFocus();
        KeyboardUtils.showKeyboard(childAt);
    }
    private LinearLayout getPointsDialogEditTextPoints() {
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        InputFilter filter = (source, start, end, dest, dstart, dend) -> {
            for(int i = start; i < end; i++) {
                if(!Character.isDigit(source.charAt(i))) {
                    return "";
                }
            }
            return null;
        };
        editText.setFilters(new InputFilter[] { filter });
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(4);
        editText.setFilters(filterArray);
        editText.setLines(1);
        editText.setHint(R.string.enter_points);
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                KeyboardUtils.showKeyboard(v);
            } else {
                KeyboardUtils.hideKeyboard(v);
            }
        });
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int margin = getResources().getDimensionPixelSize(R.dimen.standard_margin);
        layoutParams.setMargins(margin, 0, margin, 0);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            layoutParams.setMarginStart(getResources().getDimensionPixelSize(R.dimen.standard_margin));
            layoutParams.setMarginEnd(margin);
        }
        layout.setLayoutParams(layoutParams);
        layout.addView(editText, layoutParams);
        return layout;
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
        if (viewPager.getCurrentItem() == LIST.getCode()) {
            viewPager.setCurrentItem(TABLE.getCode(), true);
        } else {
            showDialogExitGame();
        }
    }
    private void showDialogExitGame() {
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
