package es.etologic.mahjongscoring2.app.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.ImageButton;

import es.etologic.mahjongscoring2.BuildConfig;
import es.etologic.mahjongscoring2.R;
import es.etologic.mahjongscoring2.app.combinations.CombinationsActivity;
import es.etologic.mahjongscoring2.app.new_game.NewGameActivity;
import es.etologic.mahjongscoring2.app.old_games.OldGamesFragment;

public class MainNavigation {

    //region Constants

    private static final String GREEN_BOOK_URL = "https://docs.google.com/gview?embedded=true&url=mahjong-europe.org/portal/images/docs/mcr_EN.pdf";
    private static final String GREEN_BOOK_FILENAME = "mcr_EN.pdf";
    private static final String MARKET_URI_BASE = "market://details?id=";
    private static final String PLAY_STORE_URL_BASE = "http://play.google.com/store/apps/details?id=";
    public static final String EMAIL_SUBJECT = "Mahjong Scoring 2";
    public static final String EMAIL_ADDRESS = "mahjongmadrid@gmail.com";

    //endregion

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
        ImageButton imageButton = mainActivity.navigationView.getMenu()
                .findItem(R.id.nav_oldgames).getActionView()
                .findViewById(R.id.ibMainDrawerOldGamesActionLayoutNewGame);
        imageButton.setOnClickListener(v -> goToNewGame());

        mainActivity.navigationView.setNavigationItemSelectedListener(menuItem -> {
            mainActivity.closeEndDrawer();
            switch (menuItem.getItemId()) {
                case R.id.nav_oldgames:
                    goToOldGames();
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
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(GREEN_BOOK_URL));
        mainActivity.startActivity(intent);
    }

    private void goToRate() {
        Uri uriMarket = Uri.parse(MARKET_URI_BASE + BuildConfig.PACKAGE_NAME);
        Intent intent = new Intent(Intent.ACTION_VIEW, uriMarket);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        } else {
            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        }
        try {
            mainActivity.startActivity(intent);
        } catch(Exception e) {
            Uri uriPlayStore = Uri.parse(PLAY_STORE_URL_BASE + BuildConfig.APPLICATION_ID);
            intent = new Intent(Intent.ACTION_VIEW, uriPlayStore);
            mainActivity.startActivity(intent);
        }
    }

    private void goToContact() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { EMAIL_ADDRESS });
        intent.putExtra(Intent.EXTRA_SUBJECT, EMAIL_SUBJECT);
        if (intent.resolveActivity(mainActivity.getPackageManager()) != null) {
            try {
                mainActivity.startActivity(intent);
            } catch(Exception e) {
                Snackbar.make(mainActivity.drawerLayout, R.string.no_email_apps_founded, Snackbar.LENGTH_LONG)
                        .show();
            }
        }
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
