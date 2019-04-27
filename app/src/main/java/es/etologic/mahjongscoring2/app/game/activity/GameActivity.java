package es.etologic.mahjongscoring2.app.game.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.FrameLayout;
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
    private LinearLayout pointsDialogLayout, penaltyPointsDialogLayout, playersDialogLayout;

    //LIFECYCLE
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);
        unbinder = ButterKnife.bind(this);
        setupToolbar();
        setupViewModel();
        setupViewPager();
        startGame();
        initPlayersDialogLayout();
        initPointsDialogLayout();
        initPenaltyPointsDialogLayout();
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
            case PLAYERS:
                showPlayersDialog();
                break;
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
    private void showPlayersDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyleMM);
        TextInputEditText tiet1 = (TextInputEditText) ((FrameLayout)((TextInputLayout)playersDialogLayout.getChildAt(0)).getChildAt(0)).getChildAt(0);
        TextInputEditText tiet2 = (TextInputEditText) ((FrameLayout)((TextInputLayout)playersDialogLayout.getChildAt(1)).getChildAt(0)).getChildAt(0);
        TextInputEditText tiet3 = (TextInputEditText) ((FrameLayout)((TextInputLayout)playersDialogLayout.getChildAt(2)).getChildAt(0)).getChildAt(0);
        TextInputEditText tiet4 = (TextInputEditText) ((FrameLayout)((TextInputLayout)playersDialogLayout.getChildAt(3)).getChildAt(0)).getChildAt(0);
        String[] playersNames = activityViewModel.getListNames().getValue();
        assert playersNames != null;
        tiet1.setText(playersNames[0]);
        tiet2.setText(playersNames[1]);
        tiet3.setText(playersNames[2]);
        tiet4.setText(playersNames[3]);
        builder.setTitle(R.string.players_names)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    activityViewModel.onRequestPlayersResponse(tiet1.getText(), tiet2.getText(), tiet3.getText(), tiet4.getText());
                    KeyboardUtils.hideKeyboard(tiet4);
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> KeyboardUtils.hideKeyboard(tiet4))
                .setView(playersDialogLayout)
                .create()
                .show();
        tiet1.requestFocus();
        KeyboardUtils.showKeyboard(tiet1);
    }
    public void showRequestHandPointsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyleMM);
        TextInputEditText tiet = (TextInputEditText) ((FrameLayout)((TextInputLayout)pointsDialogLayout.getChildAt(0)).getChildAt(0)).getChildAt(0);
        builder.setTitle(R.string.hand_points)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    activityViewModel.onRequestHandPointsResponse(tiet.getText());
                    KeyboardUtils.hideKeyboard(tiet);
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                    activityViewModel.onRequestHandPointsCancel();
                    KeyboardUtils.hideKeyboard(tiet);
                })
                .setOnDismissListener(dialog -> tiet.setText(""))
                .setView(pointsDialogLayout)
                .create()
                .show();
        tiet.requestFocus();
        KeyboardUtils.showKeyboard(tiet);
    }
    public void showRequestPenaltyPointsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyleMM);
        TextInputEditText tiet = (TextInputEditText) ((FrameLayout)((TextInputLayout)penaltyPointsDialogLayout.getChildAt(0)).getChildAt(0)).getChildAt(0);
        CheckBox checkbox = (CheckBox) penaltyPointsDialogLayout.getChildAt(1);
        builder .setTitle(R.string.penalty_points)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    activityViewModel.onRequestPenaltyPointsResponse(tiet.getText(), checkbox.isChecked());
                    KeyboardUtils.hideKeyboard(tiet);
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                    activityViewModel.onRequestPenaltyPointsCancel();
                    KeyboardUtils.hideKeyboard(tiet);
                })
                .setOnDismissListener(dialog -> {
                    tiet.setText("");
                    checkbox.setChecked(true);
                })
                .setView(penaltyPointsDialogLayout)
                .create()
                .show();
        tiet.requestFocus();
        KeyboardUtils.showKeyboard(tiet);
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
//    private void gameFinished(Boolean gameFinished) {
//        if (gameFinished != null) {
//            if (gameFinished) finish();
//            else {
//                Snackbar.make(viewPager, R.string.error_updating_end_date, Snackbar.LENGTH_LONG)
//                        .setActionTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
//                        .setAction(R.string.exit, v -> finish())
//                        .show();
//            }
//        }
//    }
    private void startGame() {
        long gameId = getIntent().getLongExtra(ARG_KEY_GAME_ID, -1);
        if (gameId == -1) {
            activityViewModel.createGame();
        } else {
            activityViewModel.loadGame(gameId);
        }
    }
    private void initPlayersDialogLayout() {
        final TextInputEditText tiet1 = new TextInputEditText(this);
        tiet1.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        tiet1.setLines(1);
        tiet1.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) KeyboardUtils.showKeyboard(v); 
            else KeyboardUtils.hideKeyboard(v);
        });
        final TextInputLayout til1 = new TextInputLayout(this);
        til1.setHint(getString(R.string.player_east_name));
        til1.addView(tiet1);

        final TextInputEditText tiet2 = new TextInputEditText(this);
        tiet2.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        tiet2.setLines(1);
        tiet2.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) KeyboardUtils.showKeyboard(v); 
            else KeyboardUtils.hideKeyboard(v);
        });
        final TextInputLayout til2 = new TextInputLayout(this);
        til2.setHint(getString(R.string.player_south_name));
        til2.addView(tiet2);

        final TextInputEditText tiet3 = new TextInputEditText(this);
        tiet3.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        tiet3.setLines(1);
        tiet3.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) KeyboardUtils.showKeyboard(v); 
            else KeyboardUtils.hideKeyboard(v);
        });
        final TextInputLayout til3 = new TextInputLayout(this);
        til3.setHint(getString(R.string.player_west_name));
        til3.addView(tiet3);

        final TextInputEditText tiet4 = new TextInputEditText(this);
        tiet4.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        tiet4.setLines(1);
        tiet4.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) KeyboardUtils.showKeyboard(v); 
            else KeyboardUtils.hideKeyboard(v);
        });
        final TextInputLayout til4 = new TextInputLayout(this);
        til4.setHint(getString(R.string.player_name));
        til4.addView(tiet4);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int margin = getResources().getDimensionPixelSize(R.dimen.standard_margin);
        int half_margin = getResources().getDimensionPixelSize(R.dimen.half_standard_margin);
        layoutParams.setMargins(margin, margin, margin, 0);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            layoutParams.setMarginStart(getResources().getDimensionPixelSize(R.dimen.standard_margin));
            layoutParams.setMarginEnd(margin);
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(layoutParams);
        layout.addView(til1, layoutParams);
        layout.addView(til2, layoutParams);
        layout.addView(til3, layoutParams);
        layout.addView(til4, layoutParams);
        playersDialogLayout = layout;
    }
    private void initPointsDialogLayout() {
        final TextInputEditText tiet = new TextInputEditText(this);
        tiet.setInputType(InputType.TYPE_CLASS_NUMBER);
        InputFilter filter = (source, start, end, dest, dstart, dend) -> {
            for(int i = start; i < end; i++) {
                if(!Character.isDigit(source.charAt(i))) {
                    return "";
                }
            }
            return null;
        };
        tiet.setFilters(new InputFilter[] { filter });
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(4);
        tiet.setFilters(filterArray);
        tiet.setLines(1);
        tiet.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                KeyboardUtils.showKeyboard(v);
            } else {
                KeyboardUtils.hideKeyboard(v);
            }
        });

        final TextInputLayout til = new TextInputLayout(this);
        til.setHint(getString(R.string.enter_points));
        til.addView(tiet);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int margin = getResources().getDimensionPixelSize(R.dimen.standard_margin);
        int half_margin = getResources().getDimensionPixelSize(R.dimen.half_standard_margin);
        layoutParams.setMargins(margin, margin, margin, half_margin);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            layoutParams.setMarginStart(getResources().getDimensionPixelSize(R.dimen.standard_margin));
            layoutParams.setMarginEnd(margin);
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(layoutParams);
        layout.addView(til, layoutParams);
        pointsDialogLayout = layout;
    }
    private void initPenaltyPointsDialogLayout() {
        final TextInputEditText tiet = new TextInputEditText(this);
        tiet.setInputType(InputType.TYPE_CLASS_NUMBER);
        InputFilter filter = (source, start, end, dest, dstart, dend) -> {
            for(int i = start; i < end; i++) {
                if(!Character.isDigit(source.charAt(i))) {
                    return "";
                }
            }
            return null;
        };
        tiet.setFilters(new InputFilter[] { filter });
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(4);
        tiet.setFilters(filterArray);
        tiet.setLines(1);
        tiet.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                KeyboardUtils.showKeyboard(v);
            } else {
                KeyboardUtils.hideKeyboard(v);
            }
        });

        final TextInputLayout til = new TextInputLayout(this);
        til.setHint(getString(R.string.enter_points));
        til.addView(tiet);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int margin = getResources().getDimensionPixelSize(R.dimen.standard_margin);
        int half_margin = getResources().getDimensionPixelSize(R.dimen.half_standard_margin);
        layoutParams.setMargins(margin, margin, margin, half_margin);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            layoutParams.setMarginStart(getResources().getDimensionPixelSize(R.dimen.standard_margin));
            layoutParams.setMarginEnd(margin);
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(layoutParams);
        layout.addView(til, layoutParams);

        CheckBox checkBox = new CheckBox(this);
        checkBox.setText(getString(R.string.divided_equally));
        checkBox.setChecked(true);
        layout.addView(checkBox, layoutParams);
        penaltyPointsDialogLayout = layout;
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
            case R.id.action_players:
                showPlayersDialog();
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
