package es.etologic.mahjongscoring2.app.game.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import es.etologic.mahjongscoring2.R
import es.etologic.mahjongscoring2.app.game.base.BaseGameDialogFragment
import kotlinx.android.synthetic.main.points_dialog_fragment.*

internal class PointsDialogFragment : BaseGameDialogFragment() {
    
    companion object {
        const val TAG = "PointsDialogFragment"
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
    }
    
    private fun setOnClickListeners() {
        btPointsDialogCancel.setOnClickListener { dismiss() }
        btPointsDialogDraw.setOnClickListener { /*saveDrawRound()*/ }
        btPointsDialogOk.setOnClickListener {
//            cnpPointsDialog?.getPoints()?.let {
//                if (cdsPointsDialog?.isRonOrTsumo() == RON) saveRonRound(it)
//                else saveTsumoRound(it)
//            }
        }
    }
}