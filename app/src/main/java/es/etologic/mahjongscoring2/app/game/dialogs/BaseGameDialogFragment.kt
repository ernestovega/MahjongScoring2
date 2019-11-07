package es.etologic.mahjongscoring2.app.game.dialogs

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import es.etologic.mahjongscoring2.app.base.BaseDialogFragment
import es.etologic.mahjongscoring2.app.game.activity.GameActivityViewModel
import es.etologic.mahjongscoring2.app.game.activity.GameActivityViewModelFactory
import javax.inject.Inject

internal abstract class BaseGameDialogFragment : BaseDialogFragment() {
    
    @Inject
    internal lateinit var activityViewModelFactory: GameActivityViewModelFactory
    protected lateinit var activityViewModel: GameActivityViewModel
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        if (activity == null || !isAdded)
            dismiss()
        
        activityViewModel = ViewModelProviders.of(activity!!, activityViewModelFactory).get(GameActivityViewModel::class.java)
    }
}
