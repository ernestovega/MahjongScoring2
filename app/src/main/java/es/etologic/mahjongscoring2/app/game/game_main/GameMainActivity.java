package es.etologic.mahjongscoring2.app.game.game_main;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import es.etologic.mahjongscoring2.Injector;
import es.etologic.mahjongscoring2.R;
import es.etologic.mahjongscoring2.domain.entities.Game;

public class GameMainActivity extends AppCompatActivity {

    //region Fields

    @BindView(R.id.toolbarGame) Toolbar toolbar;
    @BindView (R.id.tabLayoutGame) TabLayout tabLayout;
    @BindView (R.id.viewPagerGame) ViewPager viewPager;
    private Unbinder unbinder;
    private GameMainViewModel viewModel;
    private long gameId;

    //endregion

    //region Lifecycle

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_main_activity);
        unbinder = ButterKnife.bind(this);
        getExtras();
        setupToolbar();
        setupViewModel();
        observeViewModel();
        setupViewPager();
        viewModel.loadGame(gameId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.game_menu, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(viewPager.getCurrentItem() == 1) {
            viewPager.setCurrentItem(0, true);
        } else {
            showDialogEndGame();
        }
    }

    private void showDialogEndGame() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.exit_game)
                .setMessage(R.string.are_you_sure)
                .setPositiveButton(R.string.exit, (dialog, which) -> finish()/*viewModel.endGame()*/)
                .setNegativeButton(android.R.string.cancel, null)
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
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
            case R.id.action_play:
                return true;
            case R.id.action_pause:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //endregion

    //region Private

    private void getExtras() {
        gameId = getIntent().getLongExtra(getString(R.string.key_extra_game_id), -1);
        if(gameId == -1) finish();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
        }
    }

    private void setupViewModel() {
        viewModel = ViewModelProviders
                .of(this, Injector.provideGameMainViewModelFactory(this))
                .get(GameMainViewModel.class);
    }

    private void observeViewModel() {
        viewModel.getGame().observe(this, this::setGame);
        viewModel.isGameFinished().observe(this, this:: gameFinished);
    }

    private void setGame(Game game) {
        //TODO
    }

    private void gameFinished(Boolean gameFinished) {
        if(gameFinished != null) {
            if(gameFinished)  finish();
            else {
                Snackbar.make(viewPager, R.string.error_updating_end_date, Snackbar.LENGTH_LONG)
                        .setActionTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .setAction(R.string.exit, v -> finish())
                        .show();
            }
        }
    }

    private void setupViewPager() {
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                if(getSupportActionBar() != null) {
                    getSupportActionBar().setHomeAsUpIndicator(position == 0 ?
                            R.drawable.ic_clear_white_24dp : R.drawable.ic_arrow_back_white_24dp);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        createFragments();
    }

    private void createFragments() {
        //TODO
    }

    //endregion
}
