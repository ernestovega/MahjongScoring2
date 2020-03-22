package com.etologic.mahjongscoring2.app.game.dialogs.players

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.game.base.BaseGameDialogFragment
import com.etologic.mahjongscoring2.app.utils.KeyboardUtils.hideKeyboard
import com.etologic.mahjongscoring2.app.utils.KeyboardUtils.showKeyboard
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.*
import kotlinx.android.synthetic.main.game_players_dialog_fragment.*

internal class PlayersDialogFragment : BaseGameDialogFragment() {
    
    companion object {
        const val TAG = "PlayersDialogFragment"
    }
    
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.game_players_dialog_fragment, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
        printNames()
        tietPlayersDialogEast.selectAll()
        tietPlayersDialogEast.requestFocus()
        showKeyboard(view)
    }
    
    private fun printNames() {
        val names = activityViewModel?.getListNames()?.value
        names?. let {
            tietPlayersDialogEast?.setText(it[EAST.code])
            tietPlayersDialogSouth?.setText(it[SOUTH.code])
            tietPlayersDialogWest?.setText(it[WEST.code])
            tietPlayersDialogNorth?.setText(it[NORTH.code])
        }
    }
    
    private fun setOnClickListeners() {
        tietPlayersDialogEast.setOnFocusChangeListener { _, isFocused -> if (isFocused) tietPlayersDialogEast.selectAll() }
        tietPlayersDialogSouth.setOnFocusChangeListener { _, isFocused -> if (isFocused) tietPlayersDialogSouth.selectAll() }
        tietPlayersDialogWest.setOnFocusChangeListener { _, isFocused -> if (isFocused) tietPlayersDialogWest.selectAll() }
        tietPlayersDialogNorth.setOnFocusChangeListener { _, isFocused -> if (isFocused) tietPlayersDialogNorth.selectAll() }
        btPlayersDialogCancel?.setOnClickListener { dismiss() }
        btPlayersDialogSave?.setOnClickListener { dismiss() }
    }
    
    override fun dismiss() {
        hideKeyboard(tietPlayersDialogEast)
    }
    
    override fun onDismiss(dialog: DialogInterface) {
        hideKeyboard(tietPlayersDialogEast)
        activityViewModel?.savePlayersNames(
            arrayOf(
                (tietPlayersDialogEast?.text ?: "").toString().trim(),
                (tietPlayersDialogSouth?.text ?: "").toString().trim(),
                (tietPlayersDialogWest?.text ?: "").toString().trim(),
                (tietPlayersDialogNorth?.text ?: "").toString().trim()
            )
        )
        super.onDismiss(dialog)
    }
}
