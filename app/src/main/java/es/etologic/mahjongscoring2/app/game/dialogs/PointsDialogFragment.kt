package es.etologic.mahjongscoring2.app.game.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import es.etologic.mahjongscoring2.R
import es.etologic.mahjongscoring2.app.game.base.BaseGameDialogFragment
import es.etologic.mahjongscoring2.app.utils.KeyboardUtils.hideKeyboard
import es.etologic.mahjongscoring2.app.utils.KeyboardUtils.showKeyboard
import kotlinx.android.synthetic.main.points_dialog_fragment.*

internal class PointsDialogFragment : BaseGameDialogFragment() {
    
    companion object {
        const val TAG = "PointsDialogFragment"
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
        btPointsDialogSave?.setOnClickListener {
            activityViewModel.onRequestHandPointsResponse(tietPointsDialog.text)
            hideKeyboard(tietPointsDialog)
        }
        btPointsDialogCancel?.setOnClickListener {
            activityViewModel.onRequestHandPointsCancel()
            hideKeyboard(tietPointsDialog)
        }
    }
}