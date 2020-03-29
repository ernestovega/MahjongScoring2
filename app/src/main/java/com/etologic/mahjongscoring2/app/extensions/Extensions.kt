package com.etologic.mahjongscoring2.app.extensions

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.SystemClock
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.tabs.TabLayout

//ON CLICK LISTENER
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

internal fun View.setOnSecureClickListener(
    listener: OnClickListener?,
    debounceTime: Long = 600L
) {
    this.setOnClickListener(object : OnClickListener {
        private var lastClickTime: Long = 0
        
        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            else listener?.onClick(v)
            
            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}

//TAB LAYOUT
internal fun TabLayout.Tab.getTextView(): TextView {
    val tabLayout = (parent?.getChildAt(0) as ViewGroup).getChildAt(position) as LinearLayout
    return tabLayout.getChildAt(1) as TextView
}

//VIEW ANIMATIONS
internal fun View.collapse(duration: Int = 0) {
    val initialHeight = measuredHeight
    val va = ValueAnimator.ofInt(initialHeight, 0)
    va.addUpdateListener { animation ->
        layoutParams.height = animation.animatedValue as Int
        requestLayout()
    }
    va.addListener(object : Animator.AnimatorListener {
        override fun onAnimationEnd(animation: Animator) {
            visibility = View.GONE
        }
        
        override fun onAnimationStart(animation: Animator) {}
        override fun onAnimationCancel(animation: Animator) {}
        override fun onAnimationRepeat(animation: Animator) {}
    })
    va.duration = duration.toLong()
    va.interpolator = DecelerateInterpolator()
    va.start()
}

internal fun View.expand(duration: Int = 0) {
    measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    val targetHeight = measuredHeight
    
    layoutParams.height = 1
    visibility = View.VISIBLE
    val va = ValueAnimator.ofInt(1, targetHeight)
    va.addUpdateListener { animation ->
        layoutParams.height = animation.animatedValue as Int
        requestLayout()
    }
    va.addListener(object : Animator.AnimatorListener {
        override fun onAnimationEnd(animation: Animator) {
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        
        override fun onAnimationStart(animation: Animator) {}
        override fun onAnimationCancel(animation: Animator) {}
        override fun onAnimationRepeat(animation: Animator) {}
    })
    va.duration = duration.toLong()
    va.interpolator = OvershootInterpolator()
    va.start()
}

internal fun TextView.expandTextView(numLines: Int) {
    val animation = ObjectAnimator.ofInt(this, "maxLines", numLines)
    animation.setDuration(200).start()
}

internal fun TextView.collapseTextView(numLines: Int) {
    val animation = ObjectAnimator.ofInt(this, "maxLines", numLines)
    animation.setDuration(200).start()
}