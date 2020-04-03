package com.etologic.mahjongscoring2.app.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.etologic.mahjongscoring2.R
import kotlinx.android.synthetic.main.custom_num_pad.view.*

class CustomNumPad(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet) {
    
    init {
        LayoutInflater.from(context).inflate(R.layout.custom_num_pad, this, true)
        setListeners()
    }
    
    private fun setListeners() {
        btCustomNumPad1?.setOnClickListener { etCustomNumPadDisplay?.setText(String.format("%s1", getDisplayNum())) }
        btCustomNumPad2?.setOnClickListener { etCustomNumPadDisplay?.setText(String.format("%s2", getDisplayNum())) }
        btCustomNumPad3?.setOnClickListener { etCustomNumPadDisplay?.setText(String.format("%s3", getDisplayNum())) }
        btCustomNumPad4?.setOnClickListener { etCustomNumPadDisplay?.setText(String.format("%s4", getDisplayNum())) }
        btCustomNumPad5?.setOnClickListener { etCustomNumPadDisplay?.setText(String.format("%s5", getDisplayNum())) }
        btCustomNumPad6?.setOnClickListener { etCustomNumPadDisplay?.setText(String.format("%s6", getDisplayNum())) }
        btCustomNumPad7?.setOnClickListener { etCustomNumPadDisplay?.setText(String.format("%s7", getDisplayNum())) }
        btCustomNumPad8?.setOnClickListener { etCustomNumPadDisplay?.setText(String.format("%s8", getDisplayNum())) }
        btCustomNumPad9?.setOnClickListener { etCustomNumPadDisplay?.setText(String.format("%s9", getDisplayNum())) }
        btCustomNumPad0?.setOnClickListener { etCustomNumPadDisplay?.setText(String.format("%s0", getDisplayNum())) }
        ibCustomNumPadBackspace?.setOnClickListener {
            if (getDisplayNum().isNotEmpty())
                etCustomNumPadDisplay?.setText(getDisplayNum().substring(0, getDisplayNum().count() - 1))
        }
    }
    
    private fun getDisplayNum(): String = (etCustomNumPadDisplay?.text ?: "").toString()
    
    internal fun setHint(points: Int) {
        etCustomNumPadDisplay?.hint = points.toString()
    }
    
    internal fun getPoints(): Int? =
        try {
            getDisplayNum().toInt()
        } catch (e: Exception) {
            null
        }
    
    internal fun setError() {
        etCustomNumPadDisplay?.error = ""
    }
}