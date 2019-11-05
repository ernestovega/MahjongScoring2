/*
 * Copyright © 2018 LaLiga.
 * All rights reserved.
 */

package es.etologic.mahjongscoring2.app.base

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerAppCompatActivity
import es.etologic.mahjongscoring2.R
import es.etologic.mahjongscoring2.app.model.ShowState
import es.etologic.mahjongscoring2.app.model.ShowState.SHOW
import kotlinx.android.synthetic.main.main_activity.*
import org.apache.commons.lang3.StringUtils

@SuppressLint("Registered")
open class BaseActivity : DaggerAppCompatActivity() {
    
    protected fun goToFragment(fragment: Fragment, @IdRes frameLayoutContainer: Int) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
        fragmentTransaction.replace(frameLayoutContainer, fragment)
        fragmentTransaction.commit()
    }
    
    protected fun goToActivity(intent: Intent, requestCode: Int) {
        startActivity/*ForResult*/(intent/*, requestCode*/)
    }
    
    internal fun showError(throwable: Throwable?) {
        if (throwable == null) getString(R.string.ups_something_wrong)
        else throwable.message?.let { showError(it) }
    }
    
    protected fun showError(message: String) {
        val builder = AlertDialog.Builder(this, R.style.AppTheme)
        builder.setMessage(message)
            .show()
    }
    
    internal fun showSnackbar(view: View, message: String) {
        val text = if (StringUtils.isBlank(message)) "" else message
        Snackbar.make(view, text, Snackbar.LENGTH_LONG)
            .show()
    }
    
    protected fun toggleProgress(showState: ShowState) {
        llProgress?.visibility = if (showState === SHOW) VISIBLE else GONE
    }
}
