package es.etologic.mahjongscoring2.app.game.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import es.etologic.mahjongscoring2.R
import es.etologic.mahjongscoring2.app.game.base.BaseGameDialogFragment
import es.etologic.mahjongscoring2.domain.model.enums.PlayerStates
import kotlinx.android.synthetic.main.hand_actions_dialog_fragment.*

internal class HandActionsDialogFragment : BaseGameDialogFragment() {
    
    companion object {
        const val TAG = "HandActionsDialogFragment"
    }
    
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.hand_actions_dialog_fragment, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = true
        setOnClickListeners()
    }
    
    private fun setOnClickListeners() {
        btHandActionsDialogHu?.setOnClickListener {  }
        btHandActionsDialogWashout?.setOnClickListener { /*viewModel.saveDrawRound()*/ }
        btHandActionsDialogPenalty?.setOnClickListener {  }
        if(activityViewModel.getSelectedPlayerState() == PlayerStates.PENALIZED) {
            btHandActionsDialogPenaltyCancel?.visibility = View.VISIBLE
            btHandActionsDialogPenaltyCancel?.setOnClickListener {  }
        }
    }
}