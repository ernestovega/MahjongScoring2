package es.etologic.mahjongscoring2.app.game.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import es.etologic.mahjongscoring2.R
import es.etologic.mahjongscoring2.app.game.base.BaseGameDialogFragment
import es.etologic.mahjongscoring2.app.model.GamePages
import kotlinx.android.synthetic.main.game_table_ranking_dialog_fragment.*

internal class RankingDialogFragment : BaseGameDialogFragment() {
    
    companion object {
        const val TAG = "RankingDialogFragment"
    }
    
    //LIFECYCLE
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.game_table_ranking_dialog_fragment, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false
        fillRankingViews()
        setOnClickListeners()
    }
    
    private fun setOnClickListeners() {
        btRankingDialogSeeList?.setOnClickListener {
            activityViewModel.setCurrentGameViewPagerPage(GamePages.LIST)
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
