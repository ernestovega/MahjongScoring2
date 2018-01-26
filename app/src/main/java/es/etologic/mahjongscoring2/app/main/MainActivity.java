package es.etologic.mahjongscoring2.app.main;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import es.etologic.mahjongscoring2.BuildConfig;
import es.etologic.mahjongscoring2.R;
import es.etologic.mahjongscoring2.app.combinations.CombinationsFragment;
import es.etologic.mahjongscoring2.app.new_game.NewGameFragment;
import es.etologic.mahjongscoring2.app.old_games.OldGamesFragment;

public class MainActivity extends AppCompatActivity {

    //region Constants

    private static final int NONE = -1;
    private static final int OLD_GAMES = 0;
    private static final int NEW_GAME = 1;
    private static final int COMBINATIONS = 2;
    private static final int GREEN_BOOK = 3;
    private static final int RATE = 4;
    private static final int CONTACT = 5;
    private static final long BACK_PRESSED_TIME = 4000;

    //endregion

    //region Fields

    @BindView (R.id.dlMain) DrawerLayout drawerLayout;
    @BindView (R.id.tMain) Toolbar toolbar;
    @BindView(R.id.nvMain) NavigationView navigationView;
    private Unbinder unbinder;
    private int checkedDrawerItem = NONE;
    private long lastBackPress;

    //endregion

    //region Lifecycle


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        setupToolbar();
        setupDrawer();
        goToOldGames();
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(drawerLayout)) {
            closeEndDrawer();
        } else {
            long currentTimeMillis = System.currentTimeMillis();
            if((currentTimeMillis - lastBackPress) > BACK_PRESSED_TIME) {
                Snackbar.make(drawerLayout, R.string.press_again_to_exit, Snackbar.LENGTH_LONG)
                        .show();
                lastBackPress = currentTimeMillis;
            } else {
                super.onBackPressed();
            }
        }
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
                openEndDrawer();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //endregion

    //region Private

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        actionBarDrawerToggle.syncState();
    }

    private void setupDrawer() {
        setupDrawerHeader();
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            closeEndDrawer();
            switch (menuItem.getItemId()) {
                case R.id.nav_oldgames:
                    goToOldGames(); break;
                case R.id.nav_newgame:
                    goToNewGame(); break;
                case R.id.nav_combinations:
                    goToCombinations(); break;
                case R.id.nav_greenbook:
                    goToGreenBook(); break;
                case R.id.nav_rate:
                    goToRate(); break;
                case R.id.nav_contact:
                    goToContact(); break;
                default:
                    return false;
            }
            return true;
        });
    }

    private void setupDrawerHeader() {
        TextView tvAppVersion = navigationView.getHeaderView(0)
                .findViewById(R.id.tvDrawerHeaderAppVersion);
        tvAppVersion.setText(BuildConfig.VERSION_NAME);
    }

    private void goToOldGames() {
        if(checkedDrawerItem != OLD_GAMES) {
            goToFragment(R.string.old_games, R.id.nav_oldgames, OLD_GAMES, new OldGamesFragment());
        } else {
            closeEndDrawer();
        }
    }

    private void goToNewGame() {
        if(checkedDrawerItem != NEW_GAME) {
            goToFragment(R.string.new_game, R.id.nav_newgame, NEW_GAME, new NewGameFragment());
        } else {
            closeEndDrawer();
        }
    }

    private void goToCombinations() {
        if(checkedDrawerItem != COMBINATIONS) {
            goToFragment(R.string.combinations, R.id.nav_combinations, COMBINATIONS,
                    new CombinationsFragment());
        } else {
            closeEndDrawer();
        }
    }

    private void goToGreenBook() {

    }

    private void goToRate() {

    }

    private void goToContact() {

    }

    private void goToFragment(@StringRes int toolbarTitle, @IdRes int navOption, int menuOption,
                              Fragment fragment) {
        toolbar.setTitle(toolbarTitle);
        checkedDrawerItem = menuOption;
        navigationView.setCheckedItem(navOption);
        replaceFragment(fragment);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right,
                R.anim.enter_from_right, R.anim.exit_to_left);
        fragmentTransaction.replace(R.id.flMain, fragment);
        fragmentTransaction.commit();
    }

    private void openEndDrawer() {
        drawerLayout.openDrawer(GravityCompat.END, true);
    }

    private void closeEndDrawer() {
        drawerLayout.closeDrawer(GravityCompat.END, true);
    }

    //endregion
}