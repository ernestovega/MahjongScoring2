/*
 * Copyright © 2018 LaLiga.
 * All rights reserved.
 */

package com.etologic.mahjongscoring2.app.base

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.R.anim.*
import com.etologic.mahjongscoring2.app.model.ShowState
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG
import dagger.android.support.DaggerAppCompatActivity
import org.apache.commons.lang3.StringUtils

@SuppressLint("Registered")
abstract class BaseActivity : DaggerAppCompatActivity() {
    
    @Deprecated("Don' use", ReplaceWith("goToActivity(intent, requestCode)"))
    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
    }
    
    @Deprecated("Don' use", ReplaceWith("goToActivity(intent, requestCode)"))
    override fun startActivity(intent: Intent?, options: Bundle?) {
        super.startActivity(intent, options)
    }
    
    internal fun goToActivity(intent: Intent, requestCode: Int) {
        startActivityForResult(intent, requestCode)
    }

    internal fun goToFragment(@IdRes frameLayoutContainer: Int, fragment: BaseFragment, tag: String) {
        supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(enter_from_left, exit_to_right, enter_from_right, exit_to_left)
            replace(frameLayoutContainer, fragment, tag)
            commit()
        }
    }
    
    internal fun showError(throwable: Throwable?) {
        if (throwable == null) getString(R.string.ups_something_wrong)
        else throwable.message?.let { showMessage(it) }
    }
    
    internal fun showMessage(message: String) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogStyleMM)
        builder.setMessage(message).show()
    }
    
    internal fun showSnackbar(view: View?, message: String) {
        view?.let {
            val text = if (StringUtils.isBlank(message)) "" else message
            Snackbar.make(it, text, LENGTH_LONG).show()
        }
    }
    
    internal fun toggleProgress(showState: ShowState) {
//        llProgress?.visibility = if (showState === SHOW) VISIBLE else GONE
    }
}
