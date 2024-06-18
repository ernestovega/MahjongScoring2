/*
 *     Copyright © 2024  Ernesto Vega de la Iglesia Soria
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

package com.etologic.mahjongscoring2.app.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.etologic.mahjongscoring2.databinding.CustomNumPadBinding

class CustomNumPad(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet) {

    private var _binding: CustomNumPadBinding? = null
    private val binding get() = _binding!!

    init {
        _binding = CustomNumPadBinding.inflate(LayoutInflater.from(context), this, true)
        setListeners()
    }

    private fun setListeners() {
        with(binding) {
            btCustomNumPad1.setOnClickListener { etCustomNumPadDisplay.setText(String.format("%s1", getDisplayNum())) }
            btCustomNumPad2.setOnClickListener { etCustomNumPadDisplay.setText(String.format("%s2", getDisplayNum())) }
            btCustomNumPad3.setOnClickListener { etCustomNumPadDisplay.setText(String.format("%s3", getDisplayNum())) }
            btCustomNumPad4.setOnClickListener { etCustomNumPadDisplay.setText(String.format("%s4", getDisplayNum())) }
            btCustomNumPad5.setOnClickListener { etCustomNumPadDisplay.setText(String.format("%s5", getDisplayNum())) }
            btCustomNumPad6.setOnClickListener { etCustomNumPadDisplay.setText(String.format("%s6", getDisplayNum())) }
            btCustomNumPad7.setOnClickListener { etCustomNumPadDisplay.setText(String.format("%s7", getDisplayNum())) }
            btCustomNumPad8.setOnClickListener { etCustomNumPadDisplay.setText(String.format("%s8", getDisplayNum())) }
            btCustomNumPad9.setOnClickListener { etCustomNumPadDisplay.setText(String.format("%s9", getDisplayNum())) }
            btCustomNumPad0.setOnClickListener { etCustomNumPadDisplay.setText(String.format("%s0", getDisplayNum())) }
            ibCustomNumPadBackspace.setOnClickListener {
                if (getDisplayNum().isNotEmpty()) {
                    etCustomNumPadDisplay.setText(getDisplayNum().substring(0, getDisplayNum().count() - 1))
                }
            }
        }
    }

    private fun getDisplayNum(): String = (binding.etCustomNumPadDisplay.text ?: "").toString()

    fun getPoints(): Int? =
        try {
            getDisplayNum().toInt()
        } catch (e: Exception) {
            null
        }

    fun setError() {
        binding.etCustomNumPadDisplay.error = ""
    }
}