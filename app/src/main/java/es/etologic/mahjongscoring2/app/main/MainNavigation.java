package es.etologic.mahjongscoring2.app.main;

import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;

import es.etologic.mahjongscoring2.R;
import es.etologic.mahjongscoring2.app.combinations.CombinationsFragment;
import es.etologic.mahjongscoring2.app.new_game.NewGameFragment;
import es.etologic.mahjongscoring2.app.old_games.OldGamesFragment;
import es.etologic.mahjongscoring2.app.old_games.OldGamesFragment.IOldGamesFragmentListener;

public class MainNavigation {

    //region Constants

    private static final int NONE = -1;
    private static final int OLD_GAMES = 0;
    private static final int NEW_GAME = 1;
    private static final int COMBINATIONS = 2;

    //endregion

    //region Fields

    private final NavigationView navigationView;
    private IMainActivityListener mainActivityListener;
    private IMainToolbarListener mainToolbarListener;
    private IOldGamesFragmentListener oldGamesFragmentListener;
    private int selectedMenuItem = NONE;

    //endregion

    public MainNavigation(NavigationView navigationView,
                          IMainActivityListener mainActivityListener,
                          IMainToolbarListener mainToolbarListener,
                          IOldGamesFragmentListener oldGamesFragmentListener) {
        this.navigationView = navigationView;
        this.mainActivityListener = mainActivityListener;
        this.mainToolbarListener = mainToolbarListener;
        this.oldGamesFragmentListener = oldGamesFragmentListener;
        setupNavigation();
    }

    void goHome() {
        goToOldGames();
    }

    //region Private

    private void setupNavigation() {
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            mainActivityListener.closeEndDrawer();
            switch (menuItem.getItemId()) {
                case R.id.nav_oldgames:
                    goToOldGames();
                    break;
                case R.id.nav_newgame:
                    goToNewGame();
                    break;
                case R.id.nav_combinations:
                    goToCombinations();
                    break;
                case R.id.nav_greenbook:
                    goToGreenBook();
                    break;
                case R.id.nav_rate:
                    goToRate();
                    break;
                case R.id.nav_contact:
                    goToContact();
                    break;
                default:
                    return false;
            }
            return true;
        });
    }

    private void goToOldGames() {
        if (selectedMenuItem != OLD_GAMES) {
            OldGamesFragment oldGamesFragment = new OldGamesFragment();
            oldGamesFragment.setOldGamesFragmentListener(oldGamesFragmentListener);
            goToFragment(R.id.nav_oldgames, OLD_GAMES, oldGamesFragment);
        } else {
            mainActivityListener.closeEndDrawer();
        }
    }

    void goToNewGame() {
        if (selectedMenuItem != NEW_GAME) {
            NewGameFragment newGameFragment = new NewGameFragment();
            newGameFragment.setMainToolbarListener(mainToolbarListener);
            goToFragment(R.id.nav_newgame, NEW_GAME, newGameFragment);
        } else {
            mainActivityListener.closeEndDrawer();
        }
    }

    private void goToCombinations() {
        if (selectedMenuItem != COMBINATIONS) {
            CombinationsFragment combinationsFragment = new CombinationsFragment();
            combinationsFragment.setMainToolbarListener(mainToolbarListener);
            goToFragment(R.id.nav_combinations, COMBINATIONS, combinationsFragment);
        } else {
            mainActivityListener.closeEndDrawer();
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
        selectedMenuItem = menuOption;
        navigationView.setCheckedItem(navOption);
        mainActivityListener.addFragment(fragment);
    }

    //endregion
}
