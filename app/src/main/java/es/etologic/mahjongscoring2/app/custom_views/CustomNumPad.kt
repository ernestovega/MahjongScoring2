package es.etologic.mahjongscoring2.app.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import es.etologic.mahjongscoring2.R
import kotlinx.android.synthetic.main.custom_num_pad.view.*

class CustomNumPad(context: Context, attributeSet: AttributeSet) : LinearLayout(context) {
    
    init {
        LayoutInflater.from(context).inflate(R.layout.custom_num_pad, this, true)
        setListeners()
    }
    
    private fun setListeners() {
        btCustomNumPad1?.setOnClickListener { tvCustomNumPadDisplay?.text = String.format("%s1", getDisplayNum()) }
        btCustomNumPad2?.setOnClickListener { tvCustomNumPadDisplay?.text = String.format("%s2", getDisplayNum()) }
        btCustomNumPad3?.setOnClickListener { tvCustomNumPadDisplay?.text = String.format("%s3", getDisplayNum()) }
        btCustomNumPad4?.setOnClickListener { tvCustomNumPadDisplay?.text = String.format("%s4", getDisplayNum()) }
        btCustomNumPad5?.setOnClickListener { tvCustomNumPadDisplay?.text = String.format("%s5", getDisplayNum()) }
        btCustomNumPad6?.setOnClickListener { tvCustomNumPadDisplay?.text = String.format("%s6", getDisplayNum()) }
        btCustomNumPad7?.setOnClickListener { tvCustomNumPadDisplay?.text = String.format("%s7", getDisplayNum()) }
        btCustomNumPad8?.setOnClickListener { tvCustomNumPadDisplay?.text = String.format("%s8", getDisplayNum()) }
        btCustomNumPad9?.setOnClickListener { tvCustomNumPadDisplay?.text = String.format("%s9", getDisplayNum()) }
        btCustomNumPad0?.setOnClickListener { tvCustomNumPadDisplay?.text = String.format("%s0", getDisplayNum()) }
        btCustomNumPadDelete?.setOnClickListener { tvCustomNumPadDisplay?.text = "" }
        btCustomNumPadBackspace?.setOnClickListener {
            if (getDisplayNum().isNotEmpty())
                tvCustomNumPadDisplay?.text = getDisplayNum().substring(0, getDisplayNum().count() - 1)
        }
    }
    
    private fun getDisplayNum(): String = (tvCustomNumPadDisplay?.text ?: "").toString()
    
    fun getPoints(): Int? =
        try { getDisplayNum().toInt() }
        catch (e: Exception) { null }
}