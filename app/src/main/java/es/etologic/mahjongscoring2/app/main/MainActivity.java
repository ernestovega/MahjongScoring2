package es.etologic.mahjongscoring2.app.main;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
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
import es.etologic.mahjongscoring2.Injector;
import es.etologic.mahjongscoring2.R;

public class MainActivity extends AppCompatActivity implements IMainToolbarListener {

    //region Fields

    @BindView (R.id.drawerLayoutMain) public DrawerLayout drawerLayout;
    @BindView(R.id.navigationViewMain) NavigationView navigationView;
    private Unbinder unbinder;
    private MainNavigation mainNavigation;

    //endregion

    //region Lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        setupDrawer();
        setupMainNavigation();
        mainNavigation.goHome();
    }

    @Override
    public void onBackPressed() {
        if(!mainNavigation.onBackPressed()) {
            super.onBackPressed();
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
                mainNavigation.hamburgerPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //endregion

    //region IMainActivityListener

    @Override
    public void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        actionBarDrawerToggle.syncState();
        mainNavigation.setDrawerLayout(drawerLayout);
    }

    //endregion

    //region Private

    private void setupDrawer() {
        TextView tvAppVersion = navigationView.getHeaderView(0).findViewById(
                R.id.tvDrawerHeaderAppVersion);
        tvAppVersion.setText(BuildConfig.VERSION_NAME);
    }

    private void setupMainNavigation() {
        mainNavigation = Injector.provideMainNavigation(navigationView,
                getSupportFragmentManager(), this);
    }

    //endregion
}