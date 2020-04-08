package com.etologic.mahjongscoring2.app.utils

import android.content.Context.INPUT_METHOD_SERVICE
import android.view.View
import android.view.inputmethod.InputMethodManager

object KeyboardUtils {
    
    fun showKeyboard(viewInputMethod: View) {
        val imm = viewInputMethod.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS)
    }
    
    fun hideKeyboard(viewInputMethod: View) {
        val imm = viewInputMethod.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(viewInputMethod.windowToken, 0)
    }
}
