package es.etologic.mahjongscoring2.app.game.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import es.etologic.mahjongscoring2.R
import es.etologic.mahjongscoring2.app.game.activity.GameActivityViewModel
import es.etologic.mahjongscoring2.app.game.activity.GameActivityViewModelFactory
import es.etologic.mahjongscoring2.app.model.GamePages
import kotlinx.android.synthetic.main.game_table_ranking_dialog_fragment.*
import javax.inject.Inject

class GameRankingFragmentDialog : androidx.fragment.app.DialogFragment() {
    
    companion object {
        const val TAG = "GameRankingFragmentDialog"
    }
    
    @Inject internal lateinit var activityViewModelFactory: GameActivityViewModelFactory
    private lateinit var activityViewModel: GameActivityViewModel
    
    //LIFECYCLE
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.game_table_ranking_dialog_fragment, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false
        setupActivityViewModel()
        fillRankingViews()
        setOnClickListeners()
    }
    
    private fun setOnClickListeners() {
        btRankingDialogSeeList?.setOnClickListener {
            activityViewModel.setCurrentViewPagerPage(GamePages.LIST)
            dismiss()
        }
        btRankingDialogResume?.setOnClickListener {
            activityViewModel.resumeGame()
        }
        btRankingDialogClose?.setOnClickListener {
            dismiss()
        }
        btRankingDialogExit?.setOnClickListener {
            activityViewModel.onBackPressed()
            dismiss()
        }
    }
    
    private fun setupActivityViewModel() {
        activity?.let { activityViewModel = ViewModelProviders.of(it, activityViewModelFactory).get(GameActivityViewModel::class.java) }
            ?: run { dismiss() }
    }
    
    private fun fillRankingViews() {
        val rankingTable = activityViewModel.getRankingTable()
        rankingTable?.let {
            val playerFirst = it.sortedPlayersRankings[0]
            val playerSecond = it.sortedPlayersRankings[1]
            val playerThird = it.sortedPlayersRankings[2]
            val playerFourth = it.sortedPlayersRankings[3]
            
            tvNamePlayerFirst?.text = playerFirst.name
            tvNamePlayerSecond?.text = playerSecond.name
            tvNamePlayerThird?.text = playerThird.name
            tvNamePlayerFourth?.text = playerFourth.name
            
            tvPointsPlayerFirst?.text = playerFirst.points
            tvPointsPlayerSecond?.text = playerSecond.points
            tvPointsPlayerThird?.text = playerThird.points
            tvPointsPlayerFourth?.text = playerFourth.points
            
            tvScorePlayerFirst?.text = playerFirst.score
            tvScorePlayerSecond?.text = playerSecond.score
            tvScorePlayerThird?.text = playerThird.score
            tvScorePlayerFourth?.text = playerFourth.score
            
            tvBestHandPlayerPoints?.text = rankingTable.bestHandPlayerPoints
            
            tvNumRounds?.text = rankingTable.sNumRounds
//            tvDuration?.setText(rankingTable.getDuration());
            if (rankingTable.numRounds < 16) {
                btRankingDialogResume?.visibility = View.VISIBLE
                btRankingDialogResume?.setOnClickListener { activityViewModel.resumeGame() }
            } else {
                btRankingDialogResume?.visibility = View.GONE
            }
        }
    }
}
