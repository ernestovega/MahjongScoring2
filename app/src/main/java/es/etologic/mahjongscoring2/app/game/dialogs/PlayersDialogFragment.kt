package es.etologic.mahjongscoring2.app.game.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import es.etologic.mahjongscoring2.R
import es.etologic.mahjongscoring2.app.game.base.BaseGameDialogFragment
import es.etologic.mahjongscoring2.app.model.DialogType.PLAYERS
import es.etologic.mahjongscoring2.app.utils.KeyboardUtils.hideKeyboard
import es.etologic.mahjongscoring2.app.utils.KeyboardUtils.showKeyboard
import es.etologic.mahjongscoring2.app.utils.StringUtils.isEmpty
import es.etologic.mahjongscoring2.domain.model.enums.TableWinds.*
import kotlinx.android.synthetic.main.players_dialog_fragment.*

internal class PlayersDialogFragment : BaseGameDialogFragment() {
    
    companion object {
        const val TAG = "PlayersDialogFragment"
    }
    
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
        tietPlayersDialogEast.selectAll()
        tietPlayersDialogEast.requestFocus()
        showKeyboard(view)
    }
    
    private fun setOnClickListeners() {
        btPlayersDialogSave?.setOnClickListener {
            val name1 = (tietPlayersDialogEast?.text ?: "Player1").toString()
            val name2 = (tietPlayersDialogSouth?.text ?: "Player2").toString()
            val name3 = (tietPlayersDialogWest?.text ?: "Player3").toString()
            val name4 = (tietPlayersDialogNorth?.text ?: "Player4").toString()
            if (!isEmpty(name1) && !isEmpty(name2) && !isEmpty(name3) && !isEmpty(name4)) {
                activityViewModel.savePlayersNames(name1, name2, name3, name4)
                hideKeyboard(tietPlayersDialogEast)
            } else
                activityViewModel.showDialog(PLAYERS)
        }
    }
    
    internal fun setNames(names: Array<String>?) {
        if (names?.size == 4) {
            tietPlayersDialogEast.setText(names[EAST.code])
            tietPlayersDialogSouth.setText(names[SOUTH.code])
            tietPlayersDialogWest.setText(names[WEST.code])
            tietPlayersDialogNorth.setText(names[NORTH.code])
        }
    }
}
