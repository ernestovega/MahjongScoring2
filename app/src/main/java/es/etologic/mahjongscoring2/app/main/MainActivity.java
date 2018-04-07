package es.etologic.mahjongscoring2.app.main;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import es.etologic.mahjongscoring2.BuildConfig;
import es.etologic.mahjongscoring2.Injector;
import es.etologic.mahjongscoring2.R;
import es.etologic.mahjongscoring2.app.combinations.CombinationsActivity;
import es.etologic.mahjongscoring2.app.game.game_main.GameMainActivity;
import es.etologic.mahjongscoring2.app.model.MainScreens;
import es.etologic.mahjongscoring2.app.new_game.NewGameActivity;
import es.etologic.mahjongscoring2.app.old_games.OldGamesFragment;

import static es.etologic.mahjongscoring2.app.model.MainScreens.COMBINATIONS;
import static es.etologic.mahjongscoring2.app.model.MainScreens.CONTACT;
import static es.etologic.mahjongscoring2.app.model.MainScreens.GREEN_BOOK;
import static es.etologic.mahjongscoring2.app.model.MainScreens.OLD_GAMES;
import static es.etologic.mahjongscoring2.app.model.MainScreens.RATE;

public class MainActivity extends AppCompatActivity {

    //CONSTANTS
    private static final String GREEN_BOOK_URL = "https://docs.google.com/gview?embedded=true&url=mahjong-europe.org/portal/images/docs/mcr_EN.pdf";
    private static final String MARKET_URI_BASE = "market://details?id=";
    private static final String PLAY_STORE_URL_BASE = "http://play.google.com/store/apps/details?id=";
    private static final String EMAIL_SUBJECT = "Mahjong Scoring 2";
    private static final String EMAIL_ADDRESS = "mahjongmadrid@gmail.com";
    private static final long LAST_BACKPRESSED_MIN_TIME = 2000;
    //VIEWS
    @BindView (R.id.drawerLayoutMain) public DrawerLayout drawerLayout;
    @BindView(R.id.navigationViewMain) NavigationView navigationView;
    //FIELDS
    private Unbinder unbinder;
    private MainActivityViewModel viewModel;
    private long lastBackPress;

    //LIFECYCLE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        unbinder = ButterKnife.bind(this);
        initializeViewModel();
        setupDrawer();
        viewModel.goToScreen(OLD_GAMES);
    }
    private void initializeViewModel() {
        viewModel = ViewModelProviders.of(this, Injector.provideMainActivityViewModelFactory()).get(MainActivityViewModel.class);
        viewModel.getCurrentScreen().observe(this, this::goToScreen);
        viewModel.getCurrentGame().observe(this, this::goToGame);
        viewModel.getToolbar().observe(this, this::setToolbar);
    }
    private void goToScreen(MainScreens screen) {
        switch (screen){
            default:
            case OLD_GAMES:
                goToOldGames();
                break;
            case NEW_GAME:
                goToNewGame();
                break;
            case COMBINATIONS:
                goToCombinations();
                break;
            case GREEN_BOOK:
                goToGreenBook();
                break;
            case RATE:
                goToRate();
                break;
            case CONTACT:
                goToContact();
                break;
            case BACK:
                onBackPressed();
                break;
        }
    }
    private void goToOldGames() {
        OldGamesFragment oldGamesFragment = new OldGamesFragment();
        goToFragment(oldGamesFragment);
    }
    private void goToNewGame() {
        Intent intent = new Intent(this, NewGameActivity.class);
        startActivity(intent);
    }
    private void goToCombinations() {
        Intent intent = new Intent(this, CombinationsActivity.class);
        startActivity(intent);
    }
    private void goToGreenBook() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(GREEN_BOOK_URL));
        startActivity(intent);
    }
    private void goToRate() {
        Uri uriMarket = Uri.parse(MARKET_URI_BASE + BuildConfig.PACKAGE_NAME);
        Intent intent = new Intent(Intent.ACTION_VIEW, uriMarket);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        }
        try {
            this.startActivity(intent);
        } catch(Exception e) {
            Uri uriPlayStore = Uri.parse(PLAY_STORE_URL_BASE + BuildConfig.APPLICATION_ID);
            intent = new Intent(Intent.ACTION_VIEW, uriPlayStore);
            startActivity(intent);
        }
    }
    private void goToContact() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { EMAIL_ADDRESS });
        intent.putExtra(Intent.EXTRA_SUBJECT, EMAIL_SUBJECT);
        if (intent.resolveActivity(this.getPackageManager()) != null) {
            try {
                this.startActivity(intent);
            } catch(Exception e) {
                Snackbar.make(this.drawerLayout, R.string.no_email_apps_founded, Snackbar.LENGTH_LONG).show();
            }
        }
    }
    private void goToFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        fragmentTransaction.add(R.id.frameLayoutMain, fragment).addToBackStack(null);
        fragmentTransaction.commit();
    }
    private void goToGame(long gameId) {
        Intent intent = new Intent(this, GameMainActivity.class);
        intent.putExtra(getString(R.string.key_extra_game_id), gameId);
        startActivity(intent);
    }
    private void setupDrawer() {
        setAppVersion();
        setOldGamesActionNewGameListener();
        setMenuItemSelectedListener();
    }
    private void setAppVersion() {
        TextView tvAppVersion = navigationView.getHeaderView(0).findViewById(R.id.tvDrawerHeaderAppVersion);
        tvAppVersion.setText(BuildConfig.VERSION_NAME);
    }
    private void setOldGamesActionNewGameListener() {
        MenuItem oldGamesItem = navigationView.getMenu().findItem(R.id.nav_oldgames);
        oldGamesItem.getActionView().findViewById(R.id.ibMainDrawerOldGamesActionLayoutNewGame).setOnClickListener(v -> goToNewGame());
    }
    private void setMenuItemSelectedListener() {
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            this.closeEndDrawer();
            switch (menuItem.getItemId()) {
                case R.id.nav_oldgames:
                    viewModel.goToScreen(OLD_GAMES);
                    break;
                case R.id.nav_combinations:
                    viewModel.goToScreen(COMBINATIONS);
                    break;
                case R.id.nav_greenbook:
                    viewModel.goToScreen(GREEN_BOOK);
                    break;
                case R.id.nav_rate:
                    viewModel.goToScreen(RATE);
                    break;
                case R.id.nav_contact:
                    viewModel.goToScreen(CONTACT);
                    break;
                default:
                    return false;
            }
            return true;
        });
    }
    private void closeEndDrawer() {
        if (drawerLayout != null) {
            drawerLayout.closeDrawer(Gravity.END, true);
        }
    }
    private void setToolbar(Toolbar toolbar)  {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        actionBarDrawerToggle.syncState();
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.END)) {
            closeEndDrawer();
        } else if(getSupportFragmentManager().getBackStackEntryCount() > 1) {
            super.onBackPressed();
        } else {
            long currentTimeMillis = System.currentTimeMillis();
            if ((currentTimeMillis - lastBackPress) > LAST_BACKPRESSED_MIN_TIME) {
                Snackbar.make(navigationView, R.string.press_again_to_exit, Snackbar.LENGTH_LONG).show();
                lastBackPress = currentTimeMillis;
            } else finish();
        }
    }
    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
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
    private void openEndDrawer() {
        if (drawerLayout != null) {
            drawerLayout.openDrawer(Gravity.END, true);
        }
    }
}