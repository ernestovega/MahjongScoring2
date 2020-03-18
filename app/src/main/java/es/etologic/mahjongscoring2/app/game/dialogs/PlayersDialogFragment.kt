package es.etologic.mahjongscoring2.app.game.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import es.etologic.mahjongscoring2.BuildConfig
import es.etologic.mahjongscoring2.R
import es.etologic.mahjongscoring2.app.game.base.BaseGameDialogFragment
import es.etologic.mahjongscoring2.app.utils.KeyboardUtils.hideKeyboard
import es.etologic.mahjongscoring2.app.utils.KeyboardUtils.showKeyboard
import es.etologic.mahjongscoring2.app.utils.StringUtils.isEmpty
import kotlinx.android.synthetic.main.players_dialog_fragment.*

internal class PlayersDialogFragment : BaseGameDialogFragment() {
    
    companion object {
        const val TAG = "PlayersDialogFragment"
    }
    
    private var names: Array<String> = if(BuildConfig.DEBUG) arrayOf("Player 1", "Player 2", "Player 3", "Player 4") else arrayOf("","","","")
    
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.players_dialog_fragment, container, false)
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
        tietPlayersDialogEast?.setText(names[0])
        tietPlayersDialogSouth?.setText(names[1])
        tietPlayersDialogWest?.setText(names[2])
        tietPlayersDialogNorth?.setText(names[3])
    }
    
    private fun setOnClickListeners() {
        tietPlayersDialogEast.setOnFocusChangeListener { _, isFocused -> if (isFocused) tietPlayersDialogEast.selectAll() }
        tietPlayersDialogSouth.setOnFocusChangeListener { _, isFocused -> if (isFocused) tietPlayersDialogSouth.selectAll() }
        tietPlayersDialogWest.setOnFocusChangeListener { _, isFocused -> if (isFocused) tietPlayersDialogWest.selectAll() }
        tietPlayersDialogNorth.setOnFocusChangeListener { _, isFocused -> if (isFocused) tietPlayersDialogNorth.selectAll() }
        btPlayersDialogCancel?.setOnClickListener {
            hideKeyboard(tietPlayersDialogEast)
            dismiss()
        }
        btPlayersDialogSave?.setOnClickListener {
            val name1 = (tietPlayersDialogEast?.text ?: names[0]).toString()
            val name2 = (tietPlayersDialogSouth?.text ?: names[1]).toString()
            val name3 = (tietPlayersDialogWest?.text ?: names[2]).toString()
            val name4 = (tietPlayersDialogNorth?.text ?: names[3]).toString()
            when {
                isEmpty(name1) -> tietPlayersDialogEast?.error = names[0]
                isEmpty(name2) -> tietPlayersDialogSouth?.error = names[1]
                isEmpty(name3) -> tietPlayersDialogWest?.error = names[2]
                isEmpty(name4) -> tietPlayersDialogNorth?.error = names[3]
                else -> {
                    hideKeyboard(tietPlayersDialogEast)
                    dismiss()
                }
            }
        }
    }
    
    internal fun setNames(newNames: Array<String>?) {
        if (newNames?.size == 4) this.names = newNames
    }
    
    override fun onDismiss(dialog: DialogInterface) {
        hideKeyboard(tietPlayersDialogEast)
        activityViewModel.savePlayersNames(
            names[0],
            names[1],
            names[2],
            names[3]
        )
        super.onDismiss(dialog)
    }
}
