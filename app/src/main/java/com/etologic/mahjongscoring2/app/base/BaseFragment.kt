package com.etologic.mahjongscoring2.app.base

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.etologic.mahjongscoring2.app.model.ShowState
import com.etologic.mahjongscoring2.app.model.ShowState.SHOW
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.main_activity.*

abstract class BaseFragment : DaggerFragment() {
    
    @Deprecated("StartActivity is forbidden from Fagments")
    override fun startActivity(intent: Intent) {
        Log.d("BaseFragment", "StartActivity is forbidden from Fagments")
    }
    
    @Deprecated("StartActivity is forbidden from Fagments")
    override fun startActivity(intent: Intent, options: Bundle?) {
        Log.d("BaseFragment", "StartActivity is forbidden from Fagments")
    }
    
    protected fun showError(throwable: Throwable) {
        val activity = activity
        if (activity is BaseActivity) activity.showError(throwable)
    }
    
    protected fun showMEssage(message: String) {
        val activity = activity
        if (activity is BaseActivity) activity.showMessage(message)
    }
    
    protected fun showSnackbar(view: View, message: String) {
        val activity = activity
        if (activity is BaseActivity) activity.showSnackbar(view, message)
    }
    
    protected fun toggleActivityProgress(showState: ShowState) {
        val activity = activity
        if (activity is BaseActivity) activity.toggleProgress(showState)
    }
}
