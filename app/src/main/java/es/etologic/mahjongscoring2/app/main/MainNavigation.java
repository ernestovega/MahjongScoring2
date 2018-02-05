package es.etologic.mahjongscoring2.app.main;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import es.etologic.mahjongscoring2.R;
import es.etologic.mahjongscoring2.app.combinations.CombinationsActivity;
import es.etologic.mahjongscoring2.app.new_game.NewGameActivity;
import es.etologic.mahjongscoring2.app.old_games.OldGamesFragment;

public class MainNavigation {

    //region Fields

    private final MainActivity mainActivity;

    //endregion

    //region Constructor

    public MainNavigation(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        setupNavigation();
    }

    //endregion

    //region Public

    void goHome() {
        goToOldGames();
    }

    void goToNewGame() {
        Intent intent = new Intent(mainActivity, NewGameActivity.class);
        mainActivity.startActivity(intent);
    }

    //endregion

    //region Private

    private void setupNavigation() {
        mainActivity.navigationView.setNavigationItemSelectedListener(menuItem -> {
            mainActivity.closeEndDrawer();
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
        OldGamesFragment oldGamesFragment = new OldGamesFragment();
        oldGamesFragment.setOldGamesFragmentListener(mainActivity);
        goToFragment(oldGamesFragment);
    }

    private void goToCombinations() {
        Intent intent = new Intent(mainActivity, CombinationsActivity.class);
        mainActivity.startActivity(intent);
    }

    private void goToGreenBook() {

    }

    private void goToRate() {

    }

    private void goToContact() {

    }

    private void goToFragment(Fragment fragment) {
        FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right,
                R.anim.enter_from_right, R.anim.exit_to_left);
        fragmentTransaction.add(R.id.frameLayoutMain, fragment).addToBackStack(null);
        fragmentTransaction.commit();
    }

    //endregion
}
