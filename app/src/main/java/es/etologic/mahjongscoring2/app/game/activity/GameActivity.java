package es.etologic.mahjongscoring2.app.game.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import es.etologic.mahjongscoring2.R;
import es.etologic.mahjongscoring2.app.base.BaseActivity;
import es.etologic.mahjongscoring2.app.base.ViewPagerAdapter;
import es.etologic.mahjongscoring2.app.game.game_list.GameListFragment;
import es.etologic.mahjongscoring2.app.game.game_table.GameTableFragment;
import es.etologic.mahjongscoring2.app.main.combinations.CombinationsActivity;
import es.etologic.mahjongscoring2.app.model.DialogType;
import es.etologic.mahjongscoring2.app.model.EnablingState;
import es.etologic.mahjongscoring2.app.model.GamePages;
import es.etologic.mahjongscoring2.app.model.ToolbarState;
import es.etologic.mahjongscoring2.app.utils.KeyboardUtils;
import es.etologic.mahjongscoring2.app.utils.PreLollipopSoundPool;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class GameActivity extends BaseActivity {

    //CONSTANTS
    public static final String ARG_KEY_GAME_ID = "arg_key_game_id";
    private static final int OFFSCREEN_PAGE_LIMIT = 1;
    //VIEWS
    @BindView(R.id.toolbarGame) Toolbar toolbar;
    @BindView(R.id.tabLayoutGame) TabLayout tabLayout;
    @BindView(R.id.viewPagerGame) ViewPager viewPager;
    //ASSETS
    @BindDrawable(R.drawable.ic_east) Drawable eastIconDrawable;
    @BindDrawable(R.drawable.ic_south) Drawable southIconDrawable;
    @BindDrawable(R.drawable.ic_west) Drawable westIconDrawable;
    @BindDrawable(R.drawable.ic_north) Drawable northIconDrawable;
    //FIELDS
    @Inject GameActivityViewModelFactory viewModelFactory;
    private Unbinder unbinder;
    private GameActivityViewModel viewModel;
    private LinearLayout pointsDialogLayout, penaltyPointsDialogLayout, playersDialogLayout;
    private AlertDialog diceDialog;
    private SoundPool diceSound;       //For dice sound playing
    private int sound_id;               //Used to control sound stream return by SoundPool
    private Handler handler;            //Post message to start roll
    private Timer timer = new Timer();  //Used to implement feedback to user
    private boolean isRolling =false;   //Is die isRolling?
    private ImageView ivDice1, ivDice2;

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
        initSound();
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
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(GameActivityViewModel.class);
        viewModel.getError().observe(this, this::showError);
        viewModel.getLocalProgressState().observe(this, this::toggleProgress);
        viewModel.getViewPagerPagingState().observe(this, this::viewPagerPagingStateObserver);
        viewModel.getShowDialog().observe(this, this::showDialogObserver);
        viewModel.getToolbarState().observe(this, this::toolbarStateObserver);
        viewModel.getCurrentViewPagerPage().observe(this, this::viewPagerPageToSeeObserver);
        viewModel.getEndGameState().observe(this, s -> finish());
    }
    private void viewPagerPageToSeeObserver(GamePages gamePages) {
        if (gamePages != null) {
            viewPager.setCurrentItem(gamePages.getCode());
        }
    }
    private void viewPagerPagingStateObserver(EnablingState enablingState) {
        viewPager.setEnabled(enablingState == EnablingState.ENABLED);
    }
    private void showDialogObserver(DialogType dialogType) {
        switch (dialogType) {
            case PLAYERS_NAMES:
                showPlayersDialog();
                break;
            case ROLL_DICE:
                showRollDiceDialog();
                break;
            case REQUEST_HAND_POINTS:
                showRequestHandPointsDialog();
                break;
            case REQUEST_PENALTY_POINTS:
                showRequestPenaltyPointsDialog();
                break;
            case EXIT:
                showDialogExitGame();
                break;
        }
    }
    private void showPlayersDialog() {
        if (playersDialogLayout.getParent() != null) {
            ((ViewGroup) playersDialogLayout.getParent()).removeView(playersDialogLayout);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyleMM);
        TextInputEditText tiet1 = (TextInputEditText) ((FrameLayout) ((TextInputLayout) playersDialogLayout.getChildAt(0)).getChildAt(0)).getChildAt(0);
        TextInputEditText tiet2 = (TextInputEditText) ((FrameLayout) ((TextInputLayout) playersDialogLayout.getChildAt(1)).getChildAt(0)).getChildAt(0);
        TextInputEditText tiet3 = (TextInputEditText) ((FrameLayout) ((TextInputLayout) playersDialogLayout.getChildAt(2)).getChildAt(0)).getChildAt(0);
        TextInputEditText tiet4 = (TextInputEditText) ((FrameLayout) ((TextInputLayout) playersDialogLayout.getChildAt(3)).getChildAt(0)).getChildAt(0);
        String[] playersNames = viewModel.getListNames().getValue();
        assert playersNames != null;
        tiet1.setText(playersNames[0]);
        tiet2.setText(playersNames[1]);
        tiet3.setText(playersNames[2]);
        tiet4.setText(playersNames[3]);
        builder.setTitle(R.string.players_names)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    viewModel.onRequestPlayersResponse(tiet1.getText(), tiet2.getText(), tiet3.getText(), tiet4.getText());
                    KeyboardUtils.hideKeyboard(tiet4);
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> KeyboardUtils.hideKeyboard(tiet4))
                .setView(playersDialogLayout)
                .create()
                .show();
        tiet1.requestFocus();
        tiet1.selectAll();
        KeyboardUtils.showKeyboard(tiet1);
    }
    private void showRollDiceDialog() {
        if (diceDialog == null) {
            ivDice1 = new ImageView(this);
            ivDice2 = new ImageView(this);
            ivDice1.setImageResource(R.drawable.dice3droll);
            ivDice2.setImageResource(R.drawable.dice3droll);
            HandleClick onClickListener = new HandleClick();
            ivDice1.setOnClickListener(onClickListener);
            ivDice2.setOnClickListener(onClickListener);

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setGravity(Gravity.CENTER);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
            int margin = getResources().getDimensionPixelSize(R.dimen.standard_margin);
            layoutParams.setMargins(margin, margin, margin, margin);
            layout.setLayoutParams(layoutParams);

            LinearLayout.LayoutParams ivDice1Params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            LinearLayout.LayoutParams ivDice2Params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            int marginDice = margin * 6;
            ivDice1Params.setMargins(0, marginDice, margin, marginDice);
            ivDice2Params.setMargins(margin, marginDice, 0, marginDice);
            layout.addView(ivDice1, ivDice1Params);
            layout.addView(ivDice2, ivDice2Params);

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyleMM);
            diceDialog = builder.setTitle("")
                    .setView(layout)
                    .setOnDismissListener(dialog -> diceSound.pause(sound_id))
                    .create();
            diceDialog.setOnShowListener(dialog -> {
                ivDice1.setImageResource(R.drawable.dice3droll);
                ivDice2.setImageResource(R.drawable.dice3droll);
            });
            diceDialog.getWindow().setLayout(300, 300);
            handler = new Handler(callback);
        }
        diceDialog.show();
    }
    private class HandleClick implements View.OnClickListener {
        public void onClick(View arg0) {
            if (!isRolling) {
                isRolling = true;
                //Show isRolling image
                ivDice1.setImageResource(R.drawable.dice3droll);
                ivDice2.setImageResource(R.drawable.dice3droll);

                diceSound.play(sound_id, 1.0f, 1.0f, 0, 0, 1.0f);
                diceSound.play(sound_id, 1.0f, 1.0f, 0, 0, 1.0f);
                timer.schedule(new Roll(), 400);
            }
        }
    }
    void initSound() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes aa = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            diceSound = new SoundPool.Builder().setAudioAttributes(aa).build();
        } else {
            diceSound = PreLollipopSoundPool.NewSoundPool();
        }
        sound_id= diceSound.load(this,R.raw.shake_dice,1);
    }
    //When pause completed message sent to callback
    class Roll extends TimerTask {
        public void run() {
            handler.sendEmptyMessage(0);
        }
    }
    //Receives message from timer to start dice roll
    Handler.Callback callback = msg -> {
        paintNewDice(ivDice1);
        paintNewDice(ivDice2);
        return true;
    };
    private void paintNewDice(ImageView imageView) {
        int randomInt = new Random().nextInt(6) + 1;
        switch (randomInt) {
            case 1: imageView.setImageResource(R.drawable.one); break;
            case 2: imageView.setImageResource(R.drawable.two); break;
            case 3: imageView.setImageResource(R.drawable.three); break;
            case 4: imageView.setImageResource(R.drawable.four); break;
            case 5: imageView.setImageResource(R.drawable.five); break;
            case 6: imageView.setImageResource(R.drawable.six); break;
        }
        isRolling = false;  //user can press again
    }
    private void showRequestHandPointsDialog() {
        if (pointsDialogLayout.getParent() != null) {
            ((ViewGroup) pointsDialogLayout.getParent()).removeView(pointsDialogLayout);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyleMM);
        TextInputEditText tiet = (TextInputEditText) ((FrameLayout) ((TextInputLayout) pointsDialogLayout.getChildAt(0)).getChildAt(0)).getChildAt(0);
        builder.setTitle(R.string.hand_points)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    viewModel.onRequestHandPointsResponse(tiet.getText());
                    KeyboardUtils.hideKeyboard(tiet);
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                    viewModel.onRequestHandPointsCancel();
                    KeyboardUtils.hideKeyboard(tiet);
                })
                .setOnDismissListener(dialog -> tiet.setText(""))
                .setView(pointsDialogLayout)
                .create()
                .show();
        tiet.requestFocus();
        KeyboardUtils.showKeyboard(tiet);
    }
    private void showRequestPenaltyPointsDialog() {
        if (penaltyPointsDialogLayout.getParent() != null) {
            ((ViewGroup) penaltyPointsDialogLayout.getParent()).removeView(penaltyPointsDialogLayout);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyleMM);
        TextInputEditText tiet = (TextInputEditText) ((FrameLayout) ((TextInputLayout) penaltyPointsDialogLayout.getChildAt(0)).getChildAt(0)).getChildAt(0);
        CheckBox checkbox = (CheckBox) penaltyPointsDialogLayout.getChildAt(1);
        builder.setTitle(R.string.penalty_points)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    viewModel.onRequestPenaltyPointsResponse(tiet.getText(), checkbox.isChecked());
                    KeyboardUtils.hideKeyboard(tiet);
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                    viewModel.onRequestPenaltyPointsCancel();
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
    private void showDialogExitGame() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.exit_game)
                .setMessage(R.string.are_you_sure)
                .setPositiveButton(R.string.exit, (dialog, which) -> {
                    viewModel.endGame();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create()
                .show();
    }
    private void toolbarStateObserver(ToolbarState toolbarState) {
        switch (toolbarState) {
            default:
            case NORMAL:
                toolbar.setTitle(getString(R.string.game));
                break;
            case REQUEST_DISCARDER:
                toolbar.setTitle(getString(R.string.select_discarder));
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

                viewModel.setCurrentViewPagerPage(GamePages.getFromCode(position));

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
            viewModel.createGame();
        } else {
            viewModel.loadGame(gameId);
        }
    }
    private void initPlayersDialogLayout() {
        final int MAX_TIET_LINES = 1;
        final int MAX_TIET_CHARS = 16;
        final TextInputEditText tiet1 = new TextInputEditText(this);
        tiet1.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        tiet1.setLines(MAX_TIET_LINES);
        tiet1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_TIET_CHARS)});
        tiet1.setCompoundDrawablesWithIntrinsicBounds(eastIconDrawable, null, null, null);
        tiet1.setCompoundDrawablePadding(MAX_TIET_CHARS);
        tiet1.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) KeyboardUtils.showKeyboard(v);
            else KeyboardUtils.hideKeyboard(v);
        });
        final TextInputLayout til1 = new TextInputLayout(this);
        til1.setHint(getString(R.string.player_east_name));
        til1.addView(tiet1);

        final TextInputEditText tiet2 = new TextInputEditText(this);
        tiet2.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        tiet2.setLines(MAX_TIET_LINES);
        tiet2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_TIET_CHARS)});
        tiet2.setCompoundDrawablesWithIntrinsicBounds(southIconDrawable, null, null, null);
        tiet2.setCompoundDrawablePadding(MAX_TIET_CHARS);
        tiet2.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) KeyboardUtils.showKeyboard(v);
            else KeyboardUtils.hideKeyboard(v);
        });
        final TextInputLayout til2 = new TextInputLayout(this);
        til2.setHint(getString(R.string.player_south_name));
        til2.addView(tiet2);

        final TextInputEditText tiet3 = new TextInputEditText(this);
        tiet3.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        tiet3.setLines(MAX_TIET_LINES);
        tiet3.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_TIET_CHARS)});
        tiet3.setCompoundDrawablesWithIntrinsicBounds(westIconDrawable, null, null, null);
        tiet3.setCompoundDrawablePadding(MAX_TIET_CHARS);
        tiet3.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) KeyboardUtils.showKeyboard(v);
            else KeyboardUtils.hideKeyboard(v);
        });
        final TextInputLayout til3 = new TextInputLayout(this);
        til3.setHint(getString(R.string.player_west_name));
        til3.addView(tiet3);

        final TextInputEditText tiet4 = new TextInputEditText(this);
        tiet4.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        tiet4.setLines(MAX_TIET_LINES);
        tiet4.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_TIET_CHARS)});
        tiet4.setCompoundDrawablesWithIntrinsicBounds(northIconDrawable, null, null, null);
        tiet4.setCompoundDrawablePadding(MAX_TIET_CHARS);
        tiet4.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) KeyboardUtils.showKeyboard(v);
            else KeyboardUtils.hideKeyboard(v);
        });
        final TextInputLayout til4 = new TextInputLayout(this);
        til4.setHint(getString(R.string.player_name));
        til4.addView(tiet4);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        int margin = getResources().getDimensionPixelSize(R.dimen.standard_margin);
        int half_margin = getResources().getDimensionPixelSize(R.dimen.half_standard_margin);
        layoutParams.setMargins(margin, margin, margin, half_margin);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
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
        InputFilter digitsFilter = (source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                if (!Character.isDigit(source.charAt(i))) {
                    return "";
                }
            }
            return null;
        };
        InputFilter.LengthFilter lengthFilter = new InputFilter.LengthFilter(4);
        tiet.setFilters(new InputFilter[]{digitsFilter, lengthFilter});
        tiet.setLines(1);
        tiet.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                KeyboardUtils.showKeyboard(v);
            } else {
                KeyboardUtils.hideKeyboard(v);
            }
        });

        final TextInputLayout til = new TextInputLayout(this);
        til.setHint(getString(R.string.enter_points));
        til.addView(tiet);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        int margin = getResources().getDimensionPixelSize(R.dimen.standard_margin);
        int half_margin = getResources().getDimensionPixelSize(R.dimen.half_standard_margin);
        layoutParams.setMargins(margin, margin, margin, half_margin);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
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
            for (int i = start; i < end; i++) {
                if (!Character.isDigit(source.charAt(i))) {
                    return "";
                }
            }
            return null;
        };
        tiet.setFilters(new InputFilter[]{filter});
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(4);
        tiet.setFilters(filterArray);
        tiet.setLines(1);
        tiet.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                KeyboardUtils.showKeyboard(v);
            } else {
                KeyboardUtils.hideKeyboard(v);
            }
        });

        final TextInputLayout til = new TextInputLayout(this);
        til.setHint(getString(R.string.enter_points));
        til.addView(tiet);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        int margin = getResources().getDimensionPixelSize(R.dimen.standard_margin);
        int half_margin = getResources().getDimensionPixelSize(R.dimen.half_standard_margin);
        layoutParams.setMargins(margin, margin, margin, half_margin);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
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
            case R.id.action_combinations:
                startActivity(new Intent(this, CombinationsActivity.class));
                return true;
            case R.id.action_players:
                showPlayersDialog();
                return true;
            //            case R.id.action_play:
            //            case R.id.action_pause:
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed() {
        viewModel.onBackPressed();
    }
    @Override
    protected void onDestroy() {
        unbinder.unbind();
        diceSound.release();
        timer.cancel();
        super.onDestroy();
    }
}
