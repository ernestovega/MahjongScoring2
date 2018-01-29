package es.etologic.mahjongscoring2.app.main;

import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;

import es.etologic.mahjongscoring2.R;
import es.etologic.mahjongscoring2.app.combinations.CombinationsFragment;
import es.etologic.mahjongscoring2.app.new_game.NewGameFragment;
import es.etologic.mahjongscoring2.app.old_games.OldGamesFragment;

public class MainNavigation {

    //region Constants

    private static final int NONE = -1;
    private static final int OLD_GAMES = 0;
    private static final int NEW_GAME = 1;
    private static final int COMBINATIONS = 2;
    private static final int GREEN_BOOK = 3;
    private static final int RATE = 4;
    private static final int CONTACT = 5;

    //endregion

    //region Fields

    private final NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private FragmentManager fragmentManager;
    private IMainToolbarListener iMainToolbarListener;
    private int checkedDrawerItem = NONE;
    private long lastBackPress;

    //endregion

    public MainNavigation(NavigationView navigationView, FragmentManager fragmentManager,
                          IMainToolbarListener iMainToolbarListener) {
        this.navigationView = navigationView;
        this.fragmentManager = fragmentManager;
        this.iMainToolbarListener = iMainToolbarListener;
        setupNavigation();
    }

    void setDrawerLayout(DrawerLayout drawerLayout) {
        this.drawerLayout = drawerLayout;
    }

    void hamburgerPressed() {
        openEndDrawer();
    }

    boolean onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(Gravity.END)) {
            closeEndDrawer();
        } else {
            long currentTimeMillis = System.currentTimeMillis();
            if ((currentTimeMillis - lastBackPress) > Snackbar.LENGTH_LONG) {
                Snackbar.make(navigationView, R.string.press_again_to_exit, Snackbar.LENGTH_LONG)
                        .show();
                lastBackPress = currentTimeMillis;
            } else {
                return false;
            }
        }
        return true;
    }

    void goHome() {
        goToOldGames();
    }

    //region Private

    private void setupNavigation() {
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            MainNavigation.this.closeEndDrawer();
            switch (menuItem.getItemId()) {
                case R.id.nav_oldgames:
                    MainNavigation.this.goToOldGames();
                    break;
                case R.id.nav_newgame:
                    MainNavigation.this.goToNewGame();
                    break;
                case R.id.nav_combinations:
                    MainNavigation.this.goToCombinations();
                    break;
                case R.id.nav_greenbook:
                    MainNavigation.this.goToGreenBook();
                    break;
                case R.id.nav_rate:
                    MainNavigation.this.goToRate();
                    break;
                case R.id.nav_contact:
                    MainNavigation.this.goToContact();
                    break;
                default:
                    return false;
            }
            return true;
        });
    }

    private void goToOldGames() {
        if (checkedDrawerItem != OLD_GAMES) {
            OldGamesFragment oldGamesFragment = new OldGamesFragment();
            oldGamesFragment.setMainToolbarListener(iMainToolbarListener);
            goToFragment(R.id.nav_oldgames, OLD_GAMES, oldGamesFragment);
        } else {
            closeEndDrawer();
        }
    }

    private void goToNewGame() {
        if (checkedDrawerItem != NEW_GAME) {
            NewGameFragment newGameFragment = new NewGameFragment();
            newGameFragment.setMainToolbarListener(iMainToolbarListener);
            goToFragment(R.id.nav_newgame, NEW_GAME, new NewGameFragment());
        } else {
            closeEndDrawer();
        }
    }

    private void goToCombinations() {
        if (checkedDrawerItem != COMBINATIONS) {
            goToFragment(R.id.nav_combinations, COMBINATIONS,
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

    private void goToFragment(@IdRes int navOption, int menuOption,
                              Fragment fragment) {
        checkedDrawerItem = menuOption;
        navigationView.setCheckedItem(navOption);
        replaceFragment(fragment);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right,
                R.anim.enter_from_right, R.anim.exit_to_left);
        fragmentTransaction.replace(R.id.frameLayoutMain, fragment);
        fragmentTransaction.commit();
    }

    private void openEndDrawer() {
        if (drawerLayout != null) {
            drawerLayout.openDrawer(Gravity.END, true);
        }
    }

    private void closeEndDrawer() {
        if (drawerLayout != null) {
            drawerLayout.closeDrawer(Gravity.END, true);
        }
    }

    //endregion
}
