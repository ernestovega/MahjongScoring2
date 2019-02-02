package es.etologic.mahjongscoring2.app.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.DaggerFragment;

public abstract class BaseFragment extends DaggerFragment {

    //FIELDS
    @Inject protected Context context;
    private Unbinder viewUnbinder;

    //LIFECYCLE
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewUnbinder = ButterKnife.bind(this, view);
    }
    protected void showError(Throwable throwable) {
        Activity activity = getActivity();
        if(activity instanceof BaseActivity) {
            ((BaseActivity) activity).showError(throwable);
        }
    }
    protected void showSnackbar(View view, String message) {
        Activity activity = getActivity();
        if(activity instanceof BaseActivity) {
            ((BaseActivity) activity).showSnackbar(view, message);
        }
    }
    @Deprecated
    @Override
    public void startActivity(Intent intent) {
        Log.d("BaseFragment", "StartActivity is forbidden from Fagments");
    }
    @Deprecated
    @Override
    public void startActivity(Intent intent, @Nullable Bundle options) {
        Log.d("BaseFragment", "StartActivity is forbidden from Fagments");
    }
    @Override
    public void onDestroyView() {
        if(viewUnbinder != null) {
            viewUnbinder.unbind();
        }
        super.onDestroyView();
    }
}
