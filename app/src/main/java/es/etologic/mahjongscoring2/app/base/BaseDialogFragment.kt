/*
 * Copyright © 2018 LaLiga.
 * All rights reserved.
 */

package es.etologic.mahjongscoring2.app.base

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import dagger.android.support.DaggerAppCompatDialogFragment

abstract class BaseDialogFragment : DaggerAppCompatDialogFragment() {
    
    protected fun showError(throwable: Throwable) {
        val activity = activity
        if (activity is BaseActivity) {
            activity.showError(throwable)
        }
    }
    
    protected fun showSnackbar(view: View, message: String) {
        val activity = activity
        if (activity is BaseActivity) {
            activity.showSnackbar(view, message)
        }
    }
    
    @Deprecated("")
    override fun startActivity(intent: Intent) {
        Log.d("BaseFragment", "StartActivity is forbidden from Fagments")
    }
    
    @Deprecated("")
    override fun startActivity(intent: Intent, options: Bundle?) {
        Log.d("BaseFragment", "StartActivity is forbidden from Fagments")
    }
}
