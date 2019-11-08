package es.etologic.mahjongscoring2.app.game.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import es.etologic.mahjongscoring2.R
import es.etologic.mahjongscoring2.app.game.base.BaseGameDialogFragment
import es.etologic.mahjongscoring2.app.utils.KeyboardUtils.hideKeyboard
import es.etologic.mahjongscoring2.app.utils.KeyboardUtils.showKeyboard
import kotlinx.android.synthetic.main.penalty_dialog_fragment.*

internal class PenaltyDialogFragment : BaseGameDialogFragment() {
    
    companion object {
        const val TAG = "PointsDialogFragment"
    }
    
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.penalty_dialog_fragment, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
        tietPenaltyDialog?.selectAll()
        tietPenaltyDialog?.requestFocus()
        showKeyboard(view)
    }
    
    private fun setOnClickListeners() {
        btPenaltyDialogSave?.setOnClickListener {
            activityViewModel.onRequestPenaltyResponse(tietPenaltyDialog.text, cbPenaltyDialog.isChecked)
            hideKeyboard(tietPenaltyDialog)
        }
        btPenaltyDialogCancel?.setOnClickListener {
            activityViewModel.onRequestPenaltyCancel()
            hideKeyboard(tietPenaltyDialog)
        }
    }
}