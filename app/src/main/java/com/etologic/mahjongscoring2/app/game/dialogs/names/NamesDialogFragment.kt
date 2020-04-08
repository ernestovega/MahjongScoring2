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
package com.etologic.mahjongscoring2.app.game.dialogs.names

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.extensions.setOnSecureClickListener
import com.etologic.mahjongscoring2.app.game.base.BaseGameDialogFragment
import com.etologic.mahjongscoring2.app.utils.KeyboardUtils.hideKeyboard
import com.etologic.mahjongscoring2.app.utils.KeyboardUtils.showKeyboard
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.*
import kotlinx.android.synthetic.main.game_players_dialog_fragment.*

internal class NamesDialogFragment : BaseGameDialogFragment() {
    
    companion object {
        const val TAG = "NamesDialogFragment"
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.game_players_dialog_fragment, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
        printNames()
        tietPlayersDialogEast?.selectAll()
        tietPlayersDialogEast?.requestFocus()
        showKeyboard(view)
    }
    
    private fun printNames() {
        val names = activityViewModel?.getNamesByInitialPosition()
        names?.let {
            tietPlayersDialogEast?.setText(it[EAST.code])
            tietPlayersDialogSouth?.setText(it[SOUTH.code])
            tietPlayersDialogWest?.setText(it[WEST.code])
            tietPlayersDialogNorth?.setText(it[NORTH.code])
        }
    }
    
    private fun setOnClickListeners() {
        tietPlayersDialogEast?.setOnFocusChangeListener { _, isFocused -> if (isFocused) tietPlayersDialogEast?.selectAll() }
        tietPlayersDialogSouth?.setOnFocusChangeListener { _, isFocused -> if (isFocused) tietPlayersDialogSouth?.selectAll() }
        tietPlayersDialogWest?.setOnFocusChangeListener { _, isFocused -> if (isFocused) tietPlayersDialogWest?.selectAll() }
        tietPlayersDialogNorth?.setOnFocusChangeListener { _, isFocused -> if (isFocused) tietPlayersDialogNorth?.selectAll() }
        btPlayersDialogCancel?.setOnSecureClickListener { dismiss() }
        btPlayersDialogSave?.setOnSecureClickListener {
            activityViewModel?.savePlayersNames(
                arrayOf(
                    (tietPlayersDialogEast?.text ?: "").toString().trim(),
                    (tietPlayersDialogSouth?.text ?: "").toString().trim(),
                    (tietPlayersDialogWest?.text ?: "").toString().trim(),
                    (tietPlayersDialogNorth?.text ?: "").toString().trim()
                )
            )
            dismiss()
        }
    }
    
    override fun dismiss() {
        hideKeyboard(tietPlayersDialogEast)
        super.dismiss()
    }
    
    override fun onDismiss(dialog: DialogInterface) {
        hideKeyboard(tietPlayersDialogEast)
        super.onDismiss(dialog)
    }
}
