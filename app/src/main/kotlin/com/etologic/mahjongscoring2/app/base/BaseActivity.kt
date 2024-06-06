/*
 *     Copyright © 2023  Ernesto Vega de la Iglesia Soria
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.etologic.mahjongscoring2.app.base

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import androidx.activity.addCallback
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.R.anim.enter_from_left
import com.etologic.mahjongscoring2.R.anim.enter_from_right
import com.etologic.mahjongscoring2.R.anim.exit_to_left
import com.etologic.mahjongscoring2.R.anim.exit_to_right
import com.etologic.mahjongscoring2.app.utils.LanguageHelper
import com.etologic.mahjongscoring2.app.utils.setLocale
import javax.inject.Inject


@SuppressLint("Registered")
abstract class BaseActivity : AppCompatActivity() {

    abstract val onBackBehaviour: () -> Unit

    @Inject
    lateinit var languageHelper: LanguageHelper
    private var currentLanguage: String? = null

    private fun setOnBackPressedCallback() {
        onBackPressedDispatcher.addCallback(this) { onBackBehaviour.invoke() }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setOnBackPressedCallback()
    }

    @Suppress("unused")
    fun goToFragment(@IdRes frameLayoutContainer: Int, fragment: BaseFragment, tag: String) {
        supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(enter_from_left, exit_to_right, enter_from_right, exit_to_left)
            replace(frameLayoutContainer, fragment, tag)
            commit()
        }
    }

    fun showError(throwable: Throwable?) {
        if (throwable == null) getString(R.string.ups_something_wrong)
        else throwable.message?.let { showMessage(it) }
    }

    private fun showMessage(message: String) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogStyleMM)
        builder.setMessage(message).show()
    }

    override fun onStart() {
        super.onStart()
        currentLanguage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales.get(0).language
        } else {
            @Suppress("DEPRECATION")
            resources.configuration.locale.language
        }
        val savedLanguage = languageHelper.currentLanguage

        if (currentLanguage != savedLanguage) {
            setLocale(savedLanguage)
            currentLanguage = savedLanguage
            recreate()
        }
    }
}
