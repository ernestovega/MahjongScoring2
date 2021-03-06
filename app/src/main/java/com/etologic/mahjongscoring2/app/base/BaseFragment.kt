/*
    Copyright (C) 2020  Ernesto Vega de la Iglesia Soria
 
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
/*
    Copyright (C) 2020  Ernesto Vega de la Iglesia Soria
 
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.etologic.mahjongscoring2.app.base

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import dagger.android.support.DaggerFragment

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
    
    protected fun showMessage(message: String) {
        val activity = activity
        if (activity is BaseActivity) activity.showMessage(message)
    }
    
    protected fun showSnackbar(view: View, message: String) {
        val activity = activity
        if (activity is BaseActivity) activity.showSnackbar(view, message)
    }
}
