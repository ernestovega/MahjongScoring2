package com.etologic.mahjongscoring2.app.extensions

import android.os.SystemClock
import android.view.View
import android.view.View.OnClickListener

internal fun View.setOnSecureClickListener(
    debounceTime: Long = 600L,
    action: (view: View) -> Unit
) {
    this.setOnClickListener(object : OnClickListener {
        private var lastClickTime: Long = 0
        
        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            else action(v)
            
            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}