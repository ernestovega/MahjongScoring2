package com.etologic.mahjongscoring2.ui

import android.view.View
import android.view.ViewGroup
import android.widget.Checkable
import android.widget.CompoundButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Plain text dump of what the user can read on screen: every shown TextView (labels, buttons, inputs) in
 * view tree order, with its resource id name. Unlike a screenshot it is readable in a diff and survives
 * pixel level changes, so the same expectations can be checked against a Compose/KMP port (by text).
 */
object ScreenDump {

    /** Root views of every attached window, read from the framework's WindowManagerGlobal. */
    @Suppress("UNCHECKED_CAST")
    private fun rootViews(): List<View> {
        val globalClass = Class.forName("android.view.WindowManagerGlobal")
        val global = globalClass.getMethod("getInstance").invoke(null)
        val field = globalClass.getDeclaredField("mViews").apply { isAccessible = true }
        return (field.get(global) as List<View>).toList()
    }

    /** Every window currently attached (activity + dialogs + popups), in attach order. */
    fun allWindows(): String = buildString {
        rootViews().filter { it.isShown }.forEachIndexed { index, root ->
            appendLine("== window ${index + 1} (${root.javaClass.simpleName})")
            dumpTree(root, this)
        }
    }

    /** Only the topmost window (e.g. the dialog that is open). */
    fun topWindow(): String = buildString {
        val root = rootViews().last { it.isShown }
        dumpTree(root, this)
    }

    fun view(view: View): String = buildString { dumpTree(view, this) }

    /**
     * Dumps every item of a RecyclerView, scrolling to each one so off screen items are included too.
     * [settle] must run the main looper after each scroll.
     */
    fun recyclerItems(recyclerView: RecyclerView, settle: () -> Unit): String = buildString {
        val count = recyclerView.adapter?.itemCount ?: 0
        appendLine("items: $count")
        for (position in 0 until count) {
            recyclerView.scrollToPosition(position)
            settle()
            val holder = recyclerView.findViewHolderForAdapterPosition(position)
            appendLine("-- item ${position + 1}")
            if (holder == null) appendLine("  <not laid out>") else dumpTree(holder.itemView, this, ignoreShown = true)
        }
    }

    private fun dumpTree(view: View, out: StringBuilder, ignoreShown: Boolean = false) {
        if (!ignoreShown && !view.isShown) return
        if (ignoreShown && view.visibility != View.VISIBLE) return
        if (view is TextView) {
            val text = view.text?.toString().orEmpty()
            val extra = when {
                view is CompoundButton -> " [${if (view.isChecked) "checked" else "unchecked"}]"
                view is Checkable -> " [${if ((view as Checkable).isChecked) "checked" else "unchecked"}]"
                else -> ""
            }
            val disabled = if (!view.isEnabled) " [disabled]" else ""
            if (text.isNotBlank() || extra.isNotEmpty()) {
                out.appendLine("${idName(view)}: ${text.replace("\n", "\\n")}$extra$disabled")
            }
        }
        // A ViewPager2 is a RecyclerView of pages: its pages are part of the screen. Any other RecyclerView is a
        // data list, summarized here and dumped item by item with recyclerItems().
        val isDataList = view is RecyclerView && view.parent !is androidx.viewpager2.widget.ViewPager2
        if (isDataList) {
            out.appendLine("${idName(view)}: <list of ${(view as RecyclerView).adapter?.itemCount ?: 0} items>")
        } else if (view is ViewGroup) {
            for (i in 0 until view.childCount) dumpTree(view.getChildAt(i), out, ignoreShown)
        }
    }

    private fun idName(view: View): String =
        if (view.id == View.NO_ID) view.javaClass.simpleName
        else runCatching { view.resources.getResourceEntryName(view.id) }.getOrDefault(view.javaClass.simpleName)
}
