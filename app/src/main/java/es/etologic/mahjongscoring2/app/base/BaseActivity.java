/*
 * Copyright © 2018 LaLiga.
 * All rights reserved.
 */

package es.etologic.mahjongscoring2.app.base;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.DaggerAppCompatActivity;
import es.etologic.mahjongscoring2.R;
import es.etologic.mahjongscoring2.app.model.ShowState;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static es.etologic.mahjongscoring2.app.model.ShowState.SHOW;

@SuppressLint("Registered")
public class BaseActivity extends DaggerAppCompatActivity {

    //FIELDS
    @BindView(R.id.llProgress) LinearLayout llProgress;
    private Unbinder viewUnbinder;

    //LIFECYCLE
    @Override public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        viewUnbinder = ButterKnife.bind(this);
    }
    public void goToFragment(Fragment fragment, @IdRes int frameLayoutContainer) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        fragmentTransaction.replace(frameLayoutContainer, fragment);
        fragmentTransaction.commit();
    }
    public void goToActivity(Intent intent, int requestCode) {
        startActivity/*ForResult*/(intent/*, requestCode*/);
//        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }
    @Override protected void onDestroy() {
        if (viewUnbinder != null) {
            viewUnbinder.unbind();
        }
        super.onDestroy();
    }
    protected void showError(Throwable throwable) {
        showError(throwable == null ? getString(R.string.ups_something_wrong) : throwable.getMessage());
    }
    private void showError(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppTheme);
        builder.setMessage(message)
//                .create()
                .show();
    }
    protected void showSnackbar(View view, String message) {
        String text = StringUtils.isBlank(message) ? "" : message;
        Snackbar.make(view, text, Snackbar.LENGTH_LONG)
                .show();
    }

    protected void toggleProgress(ShowState showState) {
        llProgress.setVisibility((showState == SHOW) ? VISIBLE : GONE);
    }
}
