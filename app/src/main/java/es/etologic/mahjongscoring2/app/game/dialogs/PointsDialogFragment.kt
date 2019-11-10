package es.etologic.mahjongscoring2.app.game.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import es.etologic.mahjongscoring2.R
import es.etologic.mahjongscoring2.app.game.base.BaseGameDialogFragment
import es.etologic.mahjongscoring2.app.model.HandActions.HU_RON
import es.etologic.mahjongscoring2.app.utils.KeyboardUtils.showKeyboard
import kotlinx.android.synthetic.main.custom_num_pad.*
import kotlinx.android.synthetic.main.points_dialog_fragment.*

internal class PointsDialogFragment : BaseGameDialogFragment() {
    
    companion object {
        const val TAG = "PointsDialogFragment"
        private const val MCR_MIN_POINTS = 8
    }
    
    override fun onDismiss(dialog: DialogInterface) {
        tietPointsDialog.setText("")
        super.onDismiss(dialog)
    }
    
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.points_dialog_fragment, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false
        setOnClickListeners()
        tietPointsDialog?.selectAll()
        tietPointsDialog?.requestFocus()
        showKeyboard(view)
    }
    
    private fun setOnClickListeners() {
        btCustomNumPad1?.setOnClickListener {}
        btCustomNumPad2?.setOnClickListener {}
        btCustomNumPad3?.setOnClickListener {}
        btCustomNumPad4?.setOnClickListener {}
        btCustomNumPad5?.setOnClickListener {}
        btCustomNumPad6?.setOnClickListener {}
        btCustomNumPad7?.setOnClickListener {}
        btCustomNumPad8?.setOnClickListener {}
        btCustomNumPad9?.setOnClickListener {}
        btCustomNumPad0?.setOnClickListener {}
        btCustomNumPadBack?.setOnClickListener {}
        btCustomNumPadOk?.setOnClickListener {
            val handPoints = tietPointsDialog.text.toString().toInt()
            if (handPoints >= MCR_MIN_POINTS)
                if(activityViewModel.getSelectedHandAction() == HU_RON)
                    activityViewModel.saveRonRound(handPoints)
                else
                    activityViewModel.saveTsumoRound(handPoints)
        }
    }
}